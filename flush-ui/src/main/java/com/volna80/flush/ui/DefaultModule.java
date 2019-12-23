package com.volna80.flush.ui;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.volna80.flush.ui.server.IDictionary;
import com.volna80.flush.ui.server.IFlushAPI;
import com.volna80.flush.ui.server.IOrdersController;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class DefaultModule extends AbstractModule {

    private final IOrdersController controller;
    private final IFlushAPI api;
    private final ISubscriptionService subscriptionService;

    private final String marketId;
    private final Long selectionId;


    public DefaultModule(IOrdersController controller, IFlushAPI api, ISubscriptionService subscriptionService) {
        this.controller = controller;
        this.api = api;
        this.subscriptionService = subscriptionService;
        this.marketId = null;
        this.selectionId = null;
    }

    public DefaultModule(IOrdersController controller, IFlushAPI api, ISubscriptionService subscriptionService, String marketId, Long selectionId) {
        this.controller = controller;
        this.api = api;
        this.subscriptionService = subscriptionService;
        this.marketId = marketId;
        this.selectionId = selectionId;
    }

    @Override
    protected void configure() {
        if (controller != null) bind(IOrdersController.class).toInstance(controller);
        if (api != null) bind(IFlushAPI.class).toInstance(api);
        if (api != null && api.getDictionary() != null) bind(IDictionary.class).toInstance(api.getDictionary());
        if (subscriptionService != null) bind(ISubscriptionService.class).toInstance(subscriptionService);
        if (marketId != null) {
            bind(String.class).annotatedWith(Names.named("marketId")).toInstance(marketId);
        }
        if (selectionId != null) {
            bind(Long.class).annotatedWith(Names.named("selectionId")).toInstance(selectionId);
        }
    }
}
