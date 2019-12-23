package com.volna80.flush.server.model.exception;

/**
 * <p>
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class FlushException extends RuntimeException {

    public FlushException() {
        super();
    }

    public FlushException(String message) {
        super(message);
    }

    public FlushException(String message, Throwable cause) {
        super(message, cause);
    }

    public FlushException(Throwable cause) {
        super(cause);
    }

    protected FlushException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
