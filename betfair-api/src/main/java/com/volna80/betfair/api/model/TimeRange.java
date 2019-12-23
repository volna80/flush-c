package com.volna80.betfair.api.model;

import java.util.Date;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class TimeRange {

    private Date from;
    private Date to;

    public TimeRange(Date from, Date to) {
        this.from = from;
        this.to = to;
    }

    public TimeRange() {
    }

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TimeRange timeRange = (TimeRange) o;

        if (from != null ? !from.equals(timeRange.from) : timeRange.from != null) return false;
        if (to != null ? !to.equals(timeRange.to) : timeRange.to != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = from != null ? from.hashCode() : 0;
        result = 31 * result + (to != null ? to.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TimeRange{" +
                "from=" + from +
                ", to=" + to +
                '}';
    }

}
