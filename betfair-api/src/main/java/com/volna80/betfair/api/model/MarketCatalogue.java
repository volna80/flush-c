package com.volna80.betfair.api.model;

import java.util.Date;
import java.util.List;

/**
 * Information about a market
 * <p/>
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class MarketCatalogue implements Comparable<MarketCatalogue> {

    /**
     * The unique identifier for the market
     */
    private String marketId;

    /**
     * The name of the market
     */
    private String marketName;

    /**
     * The time this market starts at, only returned when the MARKET_START_TIME enum is passed in the marketProjections
     */
    private Date marketStartTime;

    /**
     * Details about the market
     */
    private MarketDescription description;

    /**
     * The runners (selections) contained in the market
     */
    private List<RunnerCatalog> runners;

    private EventType eventType;

    private Competition competition;

    private Event event;

    private double totalMatched;

    public String getMarketId() {
        return marketId;
    }

    public void setMarketId(String marketId) {
        this.marketId = marketId;
    }

    public String getMarketName() {
        return marketName;
    }

    public void setMarketName(String marketName) {
        this.marketName = marketName;
    }

    public Date getMarketStartTime() {
        return marketStartTime;
    }

    public void setMarketStartTime(Date marketStartTime) {
        this.marketStartTime = marketStartTime;
    }

    public MarketDescription getDescription() {
        return description;
    }

    public void setDescription(MarketDescription description) {
        this.description = description;
    }

    public List<RunnerCatalog> getRunners() {
        return runners;
    }

    public void setRunners(List<RunnerCatalog> runners) {
        this.runners = runners;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public Competition getCompetition() {
        return competition;
    }

    public void setCompetition(Competition competition) {
        this.competition = competition;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public double getTotalMatched() {
        return totalMatched;
    }

    @Override
    public String toString() {
        return "MarketCatalogue{" +
                "marketId='" + marketId + '\'' +
                ", marketName='" + marketName + '\'' +
                ", marketStartTime=" + marketStartTime +
                ", description=" + description +
                ", runners=" + runners +
                ", eventType=" + eventType +
                ", competition=" + competition +
                ", event=" + event +
                '}';
    }

    @Override
    public int compareTo(MarketCatalogue o) {
        return getMarketName().compareTo(o.getMarketName());
    }
}
