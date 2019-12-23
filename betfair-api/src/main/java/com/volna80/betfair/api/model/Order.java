package com.volna80.betfair.api.model;

import com.google.gson.annotations.JsonAdapter;
import com.volna80.betfair.api.model.adapter.*;

import java.util.Date;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class Order {

    private String betId;

    /**
     * BSP Order type.
     */
    private OrderType orderType;

    /**
     * Either EXECUTABLE (an unmatched amount remains) or EXECUTION_COMPLETE (no unmatched amount remains).
     */
    private OrderStatus status;

    /**
     * What to do with the order at turn-in-play
     */
    private PersistenceType persistenceType;

    /**
     * Indicates if the bet is a Back or a LAY
     */
    private Side side;

    /**
     * The price of the bet.
     */
    @JsonAdapter(PriceAdapter.class)
    private int price;

    /**
     * The size of the bet.
     */
    @JsonAdapter(SizeAdapter.class)
    private int size;

    /**
     * Not to be confused with size. This is the liability of a given BSP bet.
     */
    private double bspLiability;

    /**
     * The date, to the second, the bet was placed.
     */
    private Date placedDate;

    /**
     * The average price matched at. Voided match fragments are removed from this average calculation.
     */
    private double avgPriceMatched;

    /**
     * The current amount of this bet that was matched.
     */
    @JsonAdapter(SizeMatchedAdapter.class)
    private int sizeMatched;


    /**
     * The current amount of this bet that is unmatched.
     */
    @JsonAdapter(SizeRemainingAdapter.class)
    private int sizeRemaining;

    /**
     * The current amount of this bet that was lapsed.
     */
    @JsonAdapter(SizeLapsedAdapter.class)
    private int sizeLapsed;

    /**
     * The current amount of this bet that was cancelled.
     */
    @JsonAdapter(SizeCancelledAdapter.class)
    private int sizeCancelled;

    /**
     * The current amount of this bet that was voided.
     */
    @JsonAdapter(SizeVoidedAdapter.class)
    private int sizeVoided;

    public String getBetId() {
        return betId;
    }

    public void setBetId(String betId) {
        this.betId = betId;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public PersistenceType getPersistenceType() {
        return persistenceType;
    }

    public void setPersistenceType(PersistenceType persistenceType) {
        this.persistenceType = persistenceType;
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

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public double getBspLiability() {
        return bspLiability;
    }

    public void setBspLiability(double bspLiability) {
        this.bspLiability = bspLiability;
    }

    public Date getPlacedDate() {
        return placedDate;
    }

    public void setPlacedDate(Date placedDate) {
        this.placedDate = placedDate;
    }

    public double getAvgPriceMatched() {
        return avgPriceMatched;
    }

    public void setAvgPriceMatched(double avgPriceMatched) {
        this.avgPriceMatched = avgPriceMatched;
    }

    public int getSizeMatched() {
        return sizeMatched;
    }

    public void setSizeMatched(int sizeMatched) {
        this.sizeMatched = sizeMatched;
    }

    public int getSizeRemaining() {
        return sizeRemaining;
    }

    public void setSizeRemaining(int sizeRemaining) {
        this.sizeRemaining = sizeRemaining;
    }

    public int getSizeLapsed() {
        return sizeLapsed;
    }

    public void setSizeLapsed(int sizeLapsed) {
        this.sizeLapsed = sizeLapsed;
    }

    public int getSizeCancelled() {
        return sizeCancelled;
    }

    public void setSizeCancelled(int sizeCancelled) {
        this.sizeCancelled = sizeCancelled;
    }

    public int getSizeVoided() {
        return sizeVoided;
    }

    public void setSizeVoided(int sizeVoided) {
        this.sizeVoided = sizeVoided;
    }

    @Override
    public String toString() {

        return "ord[" + betId + '|' + side + '|' + size + '@' + price + '|' + sizeMatched + '|' + sizeRemaining + ']';
    }
}
