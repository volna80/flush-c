package com.volna80.betfair.api.model;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class TimeRangeResult {

    private TimeRange timeRange;
    private int marketCount;

    public TimeRange getTimeRange() {
        return timeRange;
    }

    public void setTimeRange(TimeRange timeRange) {
        this.timeRange = timeRange;
    }

    public int getMarketCount() {
        return marketCount;
    }

    public void setMarketCount(int marketCount) {
        this.marketCount = marketCount;
    }

    @Override
    public String toString() {
        return "TimeRangeResult{" +
                "timeRange=" + timeRange +
                ", marketCount=" + marketCount +
                '}';
    }
}
