package com.volna80.flush.server.algos;

import akka.actor.ActorRef;
import akka.actor.Props;
import com.google.common.base.Preconditions;
import com.volna80.betfair.api.model.ExecutionReportErrorCode;
import com.volna80.betfair.api.model.OrderStatus;
import com.volna80.flush.server.AbstractActor;
import com.volna80.flush.server.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */

//TODO write tests
public class BasicOrder extends AbstractActor {

    private static final Logger logger = LoggerFactory.getLogger(BasicOrder.class);
    private final ActorRef downstream;
    private ActorRef client;
    private NewOrderSingle origin;
    private ExecutionReport lastER;

    private boolean needToCancelOrder = false;


    public BasicOrder(ActorRef downstream) {
        this.downstream = downstream;
    }

    public static Props props(ActorRef downstream) {
        return Props.create(BasicOrder.class, downstream);
    }

    @Override
    public void postStop() throws Exception {
        logger.info("postStop:{}", origin);
    }

    @Override
    public void onExecutionReport(ExecutionReport msg) throws Exception {
        logger.info("onExecutionReport:{}", msg);
        Preconditions.checkArgument(msg.getCorrelationId().equals(origin.getCorrelationId()));

        if (lastER == null && msg.getStatus() == OrderStatus.EXECUTION_COMPLETE
                && msg.getErrorCode() == ExecutionReportErrorCode.MARKET_SUSPENDED
                && !needToCancelOrder) {
            //first try and market is suspended state
            logger.info("market is suspended, try to place an order again");
            downstream.tell(origin, getSelf());
            return;
        }

        lastER = msg;

        client.tell(msg, getSelf());

        if (msg.getStatus() == OrderStatus.EXECUTION_COMPLETE) {

            getContext().stop(getSelf());
        } else if (needToCancelOrder) {
            //we got a cancel request while we wait a first ER
            logger.info("a client has request to cancel an order " + msg.getBetId());

            OrderCancelRequest ocr = MessageConverter.makeCancel(msg);
            downstream.tell(ocr, getSelf());
            needToCancelOrder = false;
        }

    }


    @Override
    public void onNewOrderSingle(NewOrderSingle msg) throws Exception {

        if (origin != null) {
            throw new RuntimeException("unexpected msg: " + msg);
        }

        client = getSender();
        this.origin = msg;

        logger.info("forwarding {} to {} from {}", msg, downstream, getSelf());
        downstream.tell(msg, getSelf());

        ExecutionReport ack = MessageConverter.makeAck(msg);
        ack.setExecType(ExecType.PENDING_NEW);
        client.tell(ack, getSelf());

    }

    @Override
    public void onOrderCancelRequest(OrderCancelRequest msg) throws Exception {
        Preconditions.checkArgument(msg.getCorrelationId().equals(origin.getCorrelationId()));
        logger.info("onOrderCancelRequest() {}", msg);

        if (msg.getBetId() != null) {
            Preconditions.checkArgument(msg.getBetId().equals(lastER.getBetId()));

            logger.info("forward {} to a downstream", msg);
            downstream.tell(msg, getSelf());

        } else if (lastER != null) {
            //ok, we got an ack from a downstream, but looks like a client not

            if (lastER.getStatus() == OrderStatus.EXECUTABLE) {
                logger.info("add bet it {} to {}", lastER.getBetId(), msg);
                msg.setBetId(lastER.getBetId());
                downstream.tell(msg, getSelf());
            } else {
                //unreachable place ?
                String reason = "the order has been cancelled or filled on a market.";
                logger.info(reason + " The latest er: {}", lastER);
                OrderCancelReject reject = MessageConverter.makeReject(msg, reason);
                getSender().tell(reject, getSelf());
            }

        } else {
            //we haven't got an ack to NOS. So. we can not cancel the order if we don't know its betId
            //let's wait the ack
            needToCancelOrder = true;
            logger.info("postpone sending cancel until we get an ack from betfair. {}", msg);
        }
    }

    @Override
    public void onOrderCancelReject(OrderCancelReject msg) throws Exception {
        Preconditions.checkArgument(msg.getCorrelationId().equals(origin.getCorrelationId()));
        logger.info("onOrderCancelReject() {}", msg);

        if (lastER.getStatus() == OrderStatus.EXECUTABLE &&
                msg.getErrorCode() != null && msg.getErrorCode() == ExecutionReportErrorCode.MARKET_SUSPENDED) {
            //try to cancel again
            logger.info("market is suspended. trying to cancel order again. betId=" + lastER.getBetId());
            OrderCancelRequest cancel = MessageConverter.makeCancel(lastER);
            downstream.tell(cancel, getSelf());
        } else {
            //TODO stop?
            client.tell(msg, getSelf());
        }
    }
}
