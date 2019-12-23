package com.volna80.betfair.api;

import com.volna80.betfair.api.model.errors.ErrorCode;

/**
 * <p>
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class APINGException extends BetfairException {

    private final ErrorCode code;

    public APINGException(String message, ErrorCode code) {
        super(message);
        this.code = code;
    }

    public ErrorCode getCode() {
        return code;
    }

    @Override
    public String toString() {
        return "APINGException{" +
                "code=" + code +
                '}';
    }
}
