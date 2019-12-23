package com.volna80.betfair.api.model;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public enum OrderStatus {

    /**
     * An order that does not have any remaining unmatched portion.
     */
    EXECUTION_COMPLETE,


    /**
     * An order that has a remaining unmatched portion.
     */
    EXECUTABLE


}
