package com.volna80.betfair.api.model;

import com.volna80.betfair.api.util.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class PriceProjection implements JsonMessage {


    /**
     * The basic price data you want to receive in the response.
     */
    private Set<PriceData> priceData;


    /**
     * Options to alter the default representation of best offer prices Applicable to EX_BEST_OFFERS priceData selection
     */
    private ExBestOffersOverrides exBestOffersOverrides;


    /**
     * Indicates if the returned prices should include virtual prices. Applicable to EX_BEST_OFFERS and EX_ALL_OFFERS priceData selections, default value is false.
     */
    private Boolean virtualise;


    /**
     * Indicates if the volume returned at each price point should be the absolute value or a cumulative sum of volumes
     * available at the price and all better prices. If unspecified defaults to false. Applicable to EX_BEST_OFFERS and EX_ALL_OFFERS price projections.
     * <p/>
     * Not supported as yet.
     */
    private Boolean rolloverStakes;
    private String cache;

    public Set<PriceData> getPriceData() {
        return priceData;
    }

    public void setPriceData(Set<PriceData> priceData) {
        this.priceData = priceData;
    }

    public void setPriceData(PriceData... priceData) {
        this.priceData = new HashSet<>(Arrays.asList(priceData));
    }

    public ExBestOffersOverrides getExBestOffersOverrides() {
        return exBestOffersOverrides;
    }

    public void setExBestOffersOverrides(ExBestOffersOverrides exBestOffersOverrides) {
        this.exBestOffersOverrides = exBestOffersOverrides;
    }

    public Boolean isVirtualise() {
        return virtualise;
    }

    /**
     * @see #virtualise
     */
    public void setVirtualise(boolean virtualise) {
        this.virtualise = virtualise;
    }

    public Boolean isRolloverStakes() {
        return rolloverStakes;
    }

    public void setRolloverStakes(boolean rolloverStakes) {
        this.rolloverStakes = rolloverStakes;
    }

    @Override
    public String toString() {
        return "PriceProjection{" +
                "priceData=" + priceData +
                ", exBestOffersOverrides=" + exBestOffersOverrides +
                ", virtualise=" + virtualise +
                ", rolloverStakes=" + rolloverStakes +
                '}';
    }

    @Override
    public String toJson() {

        if (cache != null) {
            return cache;
        }

        StringBuilder json = new StringBuilder();
        json.append('{');

        if (priceData != null && priceData.size() > 0) {
            json.append('"').append("priceData").append("\":");
            json.append('[');
            Iterator<PriceData> i = priceData.iterator();
            boolean hasNext = i.hasNext();
            while (hasNext) {
                json.append('"').append(i.next()).append('"');
                if (hasNext = i.hasNext()) {
                    json.append(',');
                }
            }
            json.append(']');
            json.append(',');
        }

        if (exBestOffersOverrides != null) {
            json.append("\"exBestOffersOverrides\":").append(exBestOffersOverrides.toJson());
            json.append(',');
        }

        if (virtualise != null) {
            json.append("\"virtualise\":").append(virtualise).append(',');
        }

        if (rolloverStakes != null) {
            json.append("\"rolloverStakes\":").append(rolloverStakes);
        }


        StringUtils.removeLastChar(json, ',');


        json.append('}');

        cache = json.toString();
        return cache;
    }

}
