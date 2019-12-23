package com.volna80.betfair.api;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class BetfairException extends Exception {

    public BetfairException(String message) {
        super(message);
    }

    public BetfairException(String message, Throwable cause) {
        super(message, cause);
    }
}
