package com.volna80.flush.ui;

/**
 * Abstract subscription service
 * <p>
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public interface ISubscriptionService {

    /**
     * add new listener
     */
    void register(Object listener);

    void post(Object object);
}
