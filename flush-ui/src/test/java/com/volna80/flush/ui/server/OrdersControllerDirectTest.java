package com.volna80.flush.ui.server;

import com.volna80.betfair.api.BetfairException;
import com.volna80.betfair.api.IBetfairAPI;
import com.volna80.betfair.api.model.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * <p>
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class OrdersControllerDirectTest {

    OrdersControllerDirect controller;

    private IBetfairAPI api;
    private FlushAPI refProvider;

    @Before
    public void setup() {

        api = mock(IBetfairAPI.class);
        refProvider = mock(FlushAPI.class);

        controller = new OrdersControllerDirect(refProvider, api);
        controller.setDoneDelay(1);
        controller.setLeaveDelay(1);

    }

    @After
    public void tearDown() {
        controller.shutdown();
    }

    @Test
    public void test() throws BetfairException, InterruptedException {

        CurrentOrderSummary summary_1 = new CurrentOrderSummary();
        summary_1.setBetId("bet1");
        summary_1.setSide(Side.BACK);
        summary_1.setStatus(OrderStatus.EXECUTION_COMPLETE);
        summary_1.setPriceSize(new PriceSize(10, 4));

        CurrentOrderSummaryReport report = new CurrentOrderSummaryReport();
        report.setCurrentOrders(Collections.singletonList(summary_1));
        report.setMoreAvailable(false);

        when(api.listCurrentOrders(null, null, OrderProjection.EXECUTION_COMPLETE, null, null, OrderBy.BY_BET, SortDir.EARLIEST_TO_LATEST, 0, 25)).thenReturn(report);

        controller.init();

        Thread.sleep(2000);

        Collection<CurrentOrderSummary> result = controller.getCompleteOrders();

        assertEquals(1, result.size());
        assertEquals(summary_1, result.iterator().next());

    }

}
