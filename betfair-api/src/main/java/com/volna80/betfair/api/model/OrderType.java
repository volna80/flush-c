package com.volna80.betfair.api.model;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public enum OrderType {

    /**
     * A normal exchange limit order for immediate execution
     */
    LIMIT,
    /**
     * Limit order for the auction (SP)
     */
    LIMIT_ON_CLOSE,

    /**
     * Market order for the auction (SP)
     */
    MARKET_ON_CLOSE


}
