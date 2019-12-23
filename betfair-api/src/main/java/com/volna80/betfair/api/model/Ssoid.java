package com.volna80.betfair.api.model;

import java.io.Serializable;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class Ssoid implements Serializable {

    private final String id;
    private final int hashCode;
    private final String hashCodeStr;

    public Ssoid(String id) {
        this.id = id;
        this.hashCode = id.hashCode();
        this.hashCodeStr = hashCode + "";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ssoid ssoid = (Ssoid) o;

        if (!id.equals(ssoid.id)) return false;

        return true;
    }

    public String getId() {
        return id;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public String toString() {
        return hashCodeStr;
    }
}
