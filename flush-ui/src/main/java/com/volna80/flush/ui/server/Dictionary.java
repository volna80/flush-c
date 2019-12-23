package com.volna80.flush.ui.server;

import com.volna80.betfair.api.IBetfairAPI;
import com.volna80.betfair.api.model.*;
import com.volna80.betfair.api.model.adapter.Precision;
import com.volna80.flush.ui.model.UIMarketInfo;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Data cache of reference data
 * <p/>
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class Dictionary implements IDictionary {

    private static final Logger log = LoggerFactory.getLogger(Dictionary.class);

    private final IBetfairAPI api;
    private final String locale;


    private ConcurrentHashMap<String, UIMarketInfo> markets = new ConcurrentHashMap<>(256);

    public Dictionary(IBetfairAPI api, String locale) {
        this.api = api;
        this.locale = locale;
    }

    @Override
    public void init() {
        //TODO
    }

    @Override
    public UIMarketInfo getMarketInfo(final String marketId) {
        UIMarketInfo market = markets.get(marketId);
        if (market == null) {
            if (markets.putIfAbsent(marketId, new UIMarketInfo(marketId)) == null) {

                Service<MarketCatalogue> loadMarketName = new Service<MarketCatalogue>() {
                    @Override
                    protected Task<MarketCatalogue> createTask() {
                        return new Task<MarketCatalogue>() {
                            @Override
                            protected MarketCatalogue call() throws Exception {

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
                        };
                    }
                };

                loadMarketName.setExecutor(IFlushAPI.executor);

                loadMarketName.setOnFailed(workerStateEvent -> {
                    log.error("couldn't load market name {}", marketId);
                    markets.get(marketId).error();
                });

                loadMarketName.setOnSucceeded(workerStateEvent -> {
                    MarketCatalogue resp = (MarketCatalogue) workerStateEvent.getSource().getValue();
                    UIMarketInfo market1 = markets.get(marketId);

                    market1.marketNameProperty().setValue(resp.getMarketName());
                    market1.eventNameProperty().setValue(resp.getEvent().getName());
                    market1.competitionNameProperty().setValue(resp.getCompetition().getName());
                    market1.eventTypeNameProperty().setValue(resp.getEventType().getName());
                    for (RunnerCatalog runner : resp.getRunners()) {
                        market1.getRunner(runner.getSelectionId()).set(runner.getRunnerName() + (Double.isNaN(runner.getHandicap()) || Precision.isZero(runner.getHandicap()) ? "" : " (" + runner.getHandicap() + ")"));
                    }
                });

                loadMarketName.start();


            }
            market = markets.get(marketId);
        }

        return market;
    }
}
