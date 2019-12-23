package com.volna80.betfair.api.model;

import com.google.gson.annotations.JsonAdapter;
import com.volna80.betfair.api.model.adapter.PriceAdapter;
import com.volna80.betfair.api.model.adapter.SizeAdapter;

import static com.volna80.betfair.api.util.JsonBuilder.append;
import static com.volna80.betfair.api.util.StringUtils.removeLastChar;

/**
 * Place a new LIMIT order (simple exchange bet for immediate execution)
 * <p/>
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class LimitOrder implements JsonMessage {

    /**
     * The size of the bet.
     */
    @JsonAdapter(SizeAdapter.class)
    private int size;

    /**
     * The limit price
     */
    @JsonAdapter(PriceAdapter.class)
    private int price;

    /**
     * What to do with the order at turn-in-play
     */
    private PersistenceType persistenceType;
    private String cache = null;

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public PersistenceType getPersistenceType() {
        return persistenceType;
    }

    public void setPersistenceType(PersistenceType persistenceType) {
        this.persistenceType = persistenceType;
    }

    @Override
    public String toJson() {
        if (cache != null) {
            return cache;
        }

        StringBuilder builder = new StringBuilder();
        builder.append('{');
        append(builder, "size", Precision2.toExternal(size));
        append(builder, "price", Precision2.toExternal(price));
        append(builder, "persistenceType", persistenceType);
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
