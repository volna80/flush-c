package com.volna80.betfair.api;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public interface IBetfairApiFactory {

    /**
     * init a factory before the usage
     */
    void init();

    /**
     * A factory must be closed to avoid memory leaks
     */
    void close();

    IBetfairAPI makeService();
}
