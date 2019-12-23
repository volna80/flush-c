package com.volna80.flush.server.model;

import com.google.common.base.Preconditions;
import com.volna80.betfair.api.model.CurrentOrderSummary;
import com.volna80.betfair.api.model.OrderStatus;
import com.volna80.betfair.api.model.PriceSize;
import com.volna80.betfair.api.model.Side;

/**
 * An object which present a state of an order
 *
 * <p>
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class Ticket implements ITicket {

    //creation time
    private final long timestamp = System.currentTimeMillis();

    private final long selectionId;
    private final String marketId;
    private final int price;
    private final Side side;
    private final String correlationId;
    private Status status;
    private int orderQty;
    private int cumQty = 0;
    private int leavesQty;
    private String betId;
    private volatile CurrentOrderSummary summary;

    /**
     * Create new ticket from a nos message
     *
     * @param nos nos
     */
    public Ticket(NewOrderSingle nos) {
        status = Status.WAITING;

        price = nos.getPrice();
        orderQty = nos.getOrderQty();
        leavesQty = nos.getOrderQty();
        side = nos.getSide();
        selectionId = nos.getSelectionId();
        marketId = nos.getMarketId();
        correlationId = nos.getCorrelationId();

        updateCache();
    }

    @Override
    public OrderCancelRequest cancel() {
        Preconditions.checkState(isCancelable(), "we can't cancel a ticket " + this);

        OrderCancelRequest ocr = new OrderCancelRequest();
        ocr.setCorrelationId(correlationId);
        ocr.setBetId(betId);
        ocr.setMarketId(marketId);
        ocr.setSelectionId(selectionId);
        status = Status.CANCELLING;

        updateCache();

        return ocr;
    }

    @Override
    public void onExecutionReport(ExecutionReport er) {

        Preconditions.checkNotNull(er);
        Preconditions.checkArgument(er.getLeavesQty() >= 0, "incorrect message " + er);
        Preconditions.checkArgument(er.getOrderQty() >= er.getLeavesQty() + er.getCumQty(), "incorect message " + er);
        Preconditions.checkArgument(isNotCompleted(), "got an execution report in a final state: " + er);
        Preconditions.checkArgument(correlationId.equals(er.getCorrelationId()));
        Preconditions.checkArgument(marketId.equals(er.getMarketId()));
        Preconditions.checkArgument(selectionId == er.getSelectionId());

        this.betId = er.getBetId();
        this.leavesQty = er.getLeavesQty();
        this.orderQty = er.getOrderQty();
        this.cumQty = er.getCumQty();

        //calculate new status
        switch (er.getStatus()) {
            case EXECUTABLE:
                switch (status) {
                    case WAITING:
                        if (er.getExecType() == ExecType.PENDING_NEW) {
                            //it is just ack from a server component, ignore it
                            break;
                        }
                        status = Status.WORKING;
                        break;
                    case CANCELLING:
                    case WORKING:
                        break;

                }
                break;
            case EXECUTION_COMPLETE:
                if (getStatus() == Status.WAITING) {
                    status = Status.REJECTED;
                } else {
                    status = orderQty == cumQty ? Status.FILLED : Status.CANCELED;
                }
                break;
            default:
                throw new RuntimeException("Unknown execution report status: " + er);
        }

        updateCache();

    }

    private void updateCache() {
        CurrentOrderSummary summary = new CurrentOrderSummary();
        summary.setMarketId(getMarketId());
        summary.setSelectionId(getSellectionId());
        summary.setBetId(getCorrelationId());
        summary.setSide(getSide());
        summary.setStatus(isCompleted() ? OrderStatus.EXECUTION_COMPLETE : OrderStatus.EXECUTABLE);
        summary.setPriceSize(new PriceSize(getPrice(), getOrderQty()));
        summary.setSizeRemaining(getLeavesQty());
        summary.setSizeMatched(getCumQty());
        this.summary = summary;
    }

    @Override
    public CurrentOrderSummary toSummary() {
        return summary;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public int getPrice() {
        return price;
    }

    @Override
    public int getOrderQty() {
        return orderQty;
    }

    @Override
    public int getCumQty() {
        return cumQty;
    }

    @Override
    public int getLeavesQty() {
        return leavesQty;
    }

    @Override
    public String getBetId() {
        return betId;
    }

    @Override
    public String getCorrelationId() {
        return correlationId;
    }

    @Override
    public String getMarketId() {
        return marketId;
    }

    @Override
    public long getSellectionId() {
        return selectionId;
    }

    @Override
    public Side getSide() {
        return side;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "Ticket[" + correlationId + '|' + betId + '|' + status + '|' + side + '|' + price + '@' + orderQty + '|' + +leavesQty + '/' + cumQty + ']';
    }
}
