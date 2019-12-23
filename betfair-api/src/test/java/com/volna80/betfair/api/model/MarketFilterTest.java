package com.volna80.betfair.api.model;

import org.json.JSONObject;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class MarketFilterTest {

    private static final Logger log = LoggerFactory.getLogger(MarketFilterTest.class);

    private static Set<String> set(String... values) {
        return new HashSet<>(Arrays.asList(values));
    }

    @Test
    public void test() {
        MarketFilter filter = new MarketFilter();

        filter.setTextQuery("textQuery");
        filter.setExchangeIds(set("exchange1", "exchange2"));
        filter.setEventTypeIds(set("event1", "event2"));
        filter.setCompetitionIds(set("competition1", "competition2"));
        filter.setMarketIds(set("market1", "market2"));
        filter.setVenues(set("venue1", "venue2"));
        filter.setBspOnly(true);
        filter.setTurnInPlayEnabled(true);
        filter.setInPlayOnly(true);
        filter.setMarketBettingTypes(set("type1", "type2"));
        filter.setMarketCountries(set("country1", "country2"));
        filter.setMarketTypeCodes(set("code1", "code2"));
        filter.setMarketStartTime(new TimeRange(new Date(0), new Date(1000 * 60 * 60 * 24)));
        filter.setWithOrders(set("id1", "id2"));

        JSONObject json = new JSONObject(filter.toJson());

        assertEquals("textQuery", json.getString("textQuery"));
        assertEquals("exchange1", json.getJSONArray("exchangeIds").getString(0));
        assertEquals("event1", json.getJSONArray("eventTypeIds").getString(0));
        assertEquals("market1", json.getJSONArray("marketIds").getString(0));
        assertEquals("market2", json.getJSONArray("marketIds").getString(1));
        assertEquals("venue2", json.getJSONArray("venues").getString(1));
        assertEquals("venue1", json.getJSONArray("venues").getString(0));
        assertEquals(true, json.getBoolean("bspOnly"));
        assertEquals(true, json.getBoolean("turnInPlayEnabled"));
        assertEquals(true, json.getBoolean("inPlayOnly"));
        assertEquals("type2", json.getJSONArray("marketBettingTypes").getString(0));
        assertEquals("country1", json.getJSONArray("marketCountries").getString(0));
        assertEquals("code2", json.getJSONArray("marketTypeCodes").getString(0));
        assertEquals("1970-01-01T03:00:00.000Z", json.getJSONObject("marketStartTime").getString("from"));
        assertEquals("1970-01-02T03:00:00.000Z", json.getJSONObject("marketStartTime").getString("to"));
        assertEquals("id2", json.getJSONArray("withOrders").getString(0));

        log.info(filter.toString());


    }

    @Test
    public void testNullObj() {
        MarketFilter filter = new MarketFilter();

        JSONObject json = new JSONObject(filter.toJson());
        assertFalse(json.has("textQuery"));
    }

}
