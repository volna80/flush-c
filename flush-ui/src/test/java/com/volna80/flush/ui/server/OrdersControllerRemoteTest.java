package com.volna80.flush.ui.server;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.TestActorRef;
import akka.testkit.TestKit;
import com.volna80.betfair.api.model.CurrentOrderSummary;
import com.volna80.betfair.api.model.Side;
import com.volna80.flush.server.model.ExecutionReport;
import com.volna80.flush.server.model.MessageConverter;
import com.volna80.flush.server.model.NewOrderSingle;
import com.volna80.flush.ui.ApplicationManager;
import com.volna80.flush.ui.IApplicationManager;
import com.volna80.flush.ui.model.UIMarketInfo;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class OrdersControllerRemoteTest extends TestKit {

    public static final String MARKET_ID = "MARKET_ID";
    public static final String CORRELATION_ID = "CORRELATION_ID";
    public static final int SELECTION_ID = 1;

    static ActorSystem _system = ActorSystem.create("test");

    private IApplicationManager manager;
    private IFlushAPI api;
    private IDictionary dictionary;

    public OrdersControllerRemoteTest() {
        super(_system);
    }

    @AfterClass
    public static void tearDown() {
        shutdownActorSystem(_system, Duration.create(100, TimeUnit.MILLISECONDS), false);
    }

    @Before
    public void setup() {
        manager = mock(IApplicationManager.class);
        ApplicationManager.setInstance(manager);

        dictionary = mock(IDictionary.class);
        when(dictionary.getMarketInfo(MARKET_ID)).thenReturn(new UIMarketInfo(MARKET_ID));

        api = mock(IFlushAPI.class);

    }

    @Test
    public void placeNewOrder() {

        Props props = OrdersControllerRemote.props(super.testActor(), dictionary);
        TestActorRef<OrdersControllerRemote> controller = TestActorRef.create(_system, props);

        controller.underlyingActor().placeNewOrder(MARKET_ID, SELECTION_ID, 10000, 10100, Side.LAY, CORRELATION_ID);

        NewOrderSingle nos = (NewOrderSingle) receiveOne(Duration.create(100, TimeUnit.MILLISECONDS));

        assertEquals(MARKET_ID, nos.getMarketId());
        assertEquals(CORRELATION_ID, nos.getCorrelationId());
        assertEquals(10000, nos.getPrice());
        assertEquals(10100, nos.getOrderQty());
        assertEquals(SELECTION_ID, nos.getSelectionId());
        assertEquals(Side.LAY, nos.getSide());


        assertEquals(1, controller.underlyingActor().getUnconfirmedOrders(MARKET_ID, SELECTION_ID).size());
        assertEquals(0, controller.underlyingActor().getExecutableOrders(MARKET_ID, SELECTION_ID).size());

        CurrentOrderSummary summary = controller.underlyingActor().getUnconfirmedOrders(MARKET_ID, SELECTION_ID).iterator().next();
        assertEquals(CORRELATION_ID, summary.getBetId());

        ExecutionReport er = MessageConverter.makeAck(nos);

        controller.tell(er, testActor());

        assertEquals(0, controller.underlyingActor().getUnconfirmedOrders(MARKET_ID, SELECTION_ID).size());
        assertEquals(1, controller.underlyingActor().getExecutableOrders(MARKET_ID, SELECTION_ID).size());

        summary = controller.underlyingActor().getExecutableOrders(MARKET_ID, SELECTION_ID).iterator().next();
        assertEquals(MARKET_ID, summary.getMarketId());
        assertEquals(CORRELATION_ID, summary.getBetId());
        assertEquals(SELECTION_ID, summary.getSelectionId());
        assertEquals(10000, summary.getPriceSize().getPrice());
        assertEquals(10100, summary.getPriceSize().getSize());
        assertEquals(10100, summary.getSizeRemaining());
        assertEquals(0, summary.getSizeMatched());


    }


}
