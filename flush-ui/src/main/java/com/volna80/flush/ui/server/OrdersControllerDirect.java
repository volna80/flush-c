package com.volna80.flush.ui.server;

import com.google.common.collect.Lists;
import com.volna80.betfair.api.APINGException;
import com.volna80.betfair.api.BetfairException;
import com.volna80.betfair.api.IBetfairAPI;
import com.volna80.betfair.api.model.*;
import com.volna80.betfair.api.model.errors.ErrorCode;
import com.volna80.flush.server.IReferenceProvider;
import com.volna80.flush.server.latency.ILatencyRecorder;
import com.volna80.flush.server.latency.LatencyRecorderFactory;
import com.volna80.flush.ui.ApplicationManager;
import com.volna80.flush.ui.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class OrdersControllerDirect implements IOrdersController {

    //TODO move to configuration
    public static final int DONE_DELAY = 5;
    public static final int LEAVE_DELAY = 1;
    public static final int RECORD_COUNT = 25;
    private static final Logger log = LoggerFactory.getLogger(OrdersControllerDirect.class);
    private final IBetfairAPI betfairAPI;
    private final FlushAPI flushApI;
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(
            r -> new Thread(r, "flush-order-api")
    );
    private final ILatencyRecorder recorderOrderMiner = LatencyRecorderFactory.getRecorder("order-miner");
    private final ILatencyRecorder recorderOrderPlacer = LatencyRecorderFactory.getRecorder("order-placer");
    private IReferenceProvider referenceProvider;
    private int doneDelay = DONE_DELAY;
    private int leaveDelay = LEAVE_DELAY;
    private ScheduledFuture<?> refreshRemainingOrdersTask;
    private ScheduledFuture<?> refreshDoneOrdersTask;
    private volatile CurrentOrderSummaryReport remainingOrders;
    private Map<String, CurrentOrderSummary> completeOrders = new ConcurrentHashMap<>();
    private Map<String, CurrentOrderSummary> waitingOrders = new ConcurrentHashMap<>();
    private int doneStartRecord = 0;

    public OrdersControllerDirect(FlushAPI flushAPI, IBetfairAPI betfairAPI) {
        this.flushApI = flushAPI;
        this.betfairAPI = betfairAPI;
        this.referenceProvider = flushAPI;
    }

    public void setDoneDelay(int doneDelay) {
        this.doneDelay = doneDelay;
    }

    public void setLeaveDelay(int leaveDelay) {
        this.leaveDelay = leaveDelay;
    }

    public void init() {

        remainingOrders = new CurrentOrderSummaryReport();
        remainingOrders.setCurrentOrders(new ArrayList<>());


        refreshRemainingOrdersTask = executor.scheduleWithFixedDelay(this::refreshRemainingOrders, 0, leaveDelay, TimeUnit.SECONDS);
        refreshDoneOrdersTask = executor.scheduleWithFixedDelay(this::refreshDoneOrders, 0, doneDelay, TimeUnit.SECONDS);

    }

    private void refreshDoneOrders() {
        recorderOrderMiner.start();

        try {

            CurrentOrderSummaryReport list = betfairAPI.listCurrentOrders(null, null, OrderProjection.EXECUTION_COMPLETE, null, null, OrderBy.BY_BET, SortDir.EARLIEST_TO_LATEST, doneStartRecord, RECORD_COUNT);

            if (log.isTraceEnabled()) {
                log.trace("refreshDoneOrders: list" + list);
            }

            if (list == null) {
                return;
            }

            if (list.isMoreAvailable()) {
                doneStartRecord += RECORD_COUNT;
                log.debug("more done completeOrders are available. startRecord={}", doneStartRecord);
            } else {
                log.debug("no more completeOrders, startRecord={}", doneStartRecord);
                //no changes in done completeOrders, so no need to cycle
            }


            for (com.volna80.betfair.api.model.CurrentOrderSummary s : list.getCurrentOrders()) {
                completeOrders.put(s.getBetId(), s);
            }


        } catch (APINGException e) {
            handleException(e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ApplicationManager.getInstance().exit(e.getMessage(), true);
        } finally {
            recorderOrderMiner.stop();
        }
    }

    private void handleException(APINGException e) {
        ErrorCode code = e.getCode();

        if (code.isNotCritical()) {
            log.info("not critical error: {}", e);
        } else {
            ApplicationManager.getInstance().exit(e.getMessage(), true);
        }
    }


    private void refreshRemainingOrders() {
        recorderOrderMiner.start();

        try {
            CurrentOrderSummaryReport remainingOrders = betfairAPI.listCurrentOrders(null, null, OrderProjection.EXECUTABLE, null, null, OrderBy.BY_BET, SortDir.EARLIEST_TO_LATEST, 0, 1000);

            if (remainingOrders == null) {
                return;
            }

            //cycle throw all remaining completeOrders for changes
            if (remainingOrders.isMoreAvailable()) {
                log.debug("more alive remainingOrders are available.");
            }

            this.remainingOrders = remainingOrders;

        } catch (APINGException e) {
            handleException(e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ApplicationManager.getInstance().exit(e.getMessage(), true);
        } finally {
            recorderOrderMiner.stop();
        }
    }

    public Collection<CurrentOrderSummary> getCompleteOrders() {
        return completeOrders.values();
    }

    public Collection<CurrentOrderSummary> getExecutableOrders() {
        return remainingOrders.getCurrentOrders();
    }

    public Collection<CurrentOrderSummary> getUnconfirmedOrders() {
        return waitingOrders.values();
    }

    public void placeNewOrder(final String marketId, final long selectionId, final int price, final int size, final Side side, final String refId) {

        final long start = System.currentTimeMillis();

        final CurrentOrderSummary summary = new CurrentOrderSummary();
        summary.setSide(side);
        summary.setMarketId(marketId);
        summary.setSelectionId(selectionId);
        summary.setPersistenceType(PersistenceType.LAPSE);
        summary.setOrderType(OrderType.LIMIT);
        summary.setPriceSize(new PriceSize(price, size));
        summary.setSizeRemaining(size);
        summary.setStatus(OrderStatus.EXECUTABLE);
        summary.setBetId(refId);
        summary.setPlacedDate(new Date(start));

        waitingOrders.put(refId, summary);

        executor.submit(() -> {

            //TODO drain and merge all instructions from the queue

            recorderOrderPlacer.start(start);
            LimitOrder order = new LimitOrder();
            order.setPrice(summary.getPriceSize().getPrice());
            order.setSize(summary.getPriceSize().getSize());
            order.setPersistenceType(summary.getPersistenceType());

            PlaceInstruction instr = new PlaceInstruction();
            instr.setOrderType(summary.getOrderType());
            instr.setSide(summary.getSide());
            instr.setSelectionId(summary.getSelectionId());
            instr.setLimitOrder(order);

            try {
                log.info("placing new order : " + order);

                PlaceExecutionReport report = betfairAPI.placeOrders(summary.getMarketId(), Collections.singletonList(instr), summary.getBetId());

                if (report.getStatus() == ExecutionReportStatus.SUCCESS) {
                    log.info("the order has been sent. report=" + report);
                    remainingOrders.getCurrentOrders().add(summary);

                } else {
                    log.info("the order has been rejected. report=" + report);
                }


            } catch (BetfairException e) {
                log.error(e.getMessage(), e);
            } finally {
                waitingOrders.remove(refId);
                recorderOrderPlacer.stop();
            }

        });

    }

    @Override
    public void cancelOrders(String marketId) {
        cancelOrders(marketId, null);
    }

    @Override
    public void cancelOrders(final String marketId, final List<String> orders) {
        log.info("cancelOrders: market:{}, orders:{}", marketId, orders);
        final long start = System.currentTimeMillis();
        executor.submit(() -> {

            recorderOrderPlacer.start(start);

            final List<CancelInstruction> instructions;

            if (orders != null) {
                instructions = new ArrayList<>();

                orders.stream().forEach(betId -> {

                    CancelInstruction cancel = new CancelInstruction();
                    cancel.setBetId(betId);
                    cancel.setSizeReduction(null);
                    instructions.add(cancel);

                });
            } else {
                instructions = null;
            }

            try {
                CancelExecutionReport report = betfairAPI.cancelOrders(marketId, instructions, referenceProvider.nextRef());

                if (report.getStatus() == ExecutionReportStatus.SUCCESS) {
                    log.info("orders have been cancelled. " + report);
                } else {
                    log.error("could not cancel orders. " + report);
                }
            } catch (BetfairException e) {
                log.error(e.getMessage(), e);
            } finally {
                recorderOrderPlacer.stop();
            }

        });
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public List<Message> getLastMessages() {
        return flushApI.getOrdersController() != null ? flushApI.getOrdersController().getLastMessages() : Collections.EMPTY_LIST;
    }

    @Override
    public Collection<CurrentOrderSummary> getMatchedOrders(String markedId) {
        List<CurrentOrderSummary> result = Lists.newArrayList();
        result.addAll(
                getCompleteOrders().stream().filter(order -> order.getMarketId().equals(markedId)).filter(order -> order.getSizeMatched() > 0).collect(Collectors.toList())
        );
        result.addAll(
                getExecutableOrders().stream().filter(order -> order.getMarketId().equals(markedId)).filter(order -> order.getSizeMatched() > 0).collect(Collectors.toList())
        );
        return result;
    }

    public void shutdown() {

        executor.shutdown();
        if (refreshRemainingOrdersTask != null) {
            refreshRemainingOrdersTask.cancel(true);
        }
        if (refreshDoneOrdersTask != null) {
            refreshDoneOrdersTask.cancel(true);
        }

    }

    @Override
    public Collection<CurrentOrderSummary> getExecutableOrders(String marketId, long selectionId) {
        Collection<CurrentOrderSummary> all = getExecutableOrders();
        return filter(marketId, selectionId, all);
    }

    @Override
    public Collection<CurrentOrderSummary> getCancelableOrders(String marketId, long selectionId) {
        return getExecutableOrders(marketId, selectionId);
    }

    @Override
    public Collection<CurrentOrderSummary> getCancellingOrders(String marketId, long selectionId) {
        throw new UnsupportedOperationException();
    }

    private Collection<CurrentOrderSummary> filter(String marketId, long selectionId, Collection<CurrentOrderSummary> all) {
        return all.stream()
                .filter(summary -> summary.getMarketId().equals(marketId))
                .filter(summary -> summary.getSelectionId() == selectionId)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<CurrentOrderSummary> getUnconfirmedOrders(String marketId, long selectionId) {
        Collection<CurrentOrderSummary> all = getUnconfirmedOrders();

        return filter(marketId, selectionId, all);
    }
}
