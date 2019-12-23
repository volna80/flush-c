package com.volna80.flush.server.model.exception;

/**
 * <p>
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class UnexpectedMessageException extends FlushException {

    public UnexpectedMessageException() {
        super();
    }

    public UnexpectedMessageException(String message) {
        super(message);
    }

    public UnexpectedMessageException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnexpectedMessageException(Throwable cause) {
        super(cause);
    }

    protected UnexpectedMessageException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
