package com.volna80.flush.server.model;

import com.volna80.betfair.api.model.ExecutionReportErrorCode;
import com.volna80.betfair.api.model.OrderStatus;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class ExecutionReport implements IMessage {

    private String correlationId;
    private String betId;
    private String marketId;
    private long selectionId;

    private OrderStatus status;

    private int price;
    private int orderQty;
    private int cumQty;
    private int leavesQty;
    private ExecType execType;

    private String text;
    private ExecutionReportErrorCode errorCode;

    public ExecutionReport() {
    }

    public ExecutionReport(ExecutionReport copy) {
        this.correlationId = copy.correlationId;
        this.betId = copy.betId;
        this.marketId = copy.marketId;
        this.status = copy.status;
        this.price = copy.price;
        this.orderQty = copy.orderQty;
        this.cumQty = copy.cumQty;
        this.leavesQty = copy.leavesQty;
        this.execType = copy.execType;
        this.text = copy.text;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public ExecutionReport setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
        return this;
    }

    public String getBetId() {
        return betId;
    }

    public ExecutionReport setBetId(String betId) {
        this.betId = betId;
        return this;
    }

    public String getMarketId() {
        return marketId;
    }

    public ExecutionReport setMarketId(String marketId) {
        this.marketId = marketId;
        return this;
    }

    public long getSelectionId() {
        return selectionId;
    }

    public ExecutionReport setSelectionId(long selectionId) {
        this.selectionId = selectionId;
        return this;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public ExecutionReport setStatus(OrderStatus status) {
        this.status = status;
        return this;
    }

    public int getPrice() {
        return price;
    }

    public ExecutionReport setPrice(int price) {
        this.price = price;
        return this;
    }

    public int getOrderQty() {
        return orderQty;
    }

    public ExecutionReport setOrderQty(int orderQty) {
        this.orderQty = orderQty;
        return this;
    }

    public int getCumQty() {
        return cumQty;
    }

    public ExecutionReport setCumQty(int cumQty) {
        this.cumQty = cumQty;
        return this;
    }

    public int getLeavesQty() {
        return leavesQty;
    }

    public ExecutionReport setLeavesQty(int leavesQty) {
        this.leavesQty = leavesQty;
        return this;
    }

    public ExecType getExecType() {
        return execType;
    }

    public ExecutionReport setExecType(ExecType execType) {
        this.execType = execType;
        return this;
    }

    public String getText() {
        return text;
    }

    public ExecutionReport setText(String text) {
        this.text = text;
        return this;
    }

    public ExecutionReportErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ExecutionReportErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExecutionReport that = (ExecutionReport) o;

        if (selectionId != that.selectionId) return false;
        if (price != that.price) return false;
        if (orderQty != that.orderQty) return false;
        if (cumQty != that.cumQty) return false;
        if (leavesQty != that.leavesQty) return false;
        if (!correlationId.equals(that.correlationId)) return false;
        if (betId != null ? !betId.equals(that.betId) : that.betId != null) return false;
        if (marketId != null ? !marketId.equals(that.marketId) : that.marketId != null) return false;
        if (status != that.status) return false;
        if (execType != that.execType) return false;
        if (errorCode != that.errorCode) return false;
        return !(text != null ? !text.equals(that.text) : that.text != null);

    }

    @Override
    public int hashCode() {
        return correlationId.hashCode();
    }

    @Override
    public String toString() {
        return "ER[" + correlationId + '|' + betId + '|' + status + '|' + execType + '|' + price + '@' + orderQty + '|' + leavesQty + '/' + cumQty
                + (text != null ? '|' + text : "")
                + (errorCode != null ? '|' + errorCode.name() : "")
                + ']';
    }


}
