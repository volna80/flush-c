package com.volna80.flush.ui.server;

import akka.actor.*;
import com.volna80.betfair.api.model.CurrentOrderSummary;
import com.volna80.betfair.api.model.Side;
import com.volna80.flush.server.model.*;
import com.volna80.flush.server.model.exception.UnknownOrderException;
import com.volna80.flush.ui.ApplicationManager;
import com.volna80.flush.ui.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.concurrent.duration.Duration;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class OrdersControllerRemote extends UntypedActor implements IOrdersController {

    private static final Logger logger = LoggerFactory.getLogger(OrdersControllerRemote.class);

    private final IDictionary dictionary;
    private final ActorRef remoteSession;

    private volatile boolean alive = false;
    private LinkedBlockingQueue<Message> messages = new LinkedBlockingQueue<>();
    private Map<String, Order> tickets = new ConcurrentHashMap<>();
    private SupervisorStrategy supervisorStrategy = new OneForOneStrategy(-1, Duration.create(0, TimeUnit.SECONDS),
            this::makeDecision);

    public OrdersControllerRemote(ActorRef remoteSession, IDictionary dictionary) {
        this.remoteSession = remoteSession;
        this.dictionary = dictionary;
    }

    public static Props props(ActorRef remoteSession, IDictionary dictionary) {
        return Props.create(OrdersControllerRemote.class, remoteSession, dictionary);
    }

    private SupervisorStrategy.Directive makeDecision(Throwable t) {
        logger.error("something happened with the controller. Reason : {}", t.getMessage(), t);
        SupervisorStrategy.Directive directive = SupervisorStrategy.defaultDecider().apply(t);
        logger.info("directive: " + directive);
        return directive;
    }

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return supervisorStrategy;
    }

    @Override
    public void preStart() throws Exception {
        logger.info("register controller " + this);
        ApplicationManager.getInstance().registerOrderController(this);
        alive = true;

    }

    @Override
    public void postStop() throws Exception {
        alive = false;
    }

    @Override
    public void onReceive(Object o) throws Exception {

        if (o instanceof ExecutionReport) {
            onExecutionReport((ExecutionReport) o);
        } else if (o instanceof OrderCancelReject) {
            //TODO
            messages.add(new Message(System.currentTimeMillis(), (OrderCancelReject) o, dictionary));
        } else {
            logger.info("onReceive() {}", o);
        }

    }

    private void onExecutionReport(ExecutionReport er) throws Exception {

        Order order = tickets.get(er.getCorrelationId());

        if (er.getExecType() == ExecType.NEW) {
            logger.info("onER: {}, latency:{} ms", er, System.currentTimeMillis() - order.ticket.getTimestamp());
        } else {
            logger.info("onER: {}", er);
        }


        if (order == null) {
            throw new UnknownOrderException(er.getCorrelationId());
        }

        order.strategy = getSender();
        order.ticket.onExecutionReport(er);
        //remember a sender

        if (order.ticket.isCompleted()) {
            //TODO we don't remove complete orders, memory leak?
        }

        messages.add(new Message(System.currentTimeMillis(), er, dictionary));
    }

    @Override
    public Collection<CurrentOrderSummary> getCompleteOrders() {
        return alive ? tickets.values().stream()
                .filter(order -> order.ticket.isCompleted())
                .map(order -> order.ticket.toSummary())
                .collect(Collectors.toList()) : Collections.EMPTY_LIST;
    }

    @Override
    public Collection<CurrentOrderSummary> getExecutableOrders() {
        return alive ? tickets.values().stream()
                .filter(order -> order.ticket.isExecutable())
                .map(order -> order.ticket.toSummary())
                .collect(Collectors.toList()) : Collections.EMPTY_LIST;
    }

    @Override
    public Collection<CurrentOrderSummary> getUnconfirmedOrders() {
        return alive ? tickets.values().stream().filter(order -> order.ticket.isUnconfirmed())
                .map(order -> order.ticket.toSummary()).collect(Collectors.toList()) : Collections.EMPTY_LIST;
    }

    @Override
    public Collection<CurrentOrderSummary> getCancelableOrders(String marketId, long selectionId) {
        List<CurrentOrderSummary> list = new ArrayList<>();
        list.addAll(getExecutableOrders(marketId, selectionId));
        list.addAll(getUnconfirmedOrders(marketId, selectionId));
        return list;
    }

    @Override
    public Collection<CurrentOrderSummary> getCancellingOrders(String marketId, long selectionId) {
        return alive ? tickets.values().stream()
                .filter(ticket -> ticket.ticket.getSellectionId() == selectionId)
                .filter(ticket -> ticket.ticket.getMarketId().equals(marketId))
                .filter(order -> order.ticket.getStatus() == ITicketReadOnly.Status.CANCELLING)
                .map(order -> order.ticket.toSummary()).collect(Collectors.toList()) : Collections.EMPTY_LIST;
    }

    @Override
    public Collection<CurrentOrderSummary> getExecutableOrders(String marketId, long selectionId) {
        return alive ? tickets.values().stream()
                .filter(ticket -> ticket.ticket.getSellectionId() == selectionId)
                .filter(ticket -> ticket.ticket.getMarketId().equals(marketId))
                .filter(ticket -> ticket.ticket.isExecutable())
                .map(ticket -> ticket.ticket.toSummary()).collect(Collectors.toList()) : Collections.EMPTY_LIST;
    }

    @Override
    public Collection<CurrentOrderSummary> getUnconfirmedOrders(String marketId, long selectionId) {
        return alive ? tickets.values().stream()
                .filter(ticket -> ticket.ticket.getSellectionId() == selectionId)
                .filter(ticket -> ticket.ticket.getMarketId().equals(marketId))
                .filter(ticket -> ticket.ticket.isUnconfirmed())
                .map(ticket -> ticket.ticket.toSummary()).collect(Collectors.toList()) : Collections.EMPTY_LIST;
    }

    @Override
    public void placeNewOrder(String marketId, long selectionId, int price, int size, Side side, String correlationId) {
        if (!alive) {
            return;
        }

        if (tickets.containsKey(correlationId)) {
            logger.error("duplicate correlationId:{}, exist:{}", correlationId, tickets.get(correlationId));
            return;
        }

        final NewOrderSingle nos = new NewOrderSingle();
        nos.setCorrelationId(correlationId);
        nos.setMarketId(marketId);
        nos.setSelectionId(selectionId);
        nos.setPrice(price);
        nos.setOrderQty(size);
        nos.setSide(side);

        Order order = new Order();
        order.ticket = new Ticket(nos);
        tickets.put(correlationId, order);

        logger.info("sending {} to {}", nos, remoteSession);

        messages.add(new Message(System.currentTimeMillis(), nos, dictionary));
        remoteSession.tell(nos, getSelf());
    }

    @Override
    public void cancelOrders(String marketId, List<String> orderIds) {

        if (!alive) {
            return;
        }

        for (String id : orderIds) {
            Order ticket = tickets.get(id);
            if (ticket == null) {
                logger.error("Unknown order id: " + id);
                continue;
            }
            if (ticket.ticket.isCancelable() && ticket.strategy != null) {
                OrderCancelRequest ocr = ticket.ticket.cancel();
                logger.info("cancelling a ticket {}", ticket);

                messages.add(new Message(System.currentTimeMillis(), ocr, dictionary));
                ticket.strategy.tell(ocr, getSelf());
            } else {
                logger.info("could not cancel an order {}", ticket);
            }
        }

    }

    @Override
    public void cancelOrders(String marketId) {
        if (!alive) {
            return;
        }

        for (Order ticket : tickets.values()) {
            if (ticket.ticket.isCancelable() && ticket.strategy != null) {
                OrderCancelRequest ocr = ticket.ticket.cancel();
                logger.info("cancelling a ticket {}", ticket);
                messages.add(new Message(System.currentTimeMillis(), ocr, dictionary));
                ticket.strategy.tell(ocr, getSelf());
            } else {
                logger.info("haven't got any response for {} yet", ticket);
            }
        }
    }

    @Override
    public boolean isActive() {
        return alive;
    }

    @Override
    public List<Message> getLastMessages() {
        List<Message> lastMessage = new ArrayList<>();
        messages.drainTo(lastMessage);
        return lastMessage;
    }

    @Override
    public Collection<CurrentOrderSummary> getMatchedOrders(String markedId) {
        //TODO
        throw new RuntimeException();
    }

    private static class Order {
        ITicket ticket;
        ActorRef strategy;

        @Override
        public String toString() {
            return ticket + ":" + strategy;
        }
    }
}
