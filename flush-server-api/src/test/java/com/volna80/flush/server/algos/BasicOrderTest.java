package com.volna80.flush.server.algos;

import akka.actor.Terminated;
import akka.testkit.TestActorRef;
import com.volna80.betfair.api.model.ExecutionReportErrorCode;
import com.volna80.betfair.api.model.OrderStatus;
import com.volna80.betfair.api.model.Side;
import com.volna80.flush.server.model.*;
import com.volna80.flush.server.tests.ActorTest;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class BasicOrderTest extends ActorTest {


    public static final String CORRELATION_ID = "COR_ID";
    public static final String BET_ID = "BET_ID";
    public static final int SELECTION_ID = 101;
    private static final String MARKET_ID = "MARKET_ID";
    TestActorRef<BasicOrder> actorRef;

    public BasicOrderTest() {
        super("basic-order");
    }

    @Before
    public void setup() {
        actorRef = TestActorRef.create(system(), BasicOrder.props(testActor()));
        watch(actorRef);
    }

    @Test
    public void simpleUno() {

        NewOrderSingle nos = makeNOS();
        actorRef.tell(nos, testActor());


        NewOrderSingle nos_to_downstream = expectMsg(nos);

        //from algo to upstream
        expectMsg(
                new ExecutionReport()
                        .setCorrelationId(CORRELATION_ID)
                        .setExecType(ExecType.PENDING_NEW)
                        .setSelectionId(SELECTION_ID)
                        .setMarketId(MARKET_ID)
                        .setOrderQty(nos.getOrderQty())
                        .setLeavesQty(nos.getOrderQty())
                        .setPrice(nos.getPrice())
                        .setStatus(OrderStatus.EXECUTABLE)
        );


        //send ack from downstream
        ExecutionReport ack = MessageConverter.makeAck(nos_to_downstream);
        ack.setBetId(BET_ID);
        ack.setExecType(ExecType.NEW);
        actorRef.tell(ack, testActor());

        expectMsg(ack);
        expectNoMsg();

        //send fill
        ExecutionReport fill = new ExecutionReport(ack)
                .setExecType(ExecType.TRADE)
                .setStatus(OrderStatus.EXECUTION_COMPLETE)
                .setCumQty(500)
                .setLeavesQty(0);

        actorRef.tell(fill, testActor());

        expectMsg(fill);
        expectTerminated(actorRef, TIMEOUT);
    }


    @Test
    public void cancelUnackOrder() {

        NewOrderSingle nos = makeNOS();
        actorRef.tell(nos, testActor());


        NewOrderSingle nos_to_downstream = expectMsg(nos);

        //from algo to upstream
        ExecutionReport er_to_client_1 = expectMsg(
                new ExecutionReport()
                        .setCorrelationId(CORRELATION_ID)
                        .setExecType(ExecType.PENDING_NEW)
                        .setSelectionId(SELECTION_ID)
                        .setMarketId(MARKET_ID)
                        .setOrderQty(nos.getOrderQty())
                        .setLeavesQty(nos.getOrderQty())
                        .setPrice(nos.getPrice())
                        .setStatus(OrderStatus.EXECUTABLE)
        );

        expectNoMsg();


        OrderCancelRequest ocr = MessageConverter.makeCancel(er_to_client_1);
        assertNull(ocr.getBetId());

        actorRef.tell(ocr, testActor());
        expectNoMsg(TOUT);

        //send ack from downstream
        ExecutionReport ack = MessageConverter.makeAck(nos_to_downstream);
        ack.setBetId(BET_ID);
        ack.setExecType(ExecType.NEW);
        actorRef.tell(ack, testActor());

        expectMsg(ack);

        //cancel to downstream
        expectMsg(new OrderCancelRequest().setCorrelationId(CORRELATION_ID).setBetId(BET_ID).setMarketId(MARKET_ID));

    }

    @Test
    public void cancelUnackOrder_2() {

        NewOrderSingle nos = makeNOS();
        actorRef.tell(nos, testActor());


        NewOrderSingle nos_to_downstream = expectMsg(nos);

        //from algo to upstream
        ExecutionReport er_to_client_1 = expectMsg(
                new ExecutionReport()
                        .setCorrelationId(CORRELATION_ID)
                        .setExecType(ExecType.PENDING_NEW)
                        .setSelectionId(SELECTION_ID)
                        .setMarketId(MARKET_ID)
                        .setOrderQty(nos.getOrderQty())
                        .setLeavesQty(nos.getOrderQty())
                        .setPrice(nos.getPrice())
                        .setStatus(OrderStatus.EXECUTABLE)
        );

        expectNoMsg();

        //send ack from downstream
        ExecutionReport ack = MessageConverter.makeAck(nos_to_downstream);
        ack.setBetId(BET_ID);
        ack.setExecType(ExecType.NEW);
        actorRef.tell(ack, testActor());

        expectMsg(ack);
        expectNoMsg();


        {
            //send a cancel from a client to algo without bet id
            OrderCancelRequest ocr = MessageConverter.makeCancel(er_to_client_1);
            assertNull(ocr.getBetId());
            actorRef.tell(ocr, testActor());
        }

        {
            //cancel to downstream recieved with bet id
            expectMsg(new OrderCancelRequest().setCorrelationId(CORRELATION_ID).setBetId(BET_ID).setMarketId(MARKET_ID));
        }


    }

    @Test
    public void placeAnOrderWhenMarketIsSuspended() {

        NewOrderSingle clientNos = makeNOS();
        actorRef.tell(clientNos, testActor());

        NewOrderSingle algoNos = expectMsg(clientNos);
        ExecutionReport clientER1 = expectMsgClass(ExecutionReport.class);
        assertEquals(ExecType.PENDING_NEW, clientER1.getExecType());
        assertEquals(OrderStatus.EXECUTABLE, clientER1.getStatus());
        expectNoMsg();


        // ER[145427068796318|null|EXECUTION_COMPLETE|REJECTED|179@400|0/0|MARKET_SUSPENDED]
        ExecutionReport algoER1 = MessageConverter.makeReject(algoNos, "MARKET_SUSPENDED", ExecutionReportErrorCode.MARKET_SUSPENDED);
        actorRef.tell(algoER1, testActor());

        NewOrderSingle algoNos2 = expectMsg(clientNos);
        assertTrue(algoNos.getCorrelationId().equals(algoNos2.getCorrelationId()));
        expectNoMsg();

        ExecutionReport ack = MessageConverter.makeAck(algoNos2);
        ack.setBetId(BET_ID);
        ack.setExecType(ExecType.NEW);
        actorRef.tell(ack, testActor());
        expectMsg(ack);
        expectNoMsg();
    }

    @Test
    public void placeAnOrderWhenMarketIsSuspended_CancelCase() {

        NewOrderSingle clientNos = makeNOS();
        actorRef.tell(clientNos, testActor());

        NewOrderSingle algoNos = expectMsg(clientNos);
        ExecutionReport clientER1 = expectMsgClass(ExecutionReport.class);
        assertEquals(ExecType.PENDING_NEW, clientER1.getExecType());
        assertEquals(OrderStatus.EXECUTABLE, clientER1.getStatus());
        expectNoMsg();


        // ER[145427068796318|null|EXECUTION_COMPLETE|REJECTED|179@400|0/0|MARKET_SUSPENDED]
        ExecutionReport algoER1 = MessageConverter.makeReject(algoNos, "MARKET_SUSPENDED", ExecutionReportErrorCode.MARKET_SUSPENDED);
        actorRef.tell(algoER1, testActor());

        NewOrderSingle algoNos2 = expectMsg(clientNos);
        assertTrue(algoNos.getCorrelationId().equals(algoNos2.getCorrelationId()));
        expectNoMsg();

        //a client cancel an order
        OrderCancelRequest ocr = MessageConverter.makeCancel(clientER1);
        assertNull(ocr.getBetId());

        actorRef.tell(ocr, testActor());
        expectNoMsg(TOUT);

        ExecutionReport algoER2 = MessageConverter.makeReject(algoNos2, "MARKET_SUSPENDED", ExecutionReportErrorCode.MARKET_SUSPENDED);
        actorRef.tell(algoER2, testActor());

        ExecutionReport clientER2 = expectMsgClass(ExecutionReport.class);
        assertEquals(ExecType.REJECTED, clientER2.getExecType());
        assertEquals(OrderStatus.EXECUTION_COMPLETE, clientER2.getStatus());
        assertEquals(clientNos.getCorrelationId(), clientER2.getCorrelationId());

        expectMsgClass(Terminated.class);
        expectNoMsg(TOUT);


    }

    @Test
    public void cancelOrderWhenMarketIsSuspended() {

        NewOrderSingle nos = makeNOS();
        actorRef.tell(nos, testActor());


        NewOrderSingle nos_to_downstream = expectMsg(nos);

        //from algo to upstream
        ExecutionReport er_to_client_1 = expectMsg(
                new ExecutionReport()
                        .setCorrelationId(CORRELATION_ID)
                        .setExecType(ExecType.PENDING_NEW)
                        .setSelectionId(SELECTION_ID)
                        .setMarketId(MARKET_ID)
                        .setOrderQty(nos.getOrderQty())
                        .setLeavesQty(nos.getOrderQty())
                        .setPrice(nos.getPrice())
                        .setStatus(OrderStatus.EXECUTABLE)
        );

        expectNoMsg();

        //send ack from downstream
        ExecutionReport ack = MessageConverter.makeAck(nos_to_downstream);
        ack.setBetId(BET_ID);
        ack.setExecType(ExecType.NEW);
        actorRef.tell(ack, testActor());
        ack = expectMsg(ack);


        OrderCancelRequest ocr = MessageConverter.makeCancel(ack);
        actorRef.tell(ocr, testActor());
        //cancel to downstream
        ocr = expectMsg(new OrderCancelRequest().setCorrelationId(CORRELATION_ID).setBetId(BET_ID).setMarketId(MARKET_ID));

        OrderCancelReject reject = MessageConverter.makeReject(ocr, "").setErrorCode(ExecutionReportErrorCode.MARKET_SUSPENDED);
        actorRef.tell(reject, testActor());

        //it resends it again
        expectMsg(ocr);
        expectNoMsg();

    }


    private NewOrderSingle makeNOS() {
        NewOrderSingle nos = new NewOrderSingle();
        nos.setCorrelationId(CORRELATION_ID);
        nos.setMarketId(MARKET_ID);
        nos.setSelectionId(SELECTION_ID);
        nos.setSide(Side.LAY);
        nos.setOrderQty(500);
        nos.setPrice(700);
        return nos;
    }


}
