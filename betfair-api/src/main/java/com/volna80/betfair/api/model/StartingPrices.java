package com.volna80.betfair.api.model;

import java.util.List;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class StartingPrices {

    /**
     * What the starting price would be if the market was reconciled now taking into account the SP bets as well as unmatched exchange bets on the same selection in the exchange.
     */
    private double nearPrice; //TODO int
    /**
     * What the starting price would be if the market was reconciled now taking into account only the currently place SP bets. The Far Price is not as complicated but not as accurate and only accounts for money on the exchange at SP.
     */
    private double farPrice; //TODO int

    /**
     * The back bets matched at the actual Betfair Starting Price
     */
    private List<PriceSize> backStakeTaken;

    /**
     * The lay amount matched at the actual Betfair Starting Price
     */
    private List<PriceSize> layLiabilityTaken;


    /**
     * The final BSP price for this runner. Only available for a BSP market that has been reconciled.
     */
    private double actualSP;

    public double getNearPrice() {
        return nearPrice;
    }

    public void setNearPrice(double nearPrice) {
        this.nearPrice = nearPrice;
    }

    public double getFarPrice() {
        return farPrice;
    }

    public void setFarPrice(double farPrice) {
        this.farPrice = farPrice;
    }

    public List<PriceSize> getBackStakeTaken() {
        return backStakeTaken;
    }

    public void setBackStakeTaken(List<PriceSize> backStakeTaken) {
        this.backStakeTaken = backStakeTaken;
    }

    public List<PriceSize> getLayLiabilityTaken() {
        return layLiabilityTaken;
    }

    public void setLayLiabilityTaken(List<PriceSize> layLiabilityTaken) {
        this.layLiabilityTaken = layLiabilityTaken;
    }

    public double getActualSP() {
        return actualSP;
    }

    public void setActualSP(double actualSP) {
        this.actualSP = actualSP;
    }

    @Override
    public String toString() {
        return "StartingPrices{" +
                "actualSP=" + actualSP +
                ", layLiabilityTaken=" + layLiabilityTaken +
                ", backStakeTaken=" + backStakeTaken +
                ", farPrice=" + farPrice +
                ", nearPrice=" + nearPrice +
                '}';
    }
}
