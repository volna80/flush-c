package com.volna80.betfair.api.model;

import com.google.gson.annotations.JsonAdapter;
import com.volna80.betfair.api.model.adapter.*;

import java.util.Date;

/**
 * Summary of the current order
 * <p/>
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class CurrentOrderSummary {

    /**
     * The bet ID of the original place order.
     */
    private String betId;


    /**
     * The market id the order is for
     */
    private String marketId;

    /**
     * The selection id the order is for.
     */
    private long selectionId;

    /**
     * The handicap of the bet.
     */
    private double handicap = Double.NaN;

    /**
     * The price and size of the bet.
     */
    private PriceSize priceSize;

    /**
     * Not to be confused with size. This is the liability of a given BSP bet.
     */
    private double bspLiability = Double.NaN;

    private Side side;

    /**
     * Either EXECUTABLE (an unmatched amount remains) or EXECUTION_COMPLETE (no unmatched amount remains).
     */
    private OrderStatus status;

    /**
     * What to do with the order at turn-in-play.
     */
    private PersistenceType persistenceType;

    /**
     * BSP Order type.
     */
    private OrderType orderType;

    /**
     * The date, to the second, the bet was placed.
     */
    private Date placedDate;

    /**
     * The date, to the second, of the last matched bet fragment (where applicable)
     */
    private Date matchedDate;

    /**
     * The average price matched at. Voided match fragments are removed from this average calculation.
     */
    @JsonAdapter(AveragePriceMatchedAdapter.class)
    private int averagePriceMatched;

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

    /**
     * The regulator authorisation code.
     */
    private String regulatorAuthCode;

    /**
     * The regulator Code.
     */
    private String regulatorCode;

    public String getBetId() {
        return betId;
    }

    public void setBetId(String betId) {
        this.betId = betId;
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

    public double getHandicap() {
        return handicap;
    }

    public void setHandicap(double handicap) {
        this.handicap = handicap;
    }

    public PriceSize getPriceSize() {
        return priceSize;
    }

    public void setPriceSize(PriceSize priceSize) {
        this.priceSize = priceSize;
    }

    public double getBspLiability() {
        return bspLiability;
    }

    public void setBspLiability(double bspLiability) {
        this.bspLiability = bspLiability;
    }

    public Side getSide() {
        return side;
    }

    public void setSide(Side side) {
        this.side = side;
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

    public OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    public Date getPlacedDate() {
        return placedDate;
    }

    public void setPlacedDate(Date placedDate) {
        this.placedDate = placedDate;
    }

    public Date getMatchedDate() {
        return matchedDate;
    }

    public void setMatchedDate(Date matchedDate) {
        this.matchedDate = matchedDate;
    }

    public int getAveragePriceMatched() {
        return averagePriceMatched;
    }

    public void setAveragePriceMatched(int averagePriceMatched) {
        this.averagePriceMatched = averagePriceMatched;
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

    public String getRegulatorAuthCode() {
        return regulatorAuthCode;
    }

    public void setRegulatorAuthCode(String regulatorAuthCode) {
        this.regulatorAuthCode = regulatorAuthCode;
    }

    public String getRegulatorCode() {
        return regulatorCode;
    }

    public void setRegulatorCode(String regulatorCode) {
        this.regulatorCode = regulatorCode;
    }

    @Override
    public String toString() {
        return "CurrentOrderSummary{" +
                "regulatorCode='" + regulatorCode + '\'' +
                ", regulatorAuthCode='" + regulatorAuthCode + '\'' +
                ", sizeVoided=" + sizeVoided +
                ", sizeCancelled=" + sizeCancelled +
                ", sizeLapsed=" + sizeLapsed +
                ", sizeRemaining=" + sizeRemaining +
                ", sizeMatched=" + sizeMatched +
                ", averagePriceMatched=" + averagePriceMatched +
                ", matchedDate=" + matchedDate +
                ", placedDate=" + placedDate +
                ", orderType=" + orderType +
                ", persistenceType=" + persistenceType +
                ", status=" + status +
                ", side=" + side +
                ", bspLiability=" + bspLiability +
                ", priceSize=" + priceSize +
                ", handicap=" + handicap +
                ", selectionId=" + selectionId +
                ", marketId='" + marketId + '\'' +
                ", betId='" + betId + '\'' +
                '}';
    }
}
