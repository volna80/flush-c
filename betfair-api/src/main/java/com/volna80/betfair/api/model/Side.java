package com.volna80.betfair.api.model;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public enum Side {


    /**
     * To back a team, horse or outcome is to bet on the selection to win.
     */
    BACK("Yes"),


    /**
     * To lay a team, horse, or outcome is to bet on the selection to lose.
     */
    LAY("No");


    private String name;

    Side(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
