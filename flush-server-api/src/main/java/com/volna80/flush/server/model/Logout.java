package com.volna80.flush.server.model;

/**
 * <p>
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class Logout implements IMessage {

    private String reason;
    private String sessionId;

    public Logout(String reason, String sessionId) {
        this.reason = reason;
        this.sessionId = sessionId;
    }

    @Override
    public String toString() {
        return "Logout{" +
                "reason='" + reason + '\'' +
                ", sessionId='" + sessionId + '\'' +
                '}';
    }

    public String getReason() {
        return reason;
    }

    public String getSessionId() {
        return sessionId;
    }
}
