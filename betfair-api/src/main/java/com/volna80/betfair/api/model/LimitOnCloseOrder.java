package com.volna80.betfair.api.model;

import com.google.gson.annotations.JsonAdapter;
import com.volna80.betfair.api.model.adapter.LiabilityAdapter;
import com.volna80.betfair.api.model.adapter.PriceAdapter;

import static com.volna80.betfair.api.util.JsonBuilder.append;
import static com.volna80.betfair.api.util.StringUtils.removeLastChar;

/**
 * Place a new LIMIT_ON_CLOSE bet
 * <p/>
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class LimitOnCloseOrder implements JsonMessage {

    /**
     * The size of the bet.
     */
    @JsonAdapter(LiabilityAdapter.class)
    private int liability;

    /**
     * The limit price of the bet if LOC
     */
    @JsonAdapter(PriceAdapter.class)
    private int price;
    private String cache = null;

    public int getLiability() {
        return liability;
    }

    public void setLiability(int liability) {
        this.liability = liability;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    @Override
    public String toJson() {
        if (cache != null) {
            return cache;
        }

        StringBuilder builder = new StringBuilder();
        builder.append('{');
        append(builder, "price", Precision2.toExternal(price));
        append(builder, "liability", Precision2.toExternal(liability));
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
