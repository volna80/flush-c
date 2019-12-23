package com.volna80.betfair.api.model;

import java.util.List;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class ExchangePrices {

    private List<PriceSize> availableToBack;

    private List<PriceSize> availableToLay;

    private List<PriceSize> tradedVolume;

    public List<PriceSize> getAvailableToBack() {
        return availableToBack;
    }

    public void setAvailableToBack(List<PriceSize> availableToBack) {
        this.availableToBack = availableToBack;
    }

    public List<PriceSize> getAvailableToLay() {
        return availableToLay;
    }

    public void setAvailableToLay(List<PriceSize> availableToLay) {
        this.availableToLay = availableToLay;
    }

    public List<PriceSize> getTradedVolume() {
        return tradedVolume;
    }

    public void setTradedVolume(List<PriceSize> tradedVolume) {
        this.tradedVolume = tradedVolume;
    }

    @Override
    public String toString() {
        return "ExchangePrices{" +
                "availableToBack=" + availableToBack +
                ", availableToLay=" + availableToLay +
                ", tradedVolume=" + tradedVolume +
                '}';
    }

}
