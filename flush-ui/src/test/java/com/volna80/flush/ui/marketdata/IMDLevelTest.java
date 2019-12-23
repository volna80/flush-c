package com.volna80.flush.ui.marketdata;

import com.google.common.collect.Lists;
import com.volna80.betfair.api.model.ExchangePrices;
import com.volna80.betfair.api.model.PriceSize;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class IMDLevelTest {

    @Test
    public void midPrice() {
        ExchangePrices ep = new ExchangePrices();
        //no
        ep.setAvailableToBack(Lists.newArrayList(new PriceSize(100, 1), new PriceSize(250, 2)));
        //yes
        ep.setAvailableToLay(Lists.newArrayList(new PriceSize(300, 3), new PriceSize(3000, 5)));

        assertEquals(274, IMDLevel.getMidPrice(ep, IMDLevel.Type.STANDARD));
    }

    @Test
    public void midPrice2() {
        ExchangePrices ep = new ExchangePrices();
        //no
        ep.setAvailableToBack(Lists.newArrayList(new PriceSize(9000, 1), new PriceSize(8000, 2)));
        //yes
        ep.setAvailableToLay(Lists.newArrayList(new PriceSize(10000, 3)));

        assertEquals(9500, IMDLevel.getMidPrice(ep, IMDLevel.Type.STANDARD));
    }
}
