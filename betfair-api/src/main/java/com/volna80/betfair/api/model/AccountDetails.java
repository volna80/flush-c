package com.volna80.betfair.api.model;

/**
 * Response for Account details.
 * <p>
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class AccountDetails {

    //user time zone
    private String timezone;

    private String region;

    //the betfair points balance
    private int pointsBalance;

    //locale code
    private String localeCode;

    private String lastName;
    private String firstName;

    private double discountRate;

    //default user currency code
    private String currencyCode;

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public int getPointsBalance() {
        return pointsBalance;
    }

    public void setPointsBalance(int pointsBalance) {
        this.pointsBalance = pointsBalance;
    }

    public String getLocaleCode() {
        return localeCode;
    }

    public void setLocaleCode(String localeCode) {
        this.localeCode = localeCode;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public double getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(double discountRate) {
        this.discountRate = discountRate;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }


    @Override
    public String toString() {
        return "AccountDetails{" +
                "timezone='" + timezone + '\'' +
                ", region='" + region + '\'' +
                ", pointsBalance=" + pointsBalance +
                ", localeCode='" + localeCode + '\'' +
                ", lastName='" + lastName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", discountRate=" + discountRate +
                ", currencyCode='" + currencyCode + '\'' +
                '}';
    }
}
