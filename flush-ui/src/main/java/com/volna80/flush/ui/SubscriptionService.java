package com.volna80.flush.ui;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import javafx.application.Platform;

/**
 * <p>
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class SubscriptionService implements ISubscriptionService {

    private final EventBus eventBus;


    public SubscriptionService() {
        eventBus = new AsyncEventBus(Platform::runLater);

    }

    @Override
    public void register(Object listener) {
        eventBus.register(listener);
    }

    @Override
    public void post(Object object) {
        eventBus.post(object);
    }
}
