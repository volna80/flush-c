package com.volna80.flush.server.model;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public enum ExecType {
    NEW,
    CANCELLED,
    TRADE,
    /**
     * Just acknowledge that a server side got an order a client side
     */
    PENDING_NEW,
    REJECTED
}
