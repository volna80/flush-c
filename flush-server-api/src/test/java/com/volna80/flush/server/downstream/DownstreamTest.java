package com.volna80.flush.server.downstream;

import akka.actor.Props;
import akka.testkit.TestActorRef;
import com.volna80.betfair.api.BetfairException;
import com.volna80.betfair.api.IBetfairAPI;
import com.volna80.betfair.api.model.*;
import com.volna80.flush.server.IReferenceProvider;
import com.volna80.flush.server.model.*;
import com.volna80.flush.server.tests.ActorTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import scala.concurrent.duration.Duration;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class DownstreamTest extends ActorTest {

    public static final String CORRELATION_ID = "COR_ID";
    private static final String MARKET_ID = "MARKET_ID";
    private IBetfairAPI api;
    private IReferenceProvider referenceProvider;
    private TestActorRef<Downstream> downstream;

    public DownstreamTest() {
        super("downstream");
    }

    @Before
    public void setup() {

        api = Mockito.mock(IBetfairAPI.class);

        referenceProvider = Mockito.mock(IReferenceProvider.class);
        when(referenceProvider.nextRef()).thenReturn("REF_ID");

        Props props = Downstream.props(api, MARKET_ID, referenceProvider);
        downstream = TestActorRef.create(system(), props);

    }

    @Test
    public void placeNewOrderAndCancel() throws BetfairException {


        NewOrderSingle nos = new NewOrderSingle();
        nos.setCorrelationId(CORRELATION_ID);
        nos.setMarketId(MARKET_ID);
        nos.setSelectionId(101);
        nos.setSide(Side.LAY);
        nos.setOrderQty(500);
        nos.setPrice(700);

        PlaceExecutionReport placeExecutionReport = new PlaceExecutionReport();
        placeExecutionReport.setStatus(ExecutionReportStatus.SUCCESS);

        when(api.placeOrders(eq(MARKET_ID), argThat(isValid(nos)), eq(CORRELATION_ID))).thenReturn(placeExecutionReport);


        CurrentOrderSummary summary = new CurrentOrderSummary();
        summary.setSizeMatched(0);
        summary.setPriceSize(new PriceSize(nos.getPrice(), nos.getOrderQty()));
        summary.setStatus(OrderStatus.EXECUTABLE);
        summary.setSizeRemaining(nos.getOrderQty());
        summary.setSide(nos.getSide());
        summary.setBetId("BET_ID");
        summary.setMarketId(MARKET_ID);
        summary.setSelectionId(nos.getSelectionId());
        CurrentOrderSummaryReport summaryReport = new CurrentOrderSummaryReport();
        summaryReport.setCurrentOrders(Collections.singletonList(summary));

        when(api.listCurrentOrders(isNull(), any(Set.class), any(OrderProjection.class), isNull(), isNull(),
                any(OrderBy.class), any(SortDir.class), anyInt(), anyInt())).thenReturn(summaryReport);

        downstream.tell(nos, this.testActor());

        ExecutionReport er = (ExecutionReport) receiveOne(Duration.create(100, TimeUnit.MILLISECONDS));

        assertEquals(MARKET_ID, er.getMarketId());
        assertEquals(101, er.getSelectionId());
        assertEquals("BET_ID", er.getBetId());
        assertEquals(CORRELATION_ID, er.getCorrelationId());

        assertEquals(nos.getOrderQty(), er.getOrderQty());
        assertEquals(nos.getPrice(), er.getPrice());
        assertEquals(0, er.getCumQty());
        assertEquals(nos.getOrderQty(), er.getLeavesQty());

        assertEquals(OrderStatus.EXECUTABLE, er.getStatus());
        assertEquals(ExecType.NEW, er.getExecType());


        OrderCancelRequest ocr = MessageConverter.makeCancel(er);
        when(api.cancelOrders(eq(MARKET_ID), argThat(isValid(ocr)), anyString())).thenReturn(new CancelExecutionReport());

        downstream.underlyingActor().onOrderCancelRequest(ocr);

    }


    @Test(expected = RuntimeException.class)
    public void placeNewOrderAndCouldNotFindBetId() throws BetfairException {
        NewOrderSingle nos = new NewOrderSingle();
        nos.setCorrelationId(CORRELATION_ID);
        nos.setMarketId(MARKET_ID);
        nos.setSelectionId(101);
        nos.setSide(Side.LAY);
        nos.setOrderQty(500);
        nos.setPrice(700);

        PlaceExecutionReport placeExecutionReport = new PlaceExecutionReport();
        placeExecutionReport.setStatus(ExecutionReportStatus.SUCCESS);

        when(api.placeOrders(eq(MARKET_ID), argThat(isValid(nos)), eq(CORRELATION_ID))).thenReturn(placeExecutionReport);

        CurrentOrderSummary summary = new CurrentOrderSummary();
        summary.setSizeMatched(0);
        summary.setPriceSize(new PriceSize(nos.getPrice(), nos.getOrderQty() + 10));
        summary.setStatus(OrderStatus.EXECUTABLE);
        summary.setSizeRemaining(nos.getOrderQty());
        summary.setSide(nos.getSide());
        summary.setBetId("BET_ID");
        summary.setMarketId(MARKET_ID);
        summary.setSelectionId(nos.getSelectionId());
        CurrentOrderSummaryReport summaryReport = new CurrentOrderSummaryReport();
        summaryReport.setCurrentOrders(Collections.singletonList(summary));

        when(api.listCurrentOrders(any(Set.class), any(Set.class), any(OrderProjection.class), any(TimeRange.class), any(TimeRange.class),
                any(OrderBy.class), any(SortDir.class), anyInt(), anyInt())).thenReturn(summaryReport);

        downstream.underlyingActor().onNewOrderSingle(nos);
    }

    private ArgumentMatcher<List<PlaceInstruction>> isValid(final NewOrderSingle nos) {
        return new ArgumentMatcher<List<PlaceInstruction>>() {
            @Override
            public boolean matches(List<PlaceInstruction> placeInstructions) {
                PlaceInstruction instruction = ((List<PlaceInstruction>) placeInstructions).get(0);

                return nos.getOrderQty() == instruction.getLimitOrder().getSize()
                        && nos.getPrice() == instruction.getLimitOrder().getPrice()
                        && nos.getSide() == instruction.getSide()
                        && nos.getSelectionId() == instruction.getSelectionId();
            }
        };
    }

    private ArgumentMatcher<List<CancelInstruction>> isValid(final OrderCancelRequest ocr) {
        return new ArgumentMatcher<List<CancelInstruction>>() {
            @Override
            public boolean matches(List<CancelInstruction> argument) {
                CancelInstruction instruction = ((List<CancelInstruction>) argument).get(0);
                return instruction.getBetId().equals(ocr.getBetId());
            }
        };
    }


}
