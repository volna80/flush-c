package com.volna80.flush.ui.server;

import com.volna80.betfair.api.BetfairException;
import com.volna80.betfair.api.model.*;
import com.volna80.flush.server.IReferenceProvider;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * High level API for interaction of UI with server side components
 * <p/>
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public interface IFlushAPI extends IReferenceProvider {


    ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(
            r -> new Thread(r, "flush-api")
    );


    AccountDetails getAccountDetails() throws BetfairException;


    /**
     * @return competitions (world cup, premier league)
     */
    List<CompetitionResult> getCompetitions(EventType event, String country, String marketType, TimeRange timeRange) throws BetfairException; //TODO move to dictionary

    /**
     * @return events (Spartak vs CSKA)
     */
    Set<Event> getSoccerEvents(EventType event, String country, CompetitionResult competition, String marketType, TimeRange timeRange) throws BetfairException; //TODO move to dictionary

    /**
     * return all markets for soccer (market type : odds)
     *
     * @param query free text query
     * @return
     * @throws BetfairException
     */
    List<MarketCatalogue> getSoccerEvents(String query) throws BetfairException;

    List<MarketCatalogue> getMarkets(EventType event, String country, String marketType, TimeRange timeRange, CompetitionResult competition, Event event2) throws BetfairException; //TODO move to dictionary

    void subscribe(String market, long selectionId);

    MarketBook snap(String market);

    void unsubscribe(String market, long selectionId);

    IDictionary getDictionary();

    /**
     * stop the system
     */
    void stop();

    MarketCatalogue getMarketInfo(String marketId) throws BetfairException;

    List<MarketCatalogue> getAllMarketsForEvent(Event event) throws BetfairException;
}
