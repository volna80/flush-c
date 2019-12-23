package com.volna80.flush.server.model.exception;

/**
 * A session got second 'logon' message in 'established' status
 * <p>
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class AlreadyLogonException extends FlushException {

    public AlreadyLogonException(String message) {
        super(message);
    }
}
