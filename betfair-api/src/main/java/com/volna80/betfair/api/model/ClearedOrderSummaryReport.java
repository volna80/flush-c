package com.volna80.betfair.api.model;

import com.google.gson.annotations.JsonAdapter;
import com.volna80.betfair.api.model.adapter.*;

import java.util.Date;

/**
 * Summary of a cleared order.
 * <p/>
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class ClearedOrderSummaryReport {

    /**
     * The id of the event type bet on. Available at EVENT_TYPE groupBy level or lower.
     */
    private String eventTypeId;

    /**
     * The id of the event bet on. Available at EVENT groupBy level or lower.
     */
    private String eventId;

    /**
     * The id of the market bet on. Available at MARKET groupBy level or lower.
     */
    private String marketId;


    /**
     * The id of the selection bet on. Available at RUNNER groupBy level or lower.
     */
    private long selectionId;

    /**
     * The id of the market bet on. Available at MARKET groupBy level or lower.
     */
    private double handicap;

    /**
     * The id of the bet. Available at BET groupBy level.
     */
    private String betId;

    /**
     * The date the bet order was placed by the customer. Only available at BET groupBy level.
     */
    private Date placedDate;

    /**
     * The turn in play persistence state of the order at bet placement time. This field will be empty or omitted on true SP bets. Only available at BET groupBy level.
     */
    private String persistenceType;

    /**
     * The type of bet (e.g standard limited-liability Exchange bet (LIMIT), a standard BSP bet (MARKET_ON_CLOSE),
     * or a minimum-accepted-price BSP bet (LIMIT_ON_CLOSE)). If the bet has a OrderType of MARKET_ON_CLOSE
     * and a persistenceType of MARKET_ON_CLOSE then it is a bet which has transitioned from LIMIT to MARKET_ON_CLOSE. Only available at BET groupBy level.
     */
    private String orderType;


    /**
     * Whether the bet was a back or lay bet. Available at SIDE groupBy level or3 lower.
     */
    private String side;

    /**
     * A container for all the ancillary data and localised text valid for this Item
     */
    private ItemDescription itemDescription;


    /**
     * The average requested price across all settled bet orders under this Item. Available at SIDE groupBy level or lower.
     */
    @JsonAdapter(PriceRequestedAdapter.class)
    private int priceRequested;


    /**
     * The date and time the bet order was settled by Betfair. Available at SIDE groupBy level or lower.
     */
    private Date settledDate;


    /**
     * The number of actual bets within this grouping (will be 1 for BET groupBy)
     */
    private int betCount;

    /**
     * The cumulative amount of commission paid by the customer across all bets under this Item, in the account currency.
     * Available at EXCHANGE, EVENT_TYPE, EVENT and MARKET level groupings only.
     */
    @JsonAdapter(CommissionAdapter.class)
    private int commission;


    /**
     * The average matched price across all settled bets or bet fragments under this Item. Available at SIDE groupBy level or lower.
     */
    @JsonAdapter(PriceMatchedAdapter.class)
    private int priceMatched;

    /**
     * If true, then the matched price was affected by a reduction factor due to of a runner removal from this Horse Racing market.
     */
    private boolean priceReduced;

    /**
     * The cumulative bet size that was settled as matched or voided under this Item, in the account currency. Available at SIDE groupBy level or lower.
     */
    @JsonAdapter(SizeSettledAdapter.class)
    private int sizeSettled;


    /**
     * The profit or loss (negative profit) gained on this line, in the account currency
     */
    @JsonAdapter(ProfitAdapter.class)
    private int profit;

    /**
     * The amount of the bet that was available to be matched, before cancellation or lapsing, in the account currency
     */
    @JsonAdapter(SizeCancelledAdapter.class)
    private int sizeCancelled;


    public String getEventTypeId() {
        return eventTypeId;
    }

    public void setEventTypeId(String eventTypeId) {
        this.eventTypeId = eventTypeId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
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

    public String getBetId() {
        return betId;
    }

    public void setBetId(String betId) {
        this.betId = betId;
    }

    public Date getPlacedDate() {
        return placedDate;
    }

    public void setPlacedDate(Date placedDate) {
        this.placedDate = placedDate;
    }

    public String getPersistenceType() {
        return persistenceType;
    }

    public void setPersistenceType(String persistenceType) {
        this.persistenceType = persistenceType;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public ItemDescription getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(ItemDescription itemDescription) {
        this.itemDescription = itemDescription;
    }

    public int getPriceRequested() {
        return priceRequested;
    }

    public void setPriceRequested(int priceRequested) {
        this.priceRequested = priceRequested;
    }

    public Date getSettledDate() {
        return settledDate;
    }

    public void setSettledDate(Date settledDate) {
        this.settledDate = settledDate;
    }

    public int getBetCount() {
        return betCount;
    }

    public void setBetCount(int betCount) {
        this.betCount = betCount;
    }

    public int getCommission() {
        return commission;
    }

    public void setCommission(int commission) {
        this.commission = commission;
    }

    public int getPriceMatched() {
        return priceMatched;
    }

    public void setPriceMatched(int priceMatched) {
        this.priceMatched = priceMatched;
    }

    public boolean isPriceReduced() {
        return priceReduced;
    }

    public void setPriceReduced(boolean priceReduced) {
        this.priceReduced = priceReduced;
    }

    public int getSizeSettled() {
        return sizeSettled;
    }

    public void setSizeSettled(int sizeSettled) {
        this.sizeSettled = sizeSettled;
    }

    public int getProfit() {
        return profit;
    }

    public void setProfit(int profit) {
        this.profit = profit;
    }

    public int getSizeCancelled() {
        return sizeCancelled;
    }

    public void setSizeCancelled(int sizeCancelled) {
        this.sizeCancelled = sizeCancelled;
    }

    @Override
    public String toString() {
        return "ClearedOrderSummaryReport{" +
                "eventTypeId='" + eventTypeId + '\'' +
                ", eventId='" + eventId + '\'' +
                ", marketId='" + marketId + '\'' +
                ", selectionId=" + selectionId +
                ", handicap=" + handicap +
                ", betId='" + betId + '\'' +
                ", placedDate=" + placedDate +
                ", persistenceType='" + persistenceType + '\'' +
                ", orderType='" + orderType + '\'' +
                ", side='" + side + '\'' +
                ", itemDescription=" + itemDescription +
                ", priceRequested=" + priceRequested +
                ", settledDate=" + settledDate +
                ", betCount=" + betCount +
                ", commission=" + commission +
                ", priceMatched=" + priceMatched +
                ", priceReduced=" + priceReduced +
                ", sizeSettled=" + sizeSettled +
                ", profit=" + profit +
                ", sizeCancelled=" + sizeCancelled +
                '}';
    }
}
