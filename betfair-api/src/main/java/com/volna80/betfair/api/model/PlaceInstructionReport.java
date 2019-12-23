package com.volna80.betfair.api.model;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class PlaceInstructionReport {

    private OrderType orderType;


    private long selectionId;

    /**
     * The handicap applied to the selection, if on an asian-style market.
     */
    private double handicap = Double.NaN;

    private Side side;

    /**
     * A simple exchange bet for immediate execution
     */
    private LimitOrder limitOrder;

    /**
     * Bets are matched if, and only if, the returned starting price is better than a specified price.
     * In the case of back bets, LOC bets are matched if the calculated starting price is greater than the specified price.
     * In the case of lay bets, LOC bets are matched if the starting price is less than the specified price.
     * If the specified limit is equal to the starting price, then it may be matched, partially matched,
     * or may not be matched at all, depending on how much is needed to balance all bets against each other (MOC, LOC and normal exchange bets)
     */
    private LimitOnCloseOrder limitOnCloseOrder;


    /**
     * Bets remain unmatched until the market is reconciled. They are matched and settled at a price that is representative of the market at the point the market is turned in-play.
     * The market is reconciled to find a starting price and MOC bets are settled at whatever starting price is returned.
     * MOC bets are always matched and settled, unless a starting price is not available for the selection.
     * Market on Close bets can only be placed before the starting price is determined
     */
    private MarketOnCloseOrder marketOnCloseOrder;

    public OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    public long getSelectionId() {
        return selectionId;
    }

    public void setSelectionId(long selectionId) {
        this.selectionId = selectionId;
    }

    public double getHandicap() {
        return handicap;
    }

    public void setHandicap(double handicap) {
        this.handicap = handicap;
    }

    public Side getSide() {
        return side;
    }

    public void setSide(Side side) {
        this.side = side;
    }

    public LimitOrder getLimitOrder() {
        return limitOrder;
    }

    public void setLimitOrder(LimitOrder limitOrder) {
        this.limitOrder = limitOrder;
    }

    public LimitOnCloseOrder getLimitOnCloseOrder() {
        return limitOnCloseOrder;
    }

    public void setLimitOnCloseOrder(LimitOnCloseOrder limitOnCloseOrder) {
        this.limitOnCloseOrder = limitOnCloseOrder;
    }

    public MarketOnCloseOrder getMarketOnCloseOrder() {
        return marketOnCloseOrder;
    }

    public void setMarketOnCloseOrder(MarketOnCloseOrder marketOnCloseOrder) {
        this.marketOnCloseOrder = marketOnCloseOrder;
    }

    @Override
    public String toString() {
        return "PlaceInstructionReport{" +
                "orderType=" + orderType +
                ", selectionId=" + selectionId +
                ", handicap=" + handicap +
                ", side=" + side +
                ", limitOrder=" + limitOrder +
                ", limitOnCloseOrder=" + limitOnCloseOrder +
                ", marketOnCloseOrder=" + marketOnCloseOrder +
                '}';
    }
}
