package com.volna80.betfair.api.model;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class AccountFunds {

    /**
     * Amount available to bet
     */
    private double availableToBetBalance;
    /**
     * Current exposure
     */
    private double exposure;
    /**
     * Sum of retained commission.
     */
    private double retainedCommission;
    /**
     * Exposure limit
     */
    private double exposureLimit;

    public double getAvailableToBetBalance() {
        return availableToBetBalance;
    }

    public void setAvailableToBetBalance(double availableToBetBalance) {
        this.availableToBetBalance = availableToBetBalance;
    }

    public double getExposure() {
        return exposure;
    }

    public void setExposure(double exposure) {
        this.exposure = exposure;
    }

    public double getRetainedCommission() {
        return retainedCommission;
    }

    public void setRetainedCommission(double retainedCommission) {
        this.retainedCommission = retainedCommission;
    }

    public double getExposureLimit() {
        return exposureLimit;
    }

    public void setExposureLimit(double exposureLimit) {
        this.exposureLimit = exposureLimit;
    }

    @Override
    public String toString() {
        return "AccountFunds{" +
                "availableToBetBalance=" + availableToBetBalance +
                ", exposure=" + exposure +
                ", retainedCommission=" + retainedCommission +
                ", exposureLimit=" + exposureLimit +
                '}';
    }
}
