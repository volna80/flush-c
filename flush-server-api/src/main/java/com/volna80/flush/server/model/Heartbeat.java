package com.volna80.flush.server.model;

/**
 * <p>
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class Heartbeat implements IMessage {

    public final long sendTime;
    public final String sessionId;

    public Heartbeat(long sendTime, String sessionId) {
        this.sendTime = sendTime;
        this.sessionId = sessionId;
    }

    @Override
    public String toString() {
        return "Heartbeat{" +
                "sendTime=" + sendTime +
                ", sessionId=" + sessionId +
                '}';
    }
}
