package com.volna80.betfair.api.model;

import com.google.gson.annotations.JsonAdapter;
import com.volna80.betfair.api.model.adapter.LastPriceTradedAdapter;
import com.volna80.betfair.api.model.adapter.TotalMatchedAdapter;

import java.util.Date;
import java.util.List;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class Runner {

    /**
     * The unique id of the runner (selection)
     */
    private long selectionId;

    /**
     * The handicap
     */
    private double handicap;

    /**
     * The status of the selection (i.e., ACTIVE, REMOVED, WINNER, LOSER, HIDDEN)
     */
    private RunnerStatus status;


    /**
     * The adjustment factor applied if the selection is removed
     */
    private double adjustmentFactor;

    /**
     * The price of the most recent bet matched on this selection
     */
    @JsonAdapter(LastPriceTradedAdapter.class)
    private int lastPriceTraded;

    /**
     * The total amount matched on this runner
     */
    @JsonAdapter(TotalMatchedAdapter.class)
    private int totalMatched;

    /**
     * If date and time the runner was removed
     */
    private Date removalDate;

    /**
     * The BSP related prices for this runner
     */
    private StartingPrices sp;

    /**
     * The Exchange prices available for this runner
     */
    private ExchangePrices ex;

    /**
     * List of orders in the market
     */
    private List<Order> orders;

    /**
     * List of matches (i.e, orders that have been fully or partially executed)
     */

    private List<Match> matches;

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

    public RunnerStatus getStatus() {
        return status;
    }

    public void setStatus(RunnerStatus status) {
        this.status = status;
    }

    public double getAdjustmentFactor() {
        return adjustmentFactor;
    }

    public void setAdjustmentFactor(double adjustmentFactor) {
        this.adjustmentFactor = adjustmentFactor;
    }

    public int getLastPriceTraded() {
        return lastPriceTraded;
    }

    public void setLastPriceTraded(int lastPriceTraded) {
        this.lastPriceTraded = lastPriceTraded;
    }

    public int getTotalMatched() {
        return totalMatched;
    }

    public void setTotalMatched(int totalMatched) {
        this.totalMatched = totalMatched;
    }

    public Date getRemovalDate() {
        return removalDate;
    }

    public void setRemovalDate(Date removalDate) {
        this.removalDate = removalDate;
    }

    public StartingPrices getSp() {
        return sp;
    }

    public void setSp(StartingPrices sp) {
        this.sp = sp;
    }

    public ExchangePrices getEx() {
        return ex;
    }

    public void setEx(ExchangePrices ex) {
        this.ex = ex;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public List<Match> getMatches() {
        return matches;
    }

    public void setMatches(List<Match> matches) {
        this.matches = matches;
    }

    @Override
    public String toString() {
        return "run[" + selectionId + '|' + ex + '|' + orders + '|' + matches + ']';
    }
}
