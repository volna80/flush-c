package com.volna80.flush.server.model;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class OrderCancelRequest implements IMessage {

    private String correlationId;
    private String betId;
    private String marketId;
    private long selectionId;

    public OrderCancelRequest() {
    }


    public String getCorrelationId() {
        return correlationId;
    }

    public OrderCancelRequest setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
        return this;
    }

    public String getBetId() {
        return betId;
    }

    public OrderCancelRequest setBetId(String betId) {
        this.betId = betId;
        return this;
    }

    @Override
    public String toString() {
        return "CANCEL[" + correlationId + ']';
    }

    public String getMarketId() {
        return marketId;
    }

    public OrderCancelRequest setMarketId(String marketId) {
        this.marketId = marketId;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrderCancelRequest that = (OrderCancelRequest) o;

        if (!correlationId.equals(that.correlationId)) return false;
        if (betId != null ? !betId.equals(that.betId) : that.betId != null) return false;
        return !(marketId != null ? !marketId.equals(that.marketId) : that.marketId != null);

    }

    @Override
    public int hashCode() {
        return correlationId.hashCode();
    }

    public long getSelectionId() {
        return selectionId;
    }

    public void setSelectionId(long selectionId) {
        this.selectionId = selectionId;
    }
}
