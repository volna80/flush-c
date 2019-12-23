package com.volna80.flush.server.model;

import com.volna80.betfair.api.model.ExecutionReportErrorCode;
import com.volna80.betfair.api.model.OrderStatus;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class MessageConverter {


    public static ExecutionReport makeAck(NewOrderSingle nos) {
        ExecutionReport er = new ExecutionReport();
        er.setCorrelationId(nos.getCorrelationId());
        er.setMarketId(nos.getMarketId());
        er.setSelectionId(nos.getSelectionId());
        er.setOrderQty(nos.getOrderQty());
        er.setLeavesQty(nos.getOrderQty());
        er.setPrice(nos.getPrice());
        er.setStatus(OrderStatus.EXECUTABLE);
        return er;
    }

    public static ExecutionReport makeReject(NewOrderSingle msg, String reason, ExecutionReportErrorCode errorCode) {
        ExecutionReport reject = new ExecutionReport();
        reject.setCorrelationId(msg.getCorrelationId());
        reject.setMarketId(msg.getMarketId());
        reject.setSelectionId(msg.getSelectionId());
        reject.setStatus(OrderStatus.EXECUTION_COMPLETE);
        reject.setExecType(ExecType.REJECTED);
        reject.setText(reason);
        reject.setErrorCode(errorCode);

        reject.setOrderQty(msg.getOrderQty());
        reject.setPrice(msg.getPrice());
        reject.setCumQty(0);
        reject.setLeavesQty(0);

        return reject;
    }

    private static OrderCancelReject makeReject(OrderCancelRequest msg) {
        OrderCancelReject reject = new OrderCancelReject();
        reject.setCorrelationId(msg.getCorrelationId());
        reject.setBetId(msg.getBetId());
        return reject;
    }

    public static OrderCancelReject makeReject(OrderCancelRequest msg, String reason) {
        OrderCancelReject reject = makeReject(msg);
        reject.setReason(reason);
        return reject;
    }

    public static OrderCancelRequest makeCancel(ExecutionReport er) {
        OrderCancelRequest ocr = new OrderCancelRequest();
        ocr.setBetId(er.getBetId());
        ocr.setCorrelationId(er.getCorrelationId());
        ocr.setMarketId(er.getMarketId());
        ocr.setSelectionId(er.getSelectionId());
        return ocr;
    }
}
