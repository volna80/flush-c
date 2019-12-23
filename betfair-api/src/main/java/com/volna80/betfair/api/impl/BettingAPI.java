package com.volna80.betfair.api.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.volna80.betfair.api.BetfairException;
import com.volna80.betfair.api.IBettingAPI;
import com.volna80.betfair.api.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.volna80.betfair.api.util.JsonBuilder.append;
import static com.volna80.betfair.api.util.JsonBuilder.appendMessages;
import static com.volna80.betfair.api.util.StringUtils.removeLastChar;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class BettingAPI extends AbstractAPI implements IBettingAPI {

    public static final String LIST_EVENT_TYPES = "listEventTypes/";
    public static final String LIST_COMPETITIONS = "listCompetitions/";
    public static final String LIST_COUNTRIES = "listCountries/";
    public static final String LIST_MARKET_TYPES = "listMarketTypes/";
    public static final String LIST_TIME_RANGES = "listTimeRanges/";
    public static final String LIST_EVENTS = "listEvents/";
    public static final String LIST_MARKET_CATALOGUE = "listMarketCatalogue/";
    public static final String LIST_MARKET_BOOK = "listMarketBook/";
    public static final String LIST_CURRENT_ORDERS = "listCurrentOrders/";
    public static final String CANCEL_ORDERS = "cancelOrders/";
    public static final String PLACE_ORDERS = "placeOrders/";
    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static final Logger log = LoggerFactory.getLogger(BettingAPI.class);
    private static final AtomicInteger reqCounter = new AtomicInteger();
    private final Gson gson;
    private Invocation.Builder listEventTypes;
    private Invocation.Builder listCompetitions;
    private Invocation.Builder listCountries;
    private Invocation.Builder listMarketTypes;
    private Invocation.Builder listTimeRanges;
    private Invocation.Builder listEvents;
    private Invocation.Builder listMarketCatalogue;
    private Invocation.Builder listMarketBook;
    private Invocation.Builder listCurrentOrders;
    private Invocation.Builder cancelOrders;
    private Invocation.Builder placeOrders;
    private IMsgBuilder<EventTypeResult[]> eventTypesBuilder;
    private IMsgBuilder<CompetitionResult[]> competitionsBuilder;
    private IMsgBuilder<CountryCodeResult[]> countryBuilder;
    private IMsgBuilder<MarketTypeResult[]> marketTypesBuilder;
    private IMsgBuilder<TimeRangeResult[]> timeRangeBuilder;
    private IMsgBuilder<EventResult[]> eventsBulider;
    private IMsgBuilder<MarketCatalogue[]> marketCatalogueBuilder;
    private IMsgBuilder<MarketBook[]> marketBookBuilder;
    private IMsgBuilder<CurrentOrderSummaryReport> currentOrdersBuilder;
    private IMsgBuilder<CancelExecutionReport> cancelExecutionReportBuilder;
    private IMsgBuilder<PlaceExecutionReport> placeExecutionReportBuilder;


    public BettingAPI(String appId, Ssoid ssoid, Client client, String uri) {
        super(appId, ssoid, client, uri);

        GsonBuilder builder = new GsonBuilder();
        builder.setDateFormat(DATE_FORMAT);
        gson = builder.create();

    }

    public void init() {

        eventTypesBuilder = new MsgBuilder<>(EventTypeResult[].class, gson);
        competitionsBuilder = new MsgBuilder<>(CompetitionResult[].class, gson);
        countryBuilder = new MsgBuilder<>(CountryCodeResult[].class, gson);
        marketTypesBuilder = new MsgBuilder<>(MarketTypeResult[].class, gson);
        timeRangeBuilder = new MsgBuilder<>(TimeRangeResult[].class, gson);
        eventsBulider = new MsgBuilder<>(EventResult[].class, gson);
        marketCatalogueBuilder = new MsgBuilder<>(MarketCatalogue[].class, gson);
        marketBookBuilder = new MsgBuilder<>(MarketBook[].class, gson);
        currentOrdersBuilder = new MsgBuilder<>(CurrentOrderSummaryReport.class, gson);
        cancelExecutionReportBuilder = new MsgBuilder<>(CancelExecutionReport.class, gson);
        placeExecutionReportBuilder = new MsgBuilder<>(PlaceExecutionReport.class, gson);


        listEventTypes = makeRequest(LIST_EVENT_TYPES);
        listCompetitions = makeRequest(LIST_COMPETITIONS);
        listCountries = makeRequest(LIST_COUNTRIES);
        listMarketTypes = makeRequest(LIST_MARKET_TYPES);
        listTimeRanges = makeRequest(LIST_TIME_RANGES);
        listEvents = makeRequest(LIST_EVENTS);
        listMarketCatalogue = makeRequest(LIST_MARKET_CATALOGUE);
        listMarketBook = makeRequest(LIST_MARKET_BOOK);
        listCurrentOrders = makeRequest(LIST_CURRENT_ORDERS);
        cancelOrders = makeRequest(CANCEL_ORDERS);
        placeOrders = makeRequest(PLACE_ORDERS);
    }

    public String makeRequest(MarketFilter filter, String locale) {


        StringBuilder request = new StringBuilder();
        request.append('{');
        append(request, "id", reqCounter.getAndIncrement());
        append(request, "locale", locale);
        append(request, "filter", filter);
        removeLastChar(request, ',');
        request.append('}');

        final String req = request.toString();
        log.debug("[{}] request : {}", ssoid, req);
        return req;
    }

    public String makeRequest(MarketFilter filter, TimeGranularity granularity) {
        StringBuilder request = new StringBuilder();
        request.append('{');
        append(request, "id", reqCounter.getAndIncrement());
        append(request, "granularity", granularity);
        append(request, "filter", filter);
        removeLastChar(request, ',');
        request.append('}');

        final String req = request.toString();
        log.debug("[{}] request : {}", ssoid, req);
        return req;
    }

    public String makeRequest(MarketFilter filter, Set<MarketProjection> marketProjection, MarketSort sort, int maxResults, String locale) {

        StringBuilder request = new StringBuilder();
        request.append('{');
        append(request, "id", reqCounter.getAndIncrement());
        append(request, "marketProjection", marketProjection);
        append(request, "sort", sort);
        append(request, "maxResults", maxResults);
        append(request, "locale", locale);
        append(request, "filter", filter);
        removeLastChar(request, ',');
        request.append('}');

        return request.toString();
    }

    public String makeRequest(List<String> marketIds, PriceProjection priceProjection, OrderProjection orderProjection, MatchProjection matchProjection, String currencyCode, String locale) {

        StringBuilder request = new StringBuilder();
        request.append('{');
        append(request, "id", reqCounter.getAndIncrement());
        append(request, "locale", locale);
        append(request, "marketIds", marketIds);
        append(request, "priceProjection", priceProjection);
        append(request, "orderProjection", orderProjection);
        append(request, "matchProjection", matchProjection);
        append(request, "currencyCode", currencyCode);
        removeLastChar(request, ',');
        request.append('}');

        return request.toString();
    }

    public String makeRequest(Set<String> betIds, Set<String> marketIds, OrderProjection orderProjection, TimeRange placedDateRange, TimeRange dateRange, OrderBy orderBy, SortDir sortDir, int fromRecord, int recordCount) {

        StringBuilder request = new StringBuilder();
        request.append('{');
        append(request, "id", reqCounter.getAndIncrement());
        append(request, "betIds", betIds);
        append(request, "marketIds", marketIds);
        append(request, "orderProjection", orderProjection);
        append(request, "placedDateRange", placedDateRange);
        append(request, "dateRange", dateRange);
        append(request, "orderBy", orderBy);
        append(request, "sortDir", sortDir);
        append(request, "fromRecord", fromRecord);
        append(request, "recordCount", recordCount);

        removeLastChar(request, ',');
        request.append('}');

        return request.toString();
    }


    private String makeRequest(String marketId, List<? extends JsonMessage> instructions, String customerRef) {
        StringBuilder request = new StringBuilder();
        request.append('{');
        append(request, "id", reqCounter.getAndIncrement());
        append(request, "marketId", marketId);
        appendMessages(request, "instructions", instructions);
        append(request, "customerRef", customerRef);

        removeLastChar(request, ',');
        request.append('}');

        return request.toString();
    }


    @Override
    public List<EventType> listEventTypes(MarketFilter filter, String locale) throws BetfairException {
        log.trace("listEventTypes : {} : {}", filter, locale);
        EventTypeResult[] result = post(listEventTypes, eventTypesBuilder, makeRequest(filter, locale));
        List<EventType> list = new ArrayList<>();
        for (EventTypeResult r : result) {
            list.add(r.eventType);
        }
        return list;
    }


    @Override
    public List<CompetitionResult> listCompetition(MarketFilter filter, String locale) throws BetfairException {
        log.trace("listCompetition : {} : {}", filter, locale);
        return Arrays.asList(
                post(listCompetitions, competitionsBuilder, makeRequest(filter, locale))
        );
    }

    @Override
    public Map<TimeRange, Integer> listTimeRanges(MarketFilter filter, TimeGranularity granularity) throws BetfairException {
        log.trace("listTimeRanges : {} : {}", filter, granularity);


        TimeRangeResult[] response = post(listTimeRanges, timeRangeBuilder, makeRequest(filter, granularity));

        Map<TimeRange, Integer> result = new HashMap<>(response.length);

        for (TimeRangeResult r : response) {
            result.put(r.getTimeRange(), r.getMarketCount());
        }

        return result;
    }


    @Override
    public Map<Event, Integer> listEvents(MarketFilter filter, String locale) throws BetfairException {
        log.trace("listEvents : {} : {}", filter, locale);


        EventResult[] response = post(listEvents, eventsBulider, makeRequest(filter, locale));

        Map<Event, Integer> result = new HashMap<>(response.length);

        for (EventResult r : response) {
            result.put(r.getEvent(), r.getMarketCount());
        }

        return result;
    }

    @Override
    public List<CountryCodeResult> listCountries(MarketFilter filter, String locale) throws BetfairException {
        log.trace("listCountries : {} : {}", filter, locale);
        return Arrays.asList(post(listCountries, countryBuilder, makeRequest(filter, locale)));
    }

    @Override
    public Map<String, Integer> listMarketTypes(MarketFilter filter, String locale) throws BetfairException {


        MarketTypeResult[] response = post(listMarketTypes, marketTypesBuilder, makeRequest(filter, locale));

        HashMap<String, Integer> result = new HashMap<>(response.length);

        for (MarketTypeResult r : response) {
            result.put(r.getMarketType(), r.getMarketCount());
        }

        return result;
    }

    @Override
    public List<MarketCatalogue> listMarketCatalogue(MarketFilter filter, Set<MarketProjection> marketProjection, MarketSort sort, int maxResults, String locale) throws BetfairException {

        MarketCatalogue[] result = post(listMarketCatalogue, marketCatalogueBuilder, makeRequest(filter, marketProjection, sort, maxResults, locale));

        return Arrays.asList(result);
    }

    @Override
    public List<MarketBook> listMarketBook(List<String> marketIds, PriceProjection priceProjection, OrderProjection orderProjection, MatchProjection matchProjection, String currencyCode, String locale) throws BetfairException {

        MarketBook[] result = post(listMarketBook, marketBookBuilder, makeRequest(marketIds, priceProjection, orderProjection, matchProjection, currencyCode, locale));

        return Arrays.asList(result);
    }

    @Override
    public List<MarketProfitAndLoss> listMarketProfitAndLoss(Set<String> marketIds, boolean includeSettledBets, boolean includeBspBets, boolean netOfCommission) throws BetfairException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public CurrentOrderSummaryReport listCurrentOrders(Set<String> betIds, Set<String> marketIds, OrderProjection orderProjection, TimeRange placedDateRange, TimeRange dateRange, OrderBy orderBy, SortDir sortDir, int fromRecord, int recordCount) throws BetfairException {
        return post(listCurrentOrders, currentOrdersBuilder, makeRequest(betIds, marketIds, orderProjection, placedDateRange, dateRange, orderBy, sortDir, fromRecord, recordCount));
    }


    @Override
    public ClearedOrderSummaryReport listClearedOrders(BetStatus betStatus, Set<String> eventTypeIds, Set<String> eventIds, Set<String> marketIds, Set<String> runnerIds, Set<String> betIds, Side side, TimeRange settledDateRange, GroupBy groupBy, boolean includeItemDescription, String locale, int fromRecord, int recordCount) throws BetfairException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public PlaceExecutionReport placeOrders(String marketId, List<PlaceInstruction> instructions, String customerRef) throws BetfairException {
        return post(placeOrders, placeExecutionReportBuilder, makeRequest(marketId, instructions, customerRef));
    }


    @Override
    public CancelExecutionReport cancelOrders(String marketId, List<CancelInstruction> instructions, String customerRef) throws BetfairException {
        return post(cancelOrders, cancelExecutionReportBuilder, makeRequest(marketId, instructions, customerRef));
    }


    @Override
    public ReplaceExecutionReport replaceOrders(String marketId, List<ReplaceInstruction> instructions, String customerRef) throws BetfairException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public UpdateExecutionReport updateOrders(String marketId, List<UpdateInstruction> instructions, String customerRef) throws BetfairException {
        throw new RuntimeException("Not implemented");
    }
}
