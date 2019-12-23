package com.volna80.flush.server.model;

import com.volna80.betfair.api.model.Ssoid;

/**
 * <p>
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class Logon implements IMessage {

    public final String sessionId;
    public final int heartbeatInt;
    public final Ssoid ssoid;

    public Logon(String sessionId, Ssoid ssoid, int heartbeatInt) {
        this.sessionId = sessionId;
        this.ssoid = ssoid;
        this.heartbeatInt = heartbeatInt;
    }

    @Override
    public String toString() {
        return "Logon{" +
                "sessionId=" + sessionId +
                ", ssoid='" + ssoid + '\'' +
                ", heartbeatInt=" + heartbeatInt +
                '}';
    }
}
