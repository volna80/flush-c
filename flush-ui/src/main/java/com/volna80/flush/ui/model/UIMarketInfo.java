package com.volna80.flush.ui.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Information about market
 * <p/>
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class UIMarketInfo {

    public static final String LOADING = "Loading ...";                      //TODO locale


    private final String marketId;
    private final SimpleStringProperty marketName = new SimpleStringProperty(LOADING);
    private final SimpleStringProperty eventName = new SimpleStringProperty(LOADING);
    private final SimpleStringProperty competitionName = new SimpleStringProperty(LOADING);
    private final SimpleStringProperty eventTypeName = new SimpleStringProperty(LOADING);
    private final ConcurrentHashMap<Long, SimpleStringProperty> runners = new ConcurrentHashMap<>();


    public UIMarketInfo(String marketId) {
        this.marketId = marketId;
    }

    public String getMarketId() {
        return marketId;
    }

    public String getMarketName() {
        return marketName.get();
    }

    public SimpleStringProperty marketNameProperty() {
        return marketName;
    }

    public String getEventName() {
        return eventName.get();
    }

    public SimpleStringProperty eventNameProperty() {
        return eventName;
    }

    public StringProperty getRunner(long runnerId) {
        StringProperty runner = runners.get(runnerId);
        if (runner == null) {
            runners.putIfAbsent(runnerId, new SimpleStringProperty(LOADING));
            runner = runners.get(runnerId);
        }
        return runner;
    }

    public String getCompetitionName() {
        return competitionName.get();
    }

    public SimpleStringProperty competitionNameProperty() {
        return competitionName;
    }

    public String getEventTypeName() {
        return eventTypeName.get();
    }

    public SimpleStringProperty eventTypeNameProperty() {
        return eventTypeName;
    }

    public void error() {
        marketName.setValue("Error!");
        eventName.setValue("Error!");
        for (SimpleStringProperty r : runners.values()) {
            r.setValue("Error!");
        }
    }

}
