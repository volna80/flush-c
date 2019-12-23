package com.volna80.betfair.api.model;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public enum SortDir {

    /**
     * Order from earliest value to latest e.g. lowest betId is first in the results.
     */
    EARLIEST_TO_LATEST,

    /**
     * Order from the latest value to the earliest e.g. highest betId is first in the results.
     */
    LATEST_TO_EARLIEST

}
