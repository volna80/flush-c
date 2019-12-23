package com.volna80.betfair.api.model;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public enum PriceData {

    /**
     * Amount available for the BSP auction.
     */
    SP_AVAILABLE,


    /**
     * Amount traded in the BSP auction.
     */
    SP_TRADED,

    /**
     * Only the best prices available for each runner, to requested price depth.
     */
    EX_BEST_OFFERS,

    /**
     * EX_ALL_OFFERS trumps EX_BEST_OFFERS if both settings are present
     */
    EX_ALL_OFFERS,


    /**
     * Amount traded on the exchange.
     */
    EX_TRADED


}
