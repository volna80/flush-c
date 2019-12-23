package com.volna80.betfair.api.model.errors;

/**
 * <p>
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public enum ErrorCode {

    /**
     * The operation requested too much data, exceeding the Market Data Request Limits.
     */
    TOO_MUCH_DATA,
    /**
     * The data input is invalid. A specific description is returned via errorDetails as shown below.
     */
    INVALID_INPUT_DATA,
    /**
     * The session token hasn't been provided, is invalid or has expired.
     */
    INVALID_SESSION_INFORMATION,
    /**
     * An application key header ('X-Application') has not been provided in the request
     */
    NO_APP_KEY,
    /**
     * A session token header ('X-Authentication') has not been provided in the request
     */
    NO_SESSION,

    /**
     * An unexpected internal error occurred that prevented successful request processing.
     */
    UNEXPECTED_ERROR,

    /**
     * The application key passed is invalid or is not present
     */
    INVALID_APP_KEY,

    /**
     * There are too many pending requests e.g. a listMarketBook with Order/Match projections is limited to 3 concurrent requests. The error also applies to listCurrentOrders, listMarketProfitAndLoss and listClearedOrders if you have 3 or more requests currently in execution
     */
    TOO_MANY_REQUESTS,
    /**
     * The service is currently too busy to service this request
     */
    SERVICE_BUSY,
    /**
     * Internal call to downstream service timed out
     */
    TIMEOUT_ERROR,
    /**
     * The request exceeds the request size limit. Requests are limited to a total of 250 betId’s/marketId’s (or a combination of both)
     */
    REQUEST_SIZE_EXCEEDS_LIMIT,
    /**
     * The calling client is not permitted to perform the specific action e.g. the using a Delayed App Key when placing bets or attempting to place a bet from a restricted jurisdiction.
     */
    ACCESS_DENIED;

    //we can try to continue
    public boolean isNotCritical() {
        return this == TOO_MUCH_DATA || this == TOO_MANY_REQUESTS || this == SERVICE_BUSY || this == TIMEOUT_ERROR || this == REQUEST_SIZE_EXCEEDS_LIMIT;
    }

}
