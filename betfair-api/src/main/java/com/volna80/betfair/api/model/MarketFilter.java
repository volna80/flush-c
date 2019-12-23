package com.volna80.betfair.api.model;

import com.volna80.betfair.api.util.StringUtils;

import java.util.HashSet;
import java.util.Set;

import static com.volna80.betfair.api.util.JsonBuilder.append;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class MarketFilter implements JsonMessage {

    public static final MarketFilter EMPTY = new MarketFilter();
    /**
     * Restrict markets by any text associated with the market such as the Name, Event, Competition, etc.
     * You can include a wildcard (*) character as long as it is not the first character.
     */
    private String textQuery;

    /**
     * Restrict markets by the Exchange where the market operates
     */
    private Set<String> exchangeIds;

    /**
     * Restrict markets by event type associated with the market. (i.e., Football, Hockey, etc)
     */
    private Set<String> eventTypeIds;

    /**
     * Restrict markets by the event id associated with the market.
     */
    private Set<String> eventIds;

    /**
     * Restrict markets by the competitions associated with the market.
     */
    private Set<String> competitionIds;

    /**
     * Restrict markets by the market id associated with the market.
     */
    private Set<String> marketIds;

    /**
     * Restrict markets by the venue associated with the market. Currently only Horse Racing markets have venues.
     */
    private Set<String> venues;

    /**
     * Restrict to bsp markets only, if True or non-bsp markets if False. If not specified then returns both BSP and non-BSP markets
     */
    private Boolean bspOnly;

    /**
     * Restrict to markets that will turn in play if True or will not turn in play if false. If not specified, returns both.
     */
    private Boolean turnInPlayEnabled;

    /**
     * Restrict to markets that are currently in play if True or are not currently in play if false. If not specified, returns both.
     */
    private Boolean inPlayOnly;

    /**
     * Restrict to markets that match the betting type of the market (i.e. Odds, Asian Handicap Singles, or Asian Handicap Doubles
     */
    private Set<String> marketBettingTypes;

    /**
     * Restrict to markets that are in the specified country or countries
     */
    private Set<String> marketCountries;

    /**
     * Restrict to markets that match the type of the market (i.e., MATCH_ODDS, HALF_TIME_SCORE).
     * You should use this instead of relying on the market name as the market type codes are the same in all locales
     */
    private Set<String> marketTypeCodes;

    /**
     * Restrict to markets with a market start time before or after the specified date
     */
    private TimeRange marketStartTime;


    /**
     * Restrict to markets that I have one or more orders in these status.
     */
    private Set<String> withOrders;
    private String json = null;

    public String getTextQuery() {
        return textQuery;
    }

    public void setTextQuery(String textQuery) {
        this.textQuery = textQuery;
    }

    public Set<String> getExchangeIds() {
        return exchangeIds;
    }

    public void setExchangeIds(Set<String> exchangeIds) {
        this.exchangeIds = exchangeIds;
    }

    public Set<String> getEventTypeIds() {
        return eventTypeIds;
    }

    public void setEventTypeIds(Set<String> eventTypeIds) {
        this.eventTypeIds = eventTypeIds;
    }

    public Set<String> getCompetitionIds() {
        return competitionIds;
    }

    public void setCompetitionIds(Set<String> competitionIds) {
        this.competitionIds = competitionIds;
    }

    public Set<String> getMarketIds() {
        return marketIds;
    }

    public void setMarketIds(Set<String> marketIds) {
        this.marketIds = marketIds;
    }

    public Set<String> getVenues() {
        return venues;
    }

    public void setVenues(Set<String> venues) {
        this.venues = venues;
    }

    public boolean isBspOnly() {
        return bspOnly;
    }

    public void setBspOnly(boolean bspOnly) {
        this.bspOnly = bspOnly;
    }

    public boolean isTurnInPlayEnabled() {
        return turnInPlayEnabled;
    }

    public void setTurnInPlayEnabled(boolean turnInPlayEnabled) {
        this.turnInPlayEnabled = turnInPlayEnabled;
    }

    public boolean isInPlayOnly() {
        return inPlayOnly;
    }

    public void setInPlayOnly(boolean inPlayOnly) {
        this.inPlayOnly = inPlayOnly;
    }

    public Set<String> getMarketBettingTypes() {
        return marketBettingTypes;
    }

    public void setMarketBettingTypes(Set<String> marketBettingTypes) {
        this.marketBettingTypes = marketBettingTypes;
    }

    public Set<String> getMarketCountries() {
        return marketCountries;
    }

    public void setMarketCountries(Set<String> marketCountries) {
        this.marketCountries = marketCountries;
    }

    public Set<String> getMarketTypeCodes() {
        return marketTypeCodes;
    }

    public void setMarketTypeCodes(Set<String> marketTypeCodes) {
        this.marketTypeCodes = marketTypeCodes;
    }

    public TimeRange getMarketStartTime() {
        return marketStartTime;
    }

    public void setMarketStartTime(TimeRange marketStartTime) {
        this.marketStartTime = marketStartTime;
    }

    public Set<String> getWithOrders() {
        return withOrders;
    }

    public void setWithOrders(Set<String> withOrders) {
        this.withOrders = withOrders;
    }

    public Set<String> getEventIds() {
        return eventIds;
    }

    public void setEventIds(Set<String> eventIds) {
        this.eventIds = eventIds;
    }

    @Override
    public String toJson() {

        if (json != null) {
            return json;
        }
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        if (textQuery != null) {
            builder.append("\"textQuery\":\"").append(textQuery).append("\"").append(",");
        }
        append(builder, "exchangeIds", exchangeIds);
        append(builder, "eventTypeIds", eventTypeIds);
        append(builder, "eventIds", eventIds);
        append(builder, "competitionIds", competitionIds);
        append(builder, "marketIds", marketIds);
        append(builder, "venues", venues);
        append(builder, "bspOnly", bspOnly);
        append(builder, "turnInPlayEnabled", turnInPlayEnabled);
        append(builder, "inPlayOnly", inPlayOnly);
        append(builder, "marketBettingTypes", marketBettingTypes);
        append(builder, "marketCountries", marketCountries);
        append(builder, "marketTypeCodes", marketTypeCodes);
        append(builder, "marketStartTime", marketStartTime);
        append(builder, "withOrders", withOrders);

        //check last comma
        StringUtils.removeLastChar(builder, ',');

        builder.append("}");

        json = builder.toString();

        return json;
    }


    @Override
    public String toString() {
        return toJson();
    }

    public void setEventTypeId(final EventType event) {
        if (event == null) {
            return;
        }
        setEventTypeIds(new HashSet<String>() {{
            add(event.getId());
        }});
    }

    public void setMarketCountry(final String country) {
        if (country == null) {
            return;
        }
        setMarketCountries(new HashSet<String>() {{
            add(country);
        }});
    }

    @Deprecated
    public void setMarketCountry(final CountryCodeResult country) {
        if (country == null) {
            return;
        }
        setMarketCountries(new HashSet<String>() {{
            add(country.getCountryCode());
        }});
    }


    public void setCompetitionId(final CompetitionResult competition) {
        if (competition == null) {
            return;
        }
        setCompetitionIds(new HashSet<String>() {{
            add(competition.getCompetition().getId());
        }});
    }

    public void setMarketTypeCode(final String marketType) {
        if (marketType == null) {
            return;
        }

        setMarketTypeCodes(
                new HashSet<String>() {{
                    add(marketType);
                }}
        );
    }

    public void setEvent(final Event event) {
        if (event == null) {
            return;
        }

        setEventIds(
                new HashSet<String>() {{
                    add(event.getId());
                }}
        );
    }
}
