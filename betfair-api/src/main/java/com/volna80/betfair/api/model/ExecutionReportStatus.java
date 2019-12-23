package com.volna80.betfair.api.model;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public enum ExecutionReportStatus {

    /**
     * Order processed successfully
     */
    SUCCESS,

    /**
     * Order failed.
     */
    FAILURE,

    /**
     * The order itself has been accepted, but at least one (possibly all) actions have generated errors
     */
    PROCESSED_WITH_ERRORS,

    /**
     * Order timed out.
     */
    TIMEOUT


}
