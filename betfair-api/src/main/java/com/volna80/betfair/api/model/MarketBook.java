package com.volna80.betfair.api.model;

import java.util.Date;
import java.util.List;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class MarketBook {

    //object creation timestamp
    public final long timestamp = System.currentTimeMillis();


    /**
     * The unique identifier for the market
     */
    private String marketId;


    /**
     * @return True if the data returned by listMarketBook will be delayed. The data may be delayed because you are not logged
     * in with a funded account or you are using an Application Key that does not allow up to date data.
     */
    private boolean isMarketDataDelayed;

    /**
     * The status of the market, for example ACTIVE, SUSPENDED, SETTLED, etc.
     */
    private MarketStatus status;


    /**
     * The number of seconds an order is held until it is submitted into the market. Orders are usually delayed when the market is in-play
     */
    private int betDelay;


    /**
     * True if the market starting price has been reconciled
     */
    private boolean bspReconciled;

    /**
     * If false, runners may be added to the market
     */
    private boolean complete;


    /**
     * True if the market is currently in play
     */
    private boolean inplay;


    /**
     * The number of selections that could be settled as winners
     */
    private int numberOfWinners;

    /**
     * The number of runners in the market
     */
    private int numberOfRunners;

    /**
     * The number of runners that are currently active. An active runner is a selection available for betting
     */
    private int numberOfActiveRunners;

    /**
     * The most recent time an order was executed
     */
    private Date lastMatchTime;

    /**
     * The total amount matched
     */
    private double totalMatched;

    /**
     * The total amount of orders that remain unmatched
     */
    private double totalAvailable;

    /**
     * True if cross matching is enabled for this market.
     */
    private boolean crossMatching;

    /**
     * True if runners in the market can be voided
     */
    private boolean runnersVoidable;

    /**
     * The version of the market. The version increments whenever the market status changes, for example, turning in-play, or suspended when a goal score.
     */
    private long version;


    /**
     * Information about the runners (selections) in the market.
     */
    private List<Runner> runners;


    public String getMarketId() {
        return marketId;
    }

    public void setMarketId(String marketId) {
        this.marketId = marketId;
    }

    public boolean isMarketDataDelayed() {
        return isMarketDataDelayed;
    }

    public void setMarketDataDelayed(boolean isMarketDataDelayed) {
        this.isMarketDataDelayed = isMarketDataDelayed;
    }

    public MarketStatus getStatus() {
        return status;
    }

    public void setStatus(MarketStatus status) {
        this.status = status;
    }

    public int getBetDelay() {
        return betDelay;
    }

    public void setBetDelay(int betDelay) {
        this.betDelay = betDelay;
    }

    public boolean isBspReconciled() {
        return bspReconciled;
    }

    public void setBspReconciled(boolean bspReconciled) {
        this.bspReconciled = bspReconciled;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public boolean isInplay() {
        return inplay;
    }

    public void setInplay(boolean inplay) {
        this.inplay = inplay;
    }

    public int getNumberOfWinners() {
        return numberOfWinners;
    }

    public void setNumberOfWinners(int numberOfWinners) {
        this.numberOfWinners = numberOfWinners;
    }

    public int getNumberOfRunners() {
        return numberOfRunners;
    }

    public void setNumberOfRunners(int numberOfRunners) {
        this.numberOfRunners = numberOfRunners;
    }

    public int getNumberOfActiveRunners() {
        return numberOfActiveRunners;
    }

    public void setNumberOfActiveRunners(int numberOfActiveRunners) {
        this.numberOfActiveRunners = numberOfActiveRunners;
    }

    public Date getLastMatchTime() {
        return lastMatchTime;
    }

    public void setLastMatchTime(Date lastMatchTime) {
        this.lastMatchTime = lastMatchTime;
    }

    public double getTotalMatched() {
        return totalMatched;
    }

    public void setTotalMatched(double totalMatched) {
        this.totalMatched = totalMatched;
    }

    public double getTotalAvailable() {
        return totalAvailable;
    }

    public void setTotalAvailable(double totalAvailable) {
        this.totalAvailable = totalAvailable;
    }

    public boolean isCrossMatching() {
        return crossMatching;
    }

    public void setCrossMatching(boolean crossMatching) {
        this.crossMatching = crossMatching;
    }

    public boolean isRunnersVoidable() {
        return runnersVoidable;
    }

    public void setRunnersVoidable(boolean runnersVoidable) {
        this.runnersVoidable = runnersVoidable;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public List<Runner> getRunners() {
        return runners;
    }

    public void setRunners(List<Runner> runners) {
        this.runners = runners;
    }

    @Override
    public String toString() {
        return "md[" + marketId + "|" + version + '|' + status + '|' + runners + ']';
    }
}
