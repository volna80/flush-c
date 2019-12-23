package com.volna80.betfair.api.model;

import static com.volna80.betfair.api.util.JsonBuilder.append;
import static com.volna80.betfair.api.util.StringUtils.removeLastChar;

/**
 * Place a new MARKET_ON_CLOSE bet
 * <p/>
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class MarketOnCloseOrder implements JsonMessage {

    /**
     * The size of the bet.
     */
    private double liability;
    private String cache = null;

    public double getLiability() {
        return liability;
    }

    public void setLiability(double liability) {
        this.liability = liability;
    }

    @Override
    public String toJson() {
        if (cache != null) {
            return cache;
        }

        StringBuilder builder = new StringBuilder();
        builder.append('{');
        append(builder, "liability", liability);
        removeLastChar(builder, ',');
        builder.append('}');

        cache = builder.toString();

        return cache;
    }

    @Override
    public String toString() {
        return toJson();
    }
}
