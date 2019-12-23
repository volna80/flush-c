package com.volna80.betfair.api.model;

import com.volna80.betfair.api.util.StringUtils;

/**
 * Options to alter the default representation of best offer prices
 * <p/>
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class ExBestOffersOverrides implements JsonMessage {

    /**
     * The maximum number of prices to return on each side for each runner. If unspecified defaults to 3.
     */
    private Integer bestPricesDepth;

    /**
     * The model to use when rolling up available sizes. If unspecified defaults to STAKE rollup model with rollupLimit of minimum stake in the specified currency.
     */
    private RollupModel rollupModel;

    /**
     * The volume limit to use when rolling up returned sizes. The exact definition of the limit depends on the rollupModel.
     * If no limit is provided it will use minimum stake as default the value. Ignored if no rollup model is specified.
     */
    private Integer rollupLimit;

    /**
     * Only applicable when rollupModel is MANAGED_LIABILITY. The rollup model switches from being stake based to liability
     * based at the smallest lay price which is >= rollupLiabilityThreshold.service level default (TBD). Not supported as yet.
     */
    private Double rollupLiabilityThreshold;


    /**
     * Only applicable when rollupModel is MANAGED_LIABILITY. (rollupLiabilityFactor * rollupLimit) is the minimum liabilty
     * the user is deemed to be comfortable with. After the rollupLiabilityThreshold price subsequent volumes will be rolled up to minimum
     * value such that the liability >= the minimum liability.service level default (5). Not supported as yet.
     */
    private Integer rollupLiabilityFactor;

    public Integer getBestPricesDepth() {
        return bestPricesDepth;
    }

    public void setBestPricesDepth(int bestPricesDepth) {
        this.bestPricesDepth = bestPricesDepth;
    }

    public RollupModel getRollupModel() {
        return rollupModel;
    }

    public void setRollupModel(RollupModel rollupModel) {
        this.rollupModel = rollupModel;
    }

    public Integer getRollupLimit() {
        return rollupLimit;
    }

    public void setRollupLimit(int rollupLimit) {
        this.rollupLimit = rollupLimit;
    }

    public Double getRollupLiabilityThreshold() {
        return rollupLiabilityThreshold;
    }

    public void setRollupLiabilityThreshold(double rollupLiabilityThreshold) {
        this.rollupLiabilityThreshold = rollupLiabilityThreshold;
    }

    public Integer getRollupLiabilityFactor() {
        return rollupLiabilityFactor;
    }

    public void setRollupLiabilityFactor(int rollupLiabilityFactor) {
        this.rollupLiabilityFactor = rollupLiabilityFactor;
    }

    @Override
    public String toString() {
        return "ExBestOffersOverrides{" +
                "bestPricesDepth=" + bestPricesDepth +
                ", rollupModel=" + rollupModel +
                ", rollupLimit=" + rollupLimit +
                ", rollupLiabilityThreshold=" + rollupLiabilityThreshold +
                ", rollupLiabilityFactor=" + rollupLiabilityFactor +
                '}';
    }

    public String toJson() {
        StringBuilder json = new StringBuilder();
        json.append('{');

        if (bestPricesDepth != null) {
            json.append("\"bestPricesDepth\":").append(bestPricesDepth).append(',');
        }
        if (rollupModel != null) {
            json.append("\"rollupModel\":\"").append(rollupModel).append('\"').append(',');
        }
        if (rollupLimit != null) {
            json.append("\"rollupLimit\":").append(rollupLimit).append(',');
        }
        if (rollupLiabilityThreshold != null) {
            json.append("\"rollupLiabilityThreshold\":").append(rollupLiabilityThreshold).append(',');
        }
        if (rollupLiabilityFactor != null) {
            json.append("\"rollupLiabilityFactor\":").append(rollupLiabilityFactor);
        }

        //check last comma
        StringUtils.removeLastChar(json, ',');

        json.append('}');
        return json.toString();
    }
}
