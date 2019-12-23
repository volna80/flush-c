package com.volna80.betfair.api.impl;

import com.volna80.betfair.api.BetfairException;
import com.volna80.betfair.api.IBetfairAPI;
import com.volna80.betfair.api.model.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class BetfairAPI implements IBetfairAPI {

    private AccountAPI accountAPI;
    private BettingAPI bettingAPI;

    @Override
    public AccountFunds getAccountFunds() throws BetfairException {
        return accountAPI.getAccountFunds();
    }

    @Override
    public AccountDetails getAccountDetails() throws BetfairException {
        return accountAPI.getAccountDetails();
    }

    @Override
    public List<EventType> listEventTypes(MarketFilter filter, String locale) throws BetfairException {
        return bettingAPI.listEventTypes(filter, locale);
    }

    @Override
    public List<CompetitionResult> listCompetition(MarketFilter filter, String locale) throws BetfairException {
        return bettingAPI.listCompetition(filter, locale);
    }

    @Override
    public Map<TimeRange, Integer> listTimeRanges(MarketFilter filter, TimeGranularity granularity) throws BetfairException {
        return bettingAPI.listTimeRanges(filter, granularity);
    }

    @Override
    public Map<Event, Integer> listEvents(MarketFilter filter, String locale) throws BetfairException {
        return bettingAPI.listEvents(filter, locale);
    }

    @Override
    public List<CountryCodeResult> listCountries(MarketFilter filter, String locale) throws BetfairException {
        return bettingAPI.listCountries(filter, locale);
    }

    @Override
    public Map<String, Integer> listMarketTypes(MarketFilter filter, String locale) throws BetfairException {
        return bettingAPI.listMarketTypes(filter, locale);
    }

    @Override
    public List<MarketCatalogue> listMarketCatalogue(MarketFilter filter, Set<MarketProjection> marketProjection, MarketSort sort, int maxResults, String locale) throws BetfairException {
        return bettingAPI.listMarketCatalogue(filter, marketProjection, sort, maxResults, locale);
    }

    @Override
    public List<MarketBook> listMarketBook(List<String> marketIds, PriceProjection priceProjection, OrderProjection orderProjection, MatchProjection matchProjection, String currencyCode, String locale) throws BetfairException {
        return bettingAPI.listMarketBook(marketIds, priceProjection, orderProjection, matchProjection, currencyCode, locale);
    }

    @Override
    public List<MarketProfitAndLoss> listMarketProfitAndLoss(Set<String> marketIds, boolean includeSettledBets, boolean includeBspBets, boolean netOfCommission) throws BetfairException {
        return bettingAPI.listMarketProfitAndLoss(marketIds, includeSettledBets, includeBspBets, netOfCommission);
    }

    @Override
    public CurrentOrderSummaryReport listCurrentOrders(Set<String> betIds, Set<String> marketIds, OrderProjection orderProjection, TimeRange placedDateRange, TimeRange dateRange,
                                                       OrderBy orderBy, SortDir sortDir, int fromRecord, int recordCount) throws BetfairException {
        return bettingAPI.listCurrentOrders(betIds, marketIds, orderProjection, placedDateRange, dateRange, orderBy, sortDir, fromRecord, recordCount);
    }

    @Override
    public ClearedOrderSummaryReport listClearedOrders(BetStatus betStatus, Set<String> eventTypeIds, Set<String> eventIds, Set<String> marketIds,
                                                       Set<String> runnerIds, Set<String> betIds, Side side, TimeRange settledDateRange, GroupBy groupBy,
                                                       boolean includeItemDescription, String locale, int fromRecord, int recordCount) throws BetfairException {
        return bettingAPI.listClearedOrders(betStatus, eventTypeIds, eventTypeIds, marketIds, runnerIds, betIds, side, settledDateRange, groupBy, includeItemDescription,
                locale, fromRecord, recordCount);
    }

    @Override
    public PlaceExecutionReport placeOrders(String marketId, List<PlaceInstruction> instructions, String customerRef) throws BetfairException {
        return bettingAPI.placeOrders(marketId, instructions, customerRef);
    }

    @Override
    public CancelExecutionReport cancelOrders(String marketId, List<CancelInstruction> instructions, String customerRef) throws BetfairException {
        return bettingAPI.cancelOrders(marketId, instructions, customerRef);
    }

    @Override
    public ReplaceExecutionReport replaceOrders(String marketId, List<ReplaceInstruction> instructions, String customerRef) throws BetfairException {
        return bettingAPI.replaceOrders(marketId, instructions, customerRef);
    }

    @Override
    public UpdateExecutionReport updateOrders(String marketId, List<UpdateInstruction> instructions, String customerRef) throws BetfairException {
        return bettingAPI.updateOrders(marketId, instructions, customerRef);
    }

    public AccountAPI getAccountAPI() {
        return accountAPI;
    }

    public void setAccountAPI(AccountAPI accountAPI) {
        this.accountAPI = accountAPI;
    }

    public void setBettingAPI(BettingAPI bettingAPI) {
        this.bettingAPI = bettingAPI;
    }

}
