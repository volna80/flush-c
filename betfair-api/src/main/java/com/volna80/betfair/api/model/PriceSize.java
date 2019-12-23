package com.volna80.betfair.api.model;

import com.google.gson.annotations.JsonAdapter;
import com.volna80.betfair.api.model.adapter.PriceAdapter;
import com.volna80.betfair.api.model.adapter.SizeAdapter;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class PriceSize {

    @JsonAdapter(PriceAdapter.class)
    private int price;
    @JsonAdapter(SizeAdapter.class)
    private int size;

    public PriceSize() {
    }

    public PriceSize(int price, int size) {
        this.price = price;
        this.size = size;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PriceSize priceSize = (PriceSize) o;

        if (price != priceSize.price) return false;
        if (size != priceSize.size) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = price;
        result = 31 * result + size;
        return result;
    }

    @Override
    public String toString() {
        return "" + '[' + size + '@' + price + ']';
    }


}
