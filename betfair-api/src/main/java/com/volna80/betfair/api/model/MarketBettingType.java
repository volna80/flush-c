package com.volna80.betfair.api.model;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public enum MarketBettingType {

    ODDS,
    LINE,
    RANGE,
    ASIAN_HANDICAP_DOUBLE_LINE,
    ASIAN_HANDICAP_SINGLE_LINE,
    /**
     * Sportsbook Odds Market. This type is deprecated and will be removed in future releases, when Sportsbook markets
     * will be represented as ODDS market but with a different product type.
     *
     * @deprecated
     */
    FIXED_ODDS
}
