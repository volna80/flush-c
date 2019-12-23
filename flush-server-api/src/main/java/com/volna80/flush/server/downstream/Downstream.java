package com.volna80.flush.server.downstream;

import akka.actor.ActorRef;
import akka.actor.Props;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.volna80.betfair.api.BetfairException;
import com.volna80.betfair.api.IBetfairAPI;
import com.volna80.betfair.api.model.*;
import com.volna80.flush.server.AbstractActor;
import com.volna80.flush.server.IReferenceProvider;
import com.volna80.flush.server.latency.ILatencyRecorder;
import com.volna80.flush.server.latency.LatencyRecorderFactory;
import com.volna80.flush.server.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.concurrent.duration.FiniteDuration;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * A downstream actor.
 * <p>
 * It is responsible for
 * <p>
 * 1) sending new orders to betfair
 * 2) monitor and sends execution reports of all submitted orders
 * 3) fails in case of any exceptions
 * <p>
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class Downstream extends AbstractActor {

    public static final int INTERVAL = 250;
    public static final int maxNumberOfAliveOrders = 20;
    public static final String TASK_REVIEW_ORDERS = "REVIEW_ORDERS";
    private static final Logger logger = LoggerFactory.getLogger(Downstream.class);
    private static final Logger incoming = LoggerFactory.getLogger("com.volna80.flush.server.downstream.D_IN");
    private static final Logger outgoing = LoggerFactory.getLogger("com.volna80.flush.server.downstream.D_OUT");
    private final IBetfairAPI api;

    private final String marketId;
    private final Set<String> marketIdSet;

    private final ILatencyRecorder recorderNOS = LatencyRecorderFactory.getRecorder("downstream-nos");
    private final ILatencyRecorder recorderOCR = LatencyRecorderFactory.getRecorder("downstream-ocr");
    private final ILatencyRecorder recorderOrders = LatencyRecorderFactory.getRecorder("downstream-review-orders");

    private final Map<String, Order> workingOrderByBetId = new HashMap<>();

    private final IReferenceProvider referenceProvider;
    private FiniteDuration interval;

    public Downstream(IBetfairAPI api, String marketId, IReferenceProvider refProvider) {
        logger.info("<> marketId:{}", marketId);
        this.api = api;
        this.marketId = marketId;
        this.marketIdSet = Sets.newHashSet(marketId);
        this.referenceProvider = refProvider;
    }

    public static Props props(IBetfairAPI api, String marketId, IReferenceProvider refProvider) {
        return Props.create(Downstream.class, api, Preconditions.checkNotNull(marketId), refProvider);
    }

    @Override
    public void preStart() throws Exception {

        logger.info("Starting " + this);

        interval = FiniteDuration.create(INTERVAL, TimeUnit.MILLISECONDS);

        scheduleReviewOrders();

        logger.info("Started", marketId);
    }

    private void scheduleReviewOrders() {
        getContext().system().scheduler().scheduleOnce(interval, getSelf(), new Task(TASK_REVIEW_ORDERS), getContext().system().dispatcher(), getSelf());
    }

    @Override
    public void postStop() throws Exception {
        //
        logger.info("stopping " + this);
        logger.info("stopped " + this);
    }

    public void reviewOrders() throws BetfairException {

        //reschedule it-self
        scheduleReviewOrders();

        if (workingOrderByBetId.size() == 0) {
            //nothing to do
            return;
        }

        recorderOrders.start();
        try {

            Set<String> betIds = workingOrderByBetId.keySet();

            CurrentOrderSummaryReport report = api.listCurrentOrders(betIds, marketIdSet, OrderProjection.ALL, null, null, OrderBy.BY_BET, SortDir.EARLIEST_TO_LATEST, 0, betIds.size());

            logger.debug("review orders: {}", report);

            report.getCurrentOrders()
                    .stream()
                    .forEach(summary -> {
                                Order order = workingOrderByBetId.get(summary.getBetId());

                                if (order.update(summary)) {
                                    ExecutionReport er = order.makeReport();
                                    outgoing.info(er.toString());
                                    order.upstream.tell(er, getSelf());
                                }

                                if (order.isComplete()) {
                                    workingOrderByBetId.remove(order.getBetId());
                                }
                            }
                    );


            if (betIds.size() != report.getCurrentOrders().size()) {

                Set<String> aliveBetIds = report.getCurrentOrders().stream().map(CurrentOrderSummary::getBetId).collect(Collectors.toSet());

                betIds = new HashSet<>(betIds);
                betIds.stream().filter(id -> !aliveBetIds.contains(id)).forEach(id -> {
                    logger.info("removing {} from alive orders", id);

                    Order order = workingOrderByBetId.get(id);
                    order.fullyCancelled();
                    workingOrderByBetId.remove(id);
                    ExecutionReport er = order.makeReport();
                    outgoing.info(er.toString());
                    order.upstream.tell(er, getSelf());

                });

            }


        } finally {
            recorderOrders.stop();
        }
    }

    @Override
    public void onOrderCancelRequest(OrderCancelRequest msg) {
        Preconditions.checkArgument(marketId.equals(msg.getMarketId()));

        final String betId = msg.getBetId();

        incoming.info(msg.toString());

        if (!workingOrderByBetId.containsKey(betId)) {
            OrderCancelReject reject = MessageConverter.makeReject(msg, "couldn't find an order in alive orders.");
            outgoing.info(reject.toString());
            getSender().tell(reject, getSelf());
            return;
        }

        recorderOCR.start();
        try {
            CancelInstruction instruction = new CancelInstruction();
            instruction.setBetId(betId);
            CancelExecutionReport report = api.cancelOrders(marketId, Collections.singletonList(instruction), referenceProvider.nextRef());
            logger.debug("cancel response: " + report);

            Order order = workingOrderByBetId.get(betId);

            if (report.getStatus() == ExecutionReportStatus.SUCCESS) {

                CancelInstructionReport cancelInstructionReport = report.getInstructionReports().get(0);

                if (order.nos.getOrderQty() == cancelInstructionReport.getSizeCancelled()) {
                    //fully cancelled
                    order.fullyCancelled();

                } else {
                    //ask a final report
                    CurrentOrderSummaryReport summaryReport = api.listCurrentOrders(Collections.singleton(betId), null, OrderProjection.ALL, null, null, OrderBy.BY_BET, null, 0, 10);
                    if (summaryReport.getCurrentOrders().size() != 1) {
                        throw new RuntimeException("Unexpected number of orders in the report. " + summaryReport);
                    }
                    order.update(summaryReport.getCurrentOrders().get(0));


                }

                workingOrderByBetId.remove(betId);
                ExecutionReport er = order.makeReport();
                outgoing.info(er.toString());
                order.upstream.tell(er, getSelf());

            } else {
                String reason = "market reject: " + report.getStatus() + " : " + report.getErrorCode();
                OrderCancelReject reject = MessageConverter.makeReject(msg, reason);
                reject.setErrorCode(report.getErrorCode());
                //TODO remove from working orders?
                order.upstream.tell(reject, getSelf());
            }
        } catch (BetfairException e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            recorderOCR.stop();
        }

    }

    @Override
    public void onNewOrderSingle(NewOrderSingle msg) {

        Preconditions.checkArgument(marketId.equals(msg.getMarketId()));

        incoming.info(msg.toString());

        //if aliveOrders > threshold, reject an order
        if (workingOrderByBetId.size() >= maxNumberOfAliveOrders) {
            ExecutionReport reject = MessageConverter.makeReject(msg, "excited max number of alive orders [" + maxNumberOfAliveOrders + "]", null);
            outgoing.info(reject.toString());
            getSender().tell(reject, getSelf());
            return;
        }
        //TODO circuit breakers

        recorderNOS.start();

        final Order o = new Order(msg, getSender());

        try {

            LimitOrder order = new LimitOrder();
            order.setPrice(msg.getPrice());
            order.setSize(msg.getOrderQty());
            order.setPersistenceType(PersistenceType.LAPSE);

            PlaceInstruction instr = new PlaceInstruction();
            instr.setOrderType(OrderType.LIMIT);
            instr.setSide(msg.getSide());
            instr.setSelectionId(msg.getSelectionId());
            instr.setLimitOrder(order);

            PlaceExecutionReport report = api.placeOrders(marketId, Collections.singletonList(instr), msg.getCorrelationId());

            if (report.getStatus() == ExecutionReportStatus.SUCCESS) {
                logger.debug("the order has been sent. report=" + report);


                //use cases:
                //1) an order is fully filled
                //2) order is alive, we have other fully filled orders by the same price/side
                CurrentOrderSummaryReport summary = api.listCurrentOrders(
                        null,
                        marketIdSet,
                        OrderProjection.ALL,
                        null,
                        null,
                        OrderBy.BY_BET,
                        SortDir.LATEST_TO_EARLIEST,
                        0,  //from record
                        maxNumberOfAliveOrders //record count
                );

                logger.debug("summary:{}", summary);

                Optional<CurrentOrderSummary> response;

                response = summary.getCurrentOrders().stream()
                        .filter(s -> s.getSide() == msg.getSide())
                        .filter(s -> s.getPriceSize().getPrice() == msg.getPrice() && s.getPriceSize().getSize() == msg.getOrderQty())
                        .filter(s -> !workingOrderByBetId.containsKey(s.getBetId()))
                        .findFirst();


                if (!response.isPresent()) {
                    throw new RuntimeException("could not find a bet id after submission of " + msg);
                }

                CurrentOrderSummary s = response.get();
                o.update(s);

                if (!o.isComplete()) {
                    //if active
                    workingOrderByBetId.put(o.getBetId(), o);
                }

                ExecutionReport er = o.makeReport();
                er.setExecType(ExecType.NEW);

                outgoing.info(er.toString());
                getSender().tell(er, getSelf());


            } else {
                logger.info("the order has been rejected. report=" + report);
                rejectNOS(o, report.getErrorCode() + "", report.getErrorCode());
            }


        } catch (BetfairException e) {
            logger.error("Unexpected exception during sending " + msg, e);
            throw new RuntimeException("Unexpected exception during sending " + msg, e);
        } finally {
            recorderNOS.stop();
        }
    }

    private void rejectNOS(Order o, String reason, ExecutionReportErrorCode errorCode) {
        ExecutionReport reject = MessageConverter.makeReject(o.nos, reason, errorCode);
        outgoing.info(reject.toString());
        o.upstream.tell(reject, getSelf());
    }

    @Override
    public void onTask(Task task) throws BetfairException {
        if (Objects.equals(task.getGoal(), TASK_REVIEW_ORDERS)) {
            reviewOrders();
        } else {
            unhandled(task);
        }
    }

    @Override
    public String toString() {
        return "Downstream[" + marketId + "]";
    }

    private static class Order {

        private final ActorRef upstream;
        private final NewOrderSingle nos;

        private CurrentOrderSummary summary;
        private ExecutionReport er;

        public Order(NewOrderSingle nos, ActorRef upstream) {
            this.nos = nos;
            this.upstream = upstream;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Order order = (Order) o;

            return !(nos != null ? !nos.getCorrelationId().equals(order.nos.getCorrelationId()) : order.nos != null);

        }

        @Override
        public int hashCode() {
            return nos != null ? nos.getCorrelationId().hashCode() : 0;
        }

        public boolean update(CurrentOrderSummary summary) {
            boolean result;

            if (this.summary == null) {
                this.summary = summary;
                this.er = null;
                return true;
            }

            result = summary.getSizeMatched() != this.summary.getSizeMatched();
            result |= summary.getSizeRemaining() != this.summary.getSizeRemaining();
            result |= summary.getPriceSize().getSize() != this.summary.getPriceSize().getSize();
            result |= summary.getStatus() != this.summary.getStatus();

            this.summary = summary;

            if (result) {
                er = null;
            }

            return result;
        }

        public void fullyCancelled() {
            summary.setSizeRemaining(0);
            summary.setStatus(OrderStatus.EXECUTION_COMPLETE);
            er = null;
        }


        public String getBetId() {
            return summary.getBetId();
        }

        public boolean isComplete() {
            return summary.getStatus() == OrderStatus.EXECUTION_COMPLETE;
        }

        public ExecutionReport makeReport() {
            if (er != null) {
                return er;
            }

            er = new ExecutionReport();
            er.setCorrelationId(nos.getCorrelationId());
            er.setSelectionId(nos.getSelectionId());
            er.setMarketId(nos.getMarketId());
            er.setBetId(summary.getBetId());

            er.setStatus(summary.getStatus());

            er.setLeavesQty(summary.getSizeRemaining());
            er.setOrderQty(summary.getPriceSize().getSize());
            er.setPrice(summary.getPriceSize().getPrice());
            er.setCumQty(summary.getSizeMatched());

            return er;
        }


    }
}
