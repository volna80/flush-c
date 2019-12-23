package com.volna80.betfair.api.model;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public enum GroupBy {

    /**
     * A roll up of settled P&L, commission paid and number of bet orders, on a specified event type
     */
    EVENT_TYPE,
    /**
     * A roll up of settled P&L, commission paid and number of bet orders, on a specified event
     */
    EVENT,
    /**
     * A roll up of settled P&L, commission paid and number of bet orders, on a specified market
     */
    MARKET,
    /**
     * A roll up of settled P&L and the number of bet orders, on a specified runner within a specified market
     */
    RUNNER,
    /**
     * An averaged roll up of settled P&L, and number of bets, on the specified side of a specified selection within a specified market, that are either settled or voided
     */
    SIDE,
    /**
     * The P&L, commission paid, side and regulatory information etc, about each individual bet order
     */
    BET


}
