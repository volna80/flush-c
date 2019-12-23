package com.volna80.flush.ui;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class SubscriptionServiceDummy implements ISubscriptionService {

    private LinkedBlockingQueue queue = new LinkedBlockingQueue();

    @Override
    public void register(Object listener) {

    }

    @Override
    public void post(Object object) {
        try {
            queue.put(object);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Object poll() {
        try {
            return queue.poll(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
