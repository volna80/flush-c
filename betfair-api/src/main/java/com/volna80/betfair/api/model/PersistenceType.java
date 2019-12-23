package com.volna80.betfair.api.model;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public enum PersistenceType {

    /**
     * Lapse the order at turn-in-play
     */
    LAPSE,


    /**
     * Persist the order to in-play
     */
    PERSIST,


    /**
     * Put the order into the auction (SP) at turn-in-play
     */
    MARKET_ON_CLOSE


}
