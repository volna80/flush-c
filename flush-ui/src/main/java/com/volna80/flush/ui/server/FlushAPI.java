package com.volna80.flush.ui.server;

import com.volna80.betfair.api.BetfairApiFactory;
import com.volna80.betfair.api.BetfairException;
import com.volna80.betfair.api.IBetfairAPI;
import com.volna80.betfair.api.model.*;
import com.volna80.flush.server.FlushServer;
import com.volna80.flush.server.IReferenceProvider;
import com.volna80.flush.ui.server.marketdata.MarketDataController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class FlushAPI implements IFlushAPI, IReferenceProvider {

    private static final Logger log = LoggerFactory.getLogger(FlushAPI.class);

    private final IBetfairAPI api;

    private final MarketDataController md;
    private final OrdersControllerDirect directOrderController;
    private final BetfairApiFactory factory;
    private final IDictionary dictionary;
    private final String locale = Locale.getDefault().getLanguage();
    private final String unique = System.currentTimeMillis() + "";
    private final AtomicLong counter = new AtomicLong(0);
    private volatile IOrdersController orderController;

    FlushAPI(Ssoid ssoid) {
        log.info("initializing ...");

        factory = new BetfairApiFactory();
        factory.setSsoid(ssoid);
        factory.setAppId(FlushServer.APP_KEY);
        factory.setAccountUrl(BetfairApiFactory.ACCOUNT_API_UK_EXCHANGE_JSON_REST);
        factory.setBettingUrl(BetfairApiFactory.BETTING_API_UK_EXCHANGE_JSON_REST);

        factory.init();


        api = factory.makeService();

        md = new MarketDataController(api, locale, FlushServer.config.getConfig("flush.md"));
        md.init();

        directOrderController = new OrdersControllerDirect(this, factory.makeService());
        directOrderController.init();

        dictionary = new Dictionary(api, locale);
        dictionary.init();


        log.info("initialized");
    }


    public static FlushAPI make(Ssoid ssoid) {
        return new FlushAPI(ssoid);
    }

    @Override
    public AccountDetails getAccountDetails() throws BetfairException {
        return api.getAccountDetails();
    }


    @Override
    public List<CompetitionResult> getCompetitions(EventType event, String country, String marketType, TimeRange timeRange) throws BetfairException {
        MarketFilter filter = new MarketFilter();
        filter.setEventTypeId(event);
        filter.setMarketCountry(country);
        filter.setMarketTypeCode(marketType);
        filter.setMarketStartTime(timeRange);

        return api.listCompetition(filter, locale);
    }

    @Override
    public Set<Event> getSoccerEvents(EventType event, String country, CompetitionResult competition, String marketType, TimeRange timeRange) throws BetfairException {
        MarketFilter filter = new MarketFilter();
        filter.setEventTypeId(event);
        filter.setMarketCountry(country);
        filter.setCompetitionId(competition);
        filter.setMarketTypeCode(marketType);
        filter.setMarketStartTime(timeRange);
        return api.listEvents(filter, locale).keySet();
    }

    @Override
    public List<MarketCatalogue> getSoccerEvents(String query) throws BetfairException {
        MarketFilter filter = new MarketFilter();
        filter.setEventTypeId(EventType.SOCCER);
        filter.setTextQuery(query);
        filter.setMarketTypeCode(MarketTypes.MATCH_ODDS);

        Set<MarketProjection> rules = new HashSet<>();
        rules.add(MarketProjection.COMPETITION);
        rules.add(MarketProjection.EVENT);
        rules.add(MarketProjection.EVENT_TYPE);
        rules.add(MarketProjection.MARKET_DESCRIPTION);
        rules.add(MarketProjection.RUNNER_DESCRIPTION);

        int MAX_RESULT = 50;

        return api.listMarketCatalogue(filter, rules, MarketSort.MAXIMUM_AVAILABLE, MAX_RESULT, locale);

    }

    @Override
    public List<MarketCatalogue> getMarkets(EventType event, String country, String marketType, TimeRange timeRange, CompetitionResult competition, Event event2) throws BetfairException {

        MarketFilter filter = new MarketFilter();
        filter.setEventTypeId(event);
        filter.setMarketCountry(country);
        filter.setMarketTypeCode(marketType);
        filter.setMarketStartTime(timeRange);
        filter.setCompetitionId(competition);
        filter.setEvent(event2);

        Set<MarketProjection> rules = new HashSet<>();
        rules.add(MarketProjection.COMPETITION);
        rules.add(MarketProjection.EVENT);
        rules.add(MarketProjection.EVENT_TYPE);
        rules.add(MarketProjection.MARKET_DESCRIPTION);
        rules.add(MarketProjection.RUNNER_METADATA);
        rules.add(MarketProjection.RUNNER_DESCRIPTION);
        rules.add(MarketProjection.MARKET_START_TIME);

        int MAX_RESULT = 50; //TODO

        return api.listMarketCatalogue(filter, rules, MarketSort.MAXIMUM_AVAILABLE, MAX_RESULT, locale);
    }

    @Override
    public MarketCatalogue getMarketInfo(String marketId) throws BetfairException {
        MarketFilter filter = new MarketFilter();
        filter.setMarketIds(Collections.singleton(marketId));

        Set<MarketProjection> rules = new HashSet<>();
        rules.add(MarketProjection.COMPETITION);
        rules.add(MarketProjection.EVENT);
        rules.add(MarketProjection.EVENT_TYPE);
        rules.add(MarketProjection.MARKET_DESCRIPTION);
        rules.add(MarketProjection.RUNNER_DESCRIPTION);

        int MAX_RESULT = 50;

        List<MarketCatalogue> catalogue = api.listMarketCatalogue(filter, rules, MarketSort.MAXIMUM_AVAILABLE, MAX_RESULT, locale);

        if (catalogue.size() == 0) {
            log.error("couldn't find a market {}", marketId);
            throw new RuntimeException("could not find a market " + marketId);
        } else if (catalogue.size() > 1) {
            log.error("unexpected number o markets for {}", marketId);
        }

        return catalogue.get(0);
    }

    @Override
    public List<MarketCatalogue> getAllMarketsForEvent(Event event) throws BetfairException {
        MarketFilter filter = new MarketFilter();
        filter.setEvent(event);

        Set<MarketProjection> rules = new HashSet<>();
        rules.add(MarketProjection.COMPETITION);
        rules.add(MarketProjection.EVENT);
        rules.add(MarketProjection.EVENT_TYPE);
        rules.add(MarketProjection.MARKET_DESCRIPTION);
        rules.add(MarketProjection.RUNNER_METADATA);
        rules.add(MarketProjection.RUNNER_DESCRIPTION);
        rules.add(MarketProjection.MARKET_START_TIME);

        int MAX_RESULT = 250;

        return api.listMarketCatalogue(filter, rules, MarketSort.MAXIMUM_AVAILABLE, MAX_RESULT, locale);
    }

    @Override
    public void subscribe(String market, long selectionId) {
        md.subscribe(market, selectionId);
    }

    @Override
    public MarketBook snap(String market) {
        return md.snap(market);
    }

    @Override
    public void unsubscribe(String market, long selectionId) {
        md.unsubscribe(market, selectionId);
    }


    @Override
    public String nextRef() {
        return unique + counter.getAndIncrement();
    }

    @Override
    public IDictionary getDictionary() {
        return dictionary;
    }


    public IOrdersController getOrdersController() {
        return orderController;
    }

    public void setOrderController(IOrdersController orderController) {
        log.info("setOrderController:{}", orderController);
        this.orderController = orderController;
    }

    public IOrdersController getDirectOrdersController() {
        return directOrderController;
    }

    @Override
    public void stop() {
        factory.close();
    }
}
