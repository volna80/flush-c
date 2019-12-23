package com.volna80.flush.server.model;

import com.volna80.betfair.api.model.ExecutionReportErrorCode;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class OrderCancelReject implements IMessage {

    private String correlationId;
    private String marketId;
    private long selectionId;
    private String betId;

    private String reason;
    private ExecutionReportErrorCode errorCode;


    public String getCorrelationId() {
        return correlationId;
    }

    public OrderCancelReject setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
        return this;
    }

    public String getMarketId() {
        return marketId;
    }

    public OrderCancelReject setMarketId(String marketId) {
        this.marketId = marketId;
        return this;
    }

    public long getSelectionId() {
        return selectionId;
    }

    public OrderCancelReject setSelectionId(long selectionId) {
        this.selectionId = selectionId;
        return this;
    }

    @Override
    public String toString() {
        return "REJECT[" + correlationId + "|" + betId + "|" + reason + ']';
    }

    public String getBetId() {
        return betId;
    }

    public OrderCancelReject setBetId(String betId) {
        this.betId = betId;
        return this;
    }

    public String getReason() {
        return reason;
    }

    public OrderCancelReject setReason(String reason) {
        this.reason = reason;
        return this;
    }

    public ExecutionReportErrorCode getErrorCode() {
        return errorCode;
    }

    public OrderCancelReject setErrorCode(ExecutionReportErrorCode errorCode) {
        this.errorCode = errorCode;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrderCancelReject that = (OrderCancelReject) o;

        if (selectionId != that.selectionId) return false;
        if (!correlationId.equals(that.correlationId)) return false;
        if (marketId != null ? !marketId.equals(that.marketId) : that.marketId != null) return false;
        if (betId != null ? !betId.equals(that.betId) : that.betId != null) return false;
        if (reason != null ? !reason.equals(that.reason) : that.reason != null) return false;
        return errorCode == that.errorCode;

    }

    @Override
    public int hashCode() {
        return correlationId.hashCode();
    }
}
