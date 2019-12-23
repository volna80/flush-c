package com.volna80.betfair.api.model;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class CountryCodeResult {

    private String countryCode;
    private int marketCount;

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public int getMarketCount() {
        return marketCount;
    }

    public void setMarketCount(int marketCount) {
        this.marketCount = marketCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CountryCodeResult that = (CountryCodeResult) o;

        if (!countryCode.equals(that.countryCode)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return countryCode.hashCode();
    }

    @Override
    public String toString() {
        return "Country{" +
                "" + countryCode + '\'' +
                '[' + marketCount + ']' +
                '}';
    }
}
