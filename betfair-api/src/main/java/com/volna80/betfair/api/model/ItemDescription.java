package com.volna80.betfair.api.model;

import java.util.Date;

/**
 * This object contains some text which may be useful to render a betting history view. It offers no long-term warranty as to the correctness of the text.
 * <p/>
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class ItemDescription {

    /**
     * The event type name, translated into the requested locale. Available at EVENT_TYPE groupBy or lower.
     */
    private String eventTypeDesc;


    /**
     * The eventName, or openDate + venue, translated into the requested locale. Available at EVENT groupBy or lower.
     */
    private String eventDesc;


    /**
     * The market name or racing market type ("Win", "To Be Placed (2 places)", "To Be Placed (5 places)" etc)
     * translated into the requested locale. Available at MARKET groupBy or lower.
     */
    private String marketDesc;

    /**
     * The start time of the market (in ISO-8601 format, not translated). Available at MARKET groupBy or lower.
     */
    private Date marketStartTime;


    /**
     * The runner name, maybe including the handicap, translated into the requested locale. Available at BET groupBy.
     */
    private String runnerDesc;


    /**
     * The numberOfWinners on a market. Available at BET groupBy.
     */
    private int numberOfWinners;


    public String getEventTypeDesc() {
        return eventTypeDesc;
    }

    public void setEventTypeDesc(String eventTypeDesc) {
        this.eventTypeDesc = eventTypeDesc;
    }

    public String getEventDesc() {
        return eventDesc;
    }

    public void setEventDesc(String eventDesc) {
        this.eventDesc = eventDesc;
    }

    public String getMarketDesc() {
        return marketDesc;
    }

    public void setMarketDesc(String marketDesc) {
        this.marketDesc = marketDesc;
    }

    public Date getMarketStartTime() {
        return marketStartTime;
    }

    public void setMarketStartTime(Date marketStartTime) {
        this.marketStartTime = marketStartTime;
    }

    public String getRunnerDesc() {
        return runnerDesc;
    }

    public void setRunnerDesc(String runnerDesc) {
        this.runnerDesc = runnerDesc;
    }

    public int getNumberOfWinners() {
        return numberOfWinners;
    }

    public void setNumberOfWinners(int numberOfWinners) {
        this.numberOfWinners = numberOfWinners;
    }

    @Override
    public String toString() {
        return "ItemDescription{" +
                "eventTypeDesc='" + eventTypeDesc + '\'' +
                ", eventDesc='" + eventDesc + '\'' +
                ", marketDesc='" + marketDesc + '\'' +
                ", marketStartTime=" + marketStartTime +
                ", runnerDesc='" + runnerDesc + '\'' +
                ", numberOfWinners=" + numberOfWinners +
                '}';
    }
}
