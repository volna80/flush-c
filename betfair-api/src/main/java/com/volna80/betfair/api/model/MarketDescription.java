package com.volna80.betfair.api.model;

import java.util.Date;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class MarketDescription {

    /**
     * If 'true' the market supports 'Keep' bets if the market is to be turned in-play
     */
    private boolean persistenceEnabled;

    /**
     * If 'true' the market supports Betfair SP betting
     */
    private boolean bspMarket;

    /**
     * The market start time
     */
    private Date marketTime;

    /**
     * The market suspend time
     */
    private Date suspendTime;

    private Date settleTime;

    private MarketBettingType bettingType;

    /**
     * If 'true' the market is set to turn in-play
     */
    private boolean turnInPlayEnabled;

    /**
     * Market base type
     */
    private String marketType;

    /**
     * The market regulator
     */
    private String regulator;

    /**
     * The commission rate applicable to the market
     */
    private double marketBaseRate;

    /**
     * Indicates whether or not the user's discount rate is taken into account on this market. If ‘false’ all users will
     * be charged the same commission rate, regardless of discount rate.
     */
    private boolean discountAllowed;

    /**
     * The wallet to which the market belongs (UK/AUS)
     */
    private String wallet;

    /**
     * The market rules
     */
    private String rules;

    /**
     *
     */
    private boolean rulesHasDate;

    /**
     * Any additional information regarding the market
     */
    private String clarifications;

    public boolean isPersistenceEnabled() {
        return persistenceEnabled;
    }

    public void setPersistenceEnabled(boolean persistenceEnabled) {
        this.persistenceEnabled = persistenceEnabled;
    }

    public boolean isBspMarket() {
        return bspMarket;
    }

    public void setBspMarket(boolean bspMarket) {
        this.bspMarket = bspMarket;
    }

    public Date getMarketTime() {
        return marketTime;
    }

    public void setMarketTime(Date marketTime) {
        this.marketTime = marketTime;
    }

    public Date getSuspendTime() {
        return suspendTime;
    }

    public void setSuspendTime(Date suspendTime) {
        this.suspendTime = suspendTime;
    }

    public Date getSettleTime() {
        return settleTime;
    }

    public void setSettleTime(Date settleTime) {
        this.settleTime = settleTime;
    }

    public MarketBettingType getBettingType() {
        return bettingType;
    }

    public void setBettingType(MarketBettingType bettingType) {
        this.bettingType = bettingType;
    }

    public boolean isTurnInPlayEnabled() {
        return turnInPlayEnabled;
    }

    public void setTurnInPlayEnabled(boolean turnInPlayEnabled) {
        this.turnInPlayEnabled = turnInPlayEnabled;
    }

    public String getMarketType() {
        return marketType;
    }

    public void setMarketType(String marketType) {
        this.marketType = marketType;
    }

    public String getRegulator() {
        return regulator;
    }

    public void setRegulator(String regulator) {
        this.regulator = regulator;
    }

    public double getMarketBaseRate() {
        return marketBaseRate;
    }

    public void setMarketBaseRate(double marketBaseRate) {
        this.marketBaseRate = marketBaseRate;
    }

    public boolean isDiscountAllowed() {
        return discountAllowed;
    }

    public void setDiscountAllowed(boolean discountAllowed) {
        this.discountAllowed = discountAllowed;
    }

    public String getWallet() {
        return wallet;
    }

    public void setWallet(String wallet) {
        this.wallet = wallet;
    }

    public String getRules() {
        return rules;
    }

    public void setRules(String rules) {
        this.rules = rules;
    }

    public boolean isRulesHasDate() {
        return rulesHasDate;
    }

    public void setRulesHasDate(boolean rulesHasDate) {
        this.rulesHasDate = rulesHasDate;
    }

    public String getClarifications() {
        return clarifications;
    }

    public void setClarifications(String clarifications) {
        this.clarifications = clarifications;
    }

    @Override
    public String toString() {
        return "Desc{" +
                "persistenceEnabled=" + persistenceEnabled +
                ", bspMarket=" + bspMarket +
                ", marketTime=" + marketTime +
                ", suspendTime=" + suspendTime +
                ", settleTime=" + settleTime +
                ", bettingType=" + bettingType +
                ", turnInPlayEnabled=" + turnInPlayEnabled +
                ", marketType='" + marketType + '\'' +
                ", regulator='" + regulator + '\'' +
                ", marketBaseRate=" + marketBaseRate +
                ", discountAllowed=" + discountAllowed +
                ", wallet='" + wallet + '\'' +
                ", rulesHasDate=" + rulesHasDate +
                ", clarifications='" + clarifications + '\'' +
                '}';
    }
}
