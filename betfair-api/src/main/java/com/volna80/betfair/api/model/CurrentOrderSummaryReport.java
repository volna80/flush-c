package com.volna80.betfair.api.model;

import java.util.List;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class CurrentOrderSummaryReport {

    /**
     * The list of current orders returned by your query. This will be a valid list (i.e. empty or non-empty but never 'null').
     */
    private List<CurrentOrderSummary> currentOrders;
    /**
     * Indicates whether there are further result items beyond this page. Note that underlying data is highly time-dependent
     * and the subsequent search orders query might return an empty result.
     * \
     */
    private boolean moreAvailable;


    public List<CurrentOrderSummary> getCurrentOrders() {
        return currentOrders;
    }

    public void setCurrentOrders(List<CurrentOrderSummary> currentOrders) {
        this.currentOrders = currentOrders;
    }

    public boolean isMoreAvailable() {
        return moreAvailable;
    }

    public void setMoreAvailable(boolean moreAvailable) {
        this.moreAvailable = moreAvailable;
    }

    @Override
    public String toString() {
        return "CurrentOrderSummaryReport{" +
                "currentOrders=" + currentOrders +
                ", moreAvailable=" + moreAvailable +
                '}';
    }
}
