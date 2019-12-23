package com.volna80.flush.ui.server.marketdata;

import com.volna80.betfair.api.model.MarketBook;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public interface IMarketDataController {

    public void subscribe(String marketId, long selectionId);

    public MarketBook snap(String marketId);

    void unsubscribe(String marketId, long selectionId);
}
