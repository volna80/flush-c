package com.volna80.flush.server.model.exception;

/**
 * An unknown order
 * <p>
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class UnknownOrderException extends FlushException {

    public final String unknownCorrelationId;

    public UnknownOrderException(String unknownCorrelationId) {
        super("Unknown correlation id : " + unknownCorrelationId);
        this.unknownCorrelationId = unknownCorrelationId;
    }
}
