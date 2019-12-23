package com.volna80.betfair.api.model;

import com.volna80.betfair.api.model.adapter.Precision;

/**
 * All double's values are kept as int inside
 * <p>
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
class Precision2 {

    /**
     * converts an external double to an internal format (int)
     */
    static int toInternal(double d) {
        return (int) Math.round(d * Precision.MULTI);
    }

    /**
     * converts an num in an internal format to external format
     */
    static double toExternal(int l) {
        return (double) l / Precision.MULTI;
    }

}
