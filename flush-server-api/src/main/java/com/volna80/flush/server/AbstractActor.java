package com.volna80.flush.server;

import akka.actor.Terminated;
import akka.actor.UntypedActor;
import com.volna80.flush.server.model.*;
import com.volna80.flush.server.model.exception.UnexpectedMessageException;

/**
 * <p>
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class AbstractActor extends UntypedActor {

    public final void onReceive(Object msg) throws Exception {
        if (msg instanceof NewOrderSingle) {
            onNewOrderSingle((NewOrderSingle) msg);
        } else if (msg instanceof ExecutionReport) {
            onExecutionReport((ExecutionReport) msg);
        } else if (msg instanceof OrderCancelRequest) {
            onOrderCancelRequest((OrderCancelRequest) msg);
        } else if (msg instanceof OrderCancelReject) {
            onOrderCancelReject((OrderCancelReject) msg);
        } else if (msg instanceof Logon) {
            onLogon((Logon) msg);
        } else if (msg instanceof Logout) {
            onLogout((Logout) msg);
        } else if (msg instanceof Heartbeat) {
            onHeartbeat((Heartbeat) msg);
        } else if (msg instanceof Terminated) {
            onTerminated((Terminated) msg);
        } else if (msg instanceof Task) {
            onTask((Task) msg);
        } else {
            onUnknown(msg);
        }
    }

    public void onTask(Task task) throws Exception {
        unhandled(task);
    }

    public void onTerminated(Terminated msg) {
        unhandled(msg);
    }

    public void onOrderCancelReject(OrderCancelReject msg) throws Exception {
        unhandled(msg);
    }

    public void onOrderCancelRequest(OrderCancelRequest msg) throws Exception {
        unhandled(msg);
    }

    public void onExecutionReport(ExecutionReport msg) throws Exception {
        unhandled(msg);
    }

    public void onNewOrderSingle(NewOrderSingle msg) throws Exception {
        unhandled(msg);
    }

    public void onUnknown(Object msg) throws Exception {
        throw new UnexpectedMessageException("Unexpected message : " + msg);
    }

    public void onHeartbeat(Heartbeat heartbeat) throws Exception {
        unhandled(heartbeat);
    }

    public void onLogout(Logout logout) throws Exception {
        unhandled(logout);
    }

    public void onLogon(Logon logon) throws Exception {
        unhandled(logon);
    }
}
