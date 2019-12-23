package com.volna80.betfair.api.model;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class EventResult {

    private Event event;
    private int marketCount;

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public int getMarketCount() {
        return marketCount;
    }

    public void setMarketCount(int marketCount) {
        this.marketCount = marketCount;
    }

    @Override
    public String toString() {
        return "EventResult{" +
                "event=" + event +
                ", marketCount=" + marketCount +
                '}';
    }
}
