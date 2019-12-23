package com.volna80.flush.ui.server.marketdata;

import com.google.common.base.Preconditions;
import com.typesafe.config.Config;
import com.volna80.betfair.api.APINGException;
import com.volna80.betfair.api.IBettingAPI;
import com.volna80.betfair.api.model.*;
import com.volna80.betfair.api.model.errors.ErrorCode;
import com.volna80.flush.server.latency.ILatencyRecorder;
import com.volna80.flush.server.latency.LatencyRecorderFactory;
import com.volna80.flush.ui.ApplicationManager;
import com.volna80.flush.ui.server.IFlushAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
//TODO write tests
public class MarketDataController implements IMarketDataController {

    private static final Logger log = LoggerFactory.getLogger(MarketDataController.class);

    private static final MarketBook EMPTY = new MarketBook();

    private static final int FAIL_THRESHOLD = 10;

    static {
        EMPTY.setStatus(MarketStatus.UNKNOWN);
        EMPTY.setRunners(new ArrayList<>());
    }

    private final IBettingAPI api;
    private final String locale;
    private final int bestPriceDepth;
    /**
     * time to request market data
     */
    private final long delay;
    private final PriceProjection priceProjection;
    private final OrderProjection orderProjection;
    private final MatchProjection matchProjection;
    private final ScheduledExecutorService executor = IFlushAPI.executor;
    private ConcurrentHashMap<String, MDMiner> subscriptionByMarket = new ConcurrentHashMap<>();


    public MarketDataController(IBettingAPI api, String locale, Config config) {
        this.api = api;
        this.locale = locale;

        bestPriceDepth = config.getInt("depth");
        delay = config.getLong("delayInMs");


        ExBestOffersOverrides ex = new ExBestOffersOverrides();
        ex.setBestPricesDepth(bestPriceDepth);

//            ex.setRollupLimit(1);
//            ex.setRollupModel(RollupModel.NONE);

        priceProjection = new PriceProjection();
        priceProjection.setPriceData(PriceData.EX_BEST_OFFERS, PriceData.EX_TRADED);
        priceProjection.setVirtualise(true);
        priceProjection.setExBestOffersOverrides(ex);

        orderProjection = OrderProjection.EXECUTABLE;

        matchProjection = MatchProjection.ROLLED_UP_BY_PRICE;
    }

    public void init() {
        //reserved
    }


    @Override
    public synchronized void subscribe(String market, long runner) {

        log.info("subscribe {}:{}", market, runner);

        Preconditions.checkNotNull(market);
        Preconditions.checkNotNull(runner);

        MDMiner s = subscriptionByMarket.get(market);
        if (s != null) {
            int i = s.numberOfActive.incrementAndGet();
            log.info("added one more subscription to " + market + ". N:" + i);
            return;
        }

        s = new MDMiner(market);
        if (subscriptionByMarket.putIfAbsent(market, s) == null) {
            executor.scheduleWithFixedDelay(s, delay, delay, TimeUnit.MILLISECONDS);
            int i = s.numberOfActive.incrementAndGet();
            log.info("subscription has been created for " + market + ". N:" + i);
        } else {
            s = subscriptionByMarket.get(market);
            int i = s.numberOfActive.incrementAndGet();
            log.info("added one more subscription to " + market + ". N:" + i);
        }
    }

    @Override
    public MarketBook snap(String marketId) {
        Preconditions.checkNotNull(marketId);

        MDMiner handler = subscriptionByMarket.get(marketId);
        if (handler == null) {
            return EMPTY;
        }

        return handler.tick;
    }

    @Override
    public synchronized void unsubscribe(String marketId, long selectionId) {
        Preconditions.checkNotNull(marketId);
        Preconditions.checkNotNull(selectionId);

        log.info("unsubscribe : {}:{}", marketId, selectionId);
        MDMiner miner = subscriptionByMarket.get(marketId);
        if (miner == null) {
            log.info("strange, number unsubscription is more than subscriptions");
            return;
        }

        int i = miner.numberOfActive.decrementAndGet();
        if (i == 0) {
            subscriptionByMarket.remove(marketId);
            miner.active = false;
            log.info("request to stop subscription for " + marketId);
        }
    }


    //TODO merge requests for set of market ids
    private class MDMiner implements Runnable {

        private final String marketId;
        private final List<String> ids;
        private final ILatencyRecorder recorder = LatencyRecorderFactory.getRecorder("md-miner");
        private volatile boolean active = true;
        private AtomicInteger numberOfActive = new AtomicInteger(0);
        private int failCount = 0;
        private volatile MarketBook tick = EMPTY;

        private MDMiner(final String marketId) {
            this.marketId = marketId;

            this.ids = new ArrayList<String>() {{
                add(marketId);
            }};

        }

        @Override
        public void run() {
            log.debug("take latest md snapshot {}", marketId);


            try {
                recorder.start();

                List<com.volna80.betfair.api.model.MarketBook> book =
                        api.listMarketBook(ids, priceProjection, orderProjection, matchProjection, null, locale);


                if (book.isEmpty()) {
                    tick = EMPTY;
                } else {
                    tick = book.get(0); //we expect only one book
                }

                recorder.stop();

                if (tick.getStatus() == MarketStatus.CLOSED) {
                    log.info("market {} is closed", marketId);
                    active = false;
                }


            } catch (APINGException e) {

                ErrorCode code = e.getCode();

                if (code.isNotCritical()) {
                    onException();
                } else {
                    ApplicationManager.getInstance().exit(e.getMessage(), true);
                }


                //fail,
            } catch (Exception e) {
                //TODO volnnik Shall we be able to recover IOExceptoins? just try to reconnect
                log.error("couldn't get market data tick for " + marketId, e);
                ApplicationManager.getInstance().exit("couldn't get market data tick for " + marketId, true);
            }

            if (!active) {
                log.info("the subscription has been cancelled for " + marketId);
                throw new RuntimeException("the subscription has been cancelled for " + marketId);
            }
        }

        /**
         * process exception
         */
        private void onException() {
            failCount++;
            if (failCount > FAIL_THRESHOLD) {
                //stop subscription
                subscriptionByMarket.remove(marketId);
                tick = EMPTY;
                log.error("stop subscription to " + marketId);
                throw new RuntimeException("reached max failure count for " + marketId);
            }
        }
    }

}
