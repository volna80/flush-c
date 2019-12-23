package com.volna80.flush.server.model;

import com.volna80.betfair.api.model.Side;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class TicketTest {


    @Test
    public void basic() {

        NewOrderSingle nos = new NewOrderSingle();
        nos.setCorrelationId("COR-1");
        nos.setMarketId("MARKET");
        nos.setOrderQty(100);
        nos.setPrice(101);
        nos.setSelectionId(12345);
        nos.setSide(Side.LAY);

        Ticket ticket = new Ticket(nos);

        assertEquals("COR-1", ticket.getCorrelationId());
        assertEquals(100, ticket.getOrderQty());
        assertEquals(0, ticket.getCumQty());
        assertEquals(100, ticket.getLeavesQty());
        assertFalse(ticket.isCompleted());


    }

}
