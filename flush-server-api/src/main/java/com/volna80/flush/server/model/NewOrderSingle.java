package com.volna80.flush.server.model;

import com.volna80.betfair.api.model.Side;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class NewOrderSingle implements IMessage {

    private String correlationId;

    private String marketId;
    private long selectionId;

    private Side side;
    private int price;
    private int orderQty;

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getMarketId() {
        return marketId;
    }

    public void setMarketId(String marketId) {
        this.marketId = marketId;
    }

    public long getSelectionId() {
        return selectionId;
    }

    public void setSelectionId(long selectionId) {
        this.selectionId = selectionId;
    }

    public Side getSide() {
        return side;
    }

    public void setSide(Side side) {
        this.side = side;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getOrderQty() {
        return orderQty;
    }

    public void setOrderQty(int orderQty) {
        this.orderQty = orderQty;
    }

    @Override
    public String toString() {
        return "NOS[" + correlationId + '|' + marketId + '|' + selectionId + '|' + side + '|' + price + '@' + orderQty + ']';
    }

    public NewOrderSingle copy() {
        NewOrderSingle nos = new NewOrderSingle();
        nos.setMarketId(marketId);
        nos.setOrderQty(orderQty);
        nos.setPrice(price);
        nos.setCorrelationId(correlationId);
        nos.setSide(side);
        nos.setSelectionId(selectionId);
        return nos;
    }
}
