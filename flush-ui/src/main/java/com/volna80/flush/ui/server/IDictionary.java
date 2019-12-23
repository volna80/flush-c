package com.volna80.flush.ui.server;

import com.volna80.flush.ui.model.UIMarketInfo;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public interface IDictionary {


    void init();

    UIMarketInfo getMarketInfo(String marketId);
}
