package com.volna80.betfair.api.model;

import java.util.List;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class MarketProfitAndLoss {

    /**
     * The unique identifier for the market
     */
    private String marketId;


    /**
     * The commission rate applied to P&L values. Only returned if netOfCommision option is requested
     */
    private Double commissionApplied = Double.NaN;


    /**
     * Calculated profit and loss data.
     */
    private List<RunnerProfitAndLoss> profitAndLosses;


    public String getMarketId() {
        return marketId;
    }

    public void setMarketId(String marketId) {
        this.marketId = marketId;
    }

    public double getCommissionApplied() {
        return commissionApplied;
    }

    public void setCommissionApplied(Double commissionApplied) {
        this.commissionApplied = commissionApplied;
    }

    public List<RunnerProfitAndLoss> getProfitAndLosses() {
        return profitAndLosses;
    }

    public void setProfitAndLosses(List<RunnerProfitAndLoss> profitAndLosses) {
        this.profitAndLosses = profitAndLosses;
    }

    @Override
    public String toString() {
        return "MarketProfitAndLoss{" +
                "marketId='" + marketId + '\'' +
                ", commissionApplied=" + commissionApplied +
                ", profitAndLosses=" + profitAndLosses +
                '}';
    }
}
