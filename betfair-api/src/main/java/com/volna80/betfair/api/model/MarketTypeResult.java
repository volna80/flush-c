package com.volna80.betfair.api.model;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class MarketTypeResult {

    private String marketType;
    private int marketCount;

    public String getMarketType() {
        return marketType;
    }

    public void setMarketType(String marketType) {
        this.marketType = marketType;
    }

    public int getMarketCount() {
        return marketCount;
    }

    public void setMarketCount(int marketCount) {
        this.marketCount = marketCount;
    }


    @Override
    public String toString() {
        return "MarketType{" +
                marketType + "[" + marketCount + "]" +
                '}';
    }
}
