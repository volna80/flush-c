package com.volna80.betfair.api.model;

import com.google.gson.annotations.JsonAdapter;
import com.volna80.betfair.api.model.adapter.NewPriceAdapter;

/**
 * Instruction to replace a LIMIT or LIMIT_ON_CLOSE order at a new price. Original order will be cancelled and a new order placed at the new price for the remaining stake.
 * <p/>
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class ReplaceInstruction {

    /**
     * Unique identifier for the bet
     */
    private String betId;


    /**
     * The price to replace the bet at
     */
    @JsonAdapter(NewPriceAdapter.class)
    private int newPrice;

    public String getBetId() {
        return betId;
    }

    public void setBetId(String betId) {
        this.betId = betId;
    }

    public int getNewPrice() {
        return newPrice;
    }

    public void setNewPrice(int newPrice) {
        this.newPrice = newPrice;
    }

    @Override
    public String toString() {
        return "replace[" + betId + '|' + newPrice + ']';
    }
}


