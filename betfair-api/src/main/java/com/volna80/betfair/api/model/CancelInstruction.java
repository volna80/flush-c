package com.volna80.betfair.api.model;

import static com.volna80.betfair.api.util.JsonBuilder.append;
import static com.volna80.betfair.api.util.StringUtils.removeLastChar;

/**
 * Instruction to fully or partially cancel an order (only applies to LIMIT orders)
 * <p/>
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class CancelInstruction implements JsonMessage {

    private String betId;

    /**
     * If supplied then this is a partial cancel.  Should be set to 'null' if no size reduction is required.
     */
    private Double sizeReduction;
    private String cache = null;

    public String getBetId() {
        return betId;
    }

    public void setBetId(String betId) {
        this.betId = betId;
    }

    public Double getSizeReduction() {
        return sizeReduction;
    }

    public void setSizeReduction(Double sizeReduction) {
        this.sizeReduction = sizeReduction;
    }

    @Override
    public String toJson() {
        if (cache != null) {
            return cache;
        }

        StringBuilder builder = new StringBuilder();
        builder.append('{');
        append(builder, "betId", betId);
        append(builder, "sizeReduction", sizeReduction);
//        builder.append('"').append("sizeReduction").append("\":").append(sizeReduction != null ? sizeReduction : "\"null\"").append(",");
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
