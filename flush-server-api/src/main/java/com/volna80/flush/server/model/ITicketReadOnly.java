package com.volna80.flush.server.model;

import com.volna80.betfair.api.model.CurrentOrderSummary;
import com.volna80.betfair.api.model.Side;

/**
 * All methods which can return the current ticket state and can not modify it
 * <p>
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public interface ITicketReadOnly {
    /**
     * @return true if an order is in a final state
     */
    default boolean isCompleted() {
        return getStatus() == Status.CANCELED || getStatus() == Status.FILLED || getStatus() == Status.REJECTED;
    }

    default boolean isNotCompleted() {
        return !isCompleted();
    }

    default boolean isCancelable() {
        return isNotCompleted() && getStatus() != Status.CANCELLING;
    }

    Ticket.Status getStatus();

    int getPrice();

    int getOrderQty();

    int getCumQty();

    int getLeavesQty();

    String getBetId();

    String getCorrelationId();

    String getMarketId();

    long getSellectionId();

    Side getSide();

    /**
     * @return creation timestamp
     */
    long getTimestamp();

    CurrentOrderSummary toSummary();

    default boolean isExecutable() {
        return getStatus() == Status.WORKING || getStatus() == Status.CANCELLING;
    }

    ;

    default boolean isUnconfirmed() {
        return getStatus() == Status.WAITING;
    }


    public enum Status {
        WAITING, WORKING, CANCELLING, CANCELED, FILLED, REJECTED
    }
}
