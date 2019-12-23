package com.volna80.betfair.api.model;

import com.google.common.base.Preconditions;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class EventType {

    //betfair constant dictionary
    public static EventType SOCCER = new EventType("1");

    private String name;
    private String id;

    public EventType(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        Preconditions.checkNotNull(name);
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        Preconditions.checkNotNull(id);
        this.id = id;
    }

    @Override
    public String toString() {
        return "EventType{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EventType eventType = (EventType) o;

        if (!id.equals(eventType.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
