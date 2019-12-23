package com.volna80.flush.server.model;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public interface ITicket extends ITicketReadOnly {

    /**
     * cancel the current order
     *
     * @return
     */
    OrderCancelRequest cancel();

    void onExecutionReport(ExecutionReport er);


}
