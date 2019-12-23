package com.volna80.betfair.api.model;

import java.util.Date;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class Event {

    /**
     * The unique id for the event
     */
    private String id;

    /**
     * The name of the event
     */
    private String name;

    /**
     * The ISO-2 code for the event.  A list of ISO-2 codes is available via http://en.wikipedia.org/wiki/ISO_3166-1_alpha-2
     */
    private String countryCode;
    /**
     * The timezone in which the event is taking place
     */
    private String timezone;


    private String venue;

    /**
     * The scheduled start date and time of the event.
     */
    private Date openDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public Date getOpenDate() {
        return openDate;
    }

    public void setOpenDate(Date openDate) {
        this.openDate = openDate;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", timezone='" + timezone + '\'' +
                ", venue='" + venue + '\'' +
                ", openDate=" + openDate +
                '}';
    }
}
