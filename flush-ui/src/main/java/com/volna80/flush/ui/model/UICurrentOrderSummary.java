package com.volna80.flush.ui.model;

import com.volna80.betfair.api.model.adapter.Precision;
import com.volna80.flush.ui.server.IDictionary;
import com.volna80.flush.ui.util.StringUtils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class UICurrentOrderSummary {

    private final ReadOnlyObjectWrapper<String> id;
    private final long selectionId;


    private final String marketId;

    private final StringProperty eventName;

    private final ReadOnlyObjectWrapper<String> side;

    private final StringProperty marketName;

    private final StringProperty runnerName;

    private final SimpleStringProperty status;

    private final ReadOnlyObjectWrapper<String> price;

    private final ReadOnlyObjectWrapper<String> size;

    private final ReadOnlyObjectWrapper<String> placedDate;

    private final SimpleStringProperty sizeMatched;

    private final SimpleStringProperty sizeRemaining;
    private final ReadOnlyObjectWrapper<String> matchedDate;
    private final SimpleStringProperty averagePriceMatched;

    public UICurrentOrderSummary(com.volna80.betfair.api.model.CurrentOrderSummary summary, IDictionary dict) {
        id = new ReadOnlyObjectWrapper<>(summary.getBetId());
        status = new SimpleStringProperty(summary.getStatus().name());
        marketId = summary.getMarketId();

        marketName = dict.getMarketInfo(summary.getMarketId()).marketNameProperty();
        eventName = dict.getMarketInfo(summary.getMarketId()).eventNameProperty();
        side = new ReadOnlyObjectWrapper<>(summary.getSide().getName());
        runnerName = dict.getMarketInfo(summary.getMarketId()).getRunner(summary.getSelectionId());
        selectionId = summary.getSelectionId();
        price = new ReadOnlyObjectWrapper<>(Precision.toUI(summary.getPriceSize().getPrice()));
        size = new ReadOnlyObjectWrapper<>(Precision.toUI(summary.getPriceSize().getSize()));
        placedDate = new ReadOnlyObjectWrapper<>(StringUtils.convertTimestamp(summary.getPlacedDate()));
        sizeMatched = new SimpleStringProperty(Precision.toUI(summary.getSizeMatched()));
        sizeRemaining = new SimpleStringProperty(Precision.toUI(summary.getSizeRemaining()));
        averagePriceMatched = new SimpleStringProperty(Precision.toUI(summary.getAveragePriceMatched()));

        matchedDate = new ReadOnlyObjectWrapper<>(StringUtils.convertTimestamp(summary.getMatchedDate()));
    }


    public String getId() {
        return id.get();
    }

    public ReadOnlyObjectWrapper<String> idProperty() {
        return id;
    }

    public String getStatus() {
        return status.get();
    }

    public SimpleStringProperty statusProperty() {
        return status;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UICurrentOrderSummary that = (UICurrentOrderSummary) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public String getPrice() {
        return price.get();
    }

    public ReadOnlyObjectWrapper<String> priceProperty() {
        return price;
    }

    public String getSize() {
        return size.get();
    }

    public ReadOnlyObjectWrapper<String> sizeProperty() {
        return size;
    }

    public String getPlacedDate() {
        return placedDate.get();
    }

    public ReadOnlyObjectWrapper<String> placedDateProperty() {
        return placedDate;
    }

    public String getSizeMatched() {
        return sizeMatched.get();
    }

    public SimpleStringProperty sizeMatchedProperty() {
        return sizeMatched;
    }

    public String getSizeRemaining() {
        return sizeRemaining.get();
    }

    public SimpleStringProperty sizeRemainingProperty() {
        return sizeRemaining;
    }

    public String getMarketName() {
        return marketName.get();
    }

    public StringProperty marketNameProperty() {
        return marketName;
    }

    public String getSide() {
        return side.get();
    }

    public ReadOnlyObjectWrapper<String> sideProperty() {
        return side;
    }

    public String getEventName() {
        return eventName.get();
    }

    public StringProperty eventNameProperty() {
        return eventName;
    }

    public String getRunnerName() {
        return runnerName.get();
    }

    public StringProperty runnerNameProperty() {
        return runnerName;
    }

    public long getSelectionId() {
        return selectionId;
    }

    public String getMatchedDate() {
        return matchedDate.get();
    }

    public ReadOnlyObjectWrapper<String> matchedDateProperty() {
        return matchedDate;
    }

    public String getAveragePriceMatched() {
        return averagePriceMatched.get();
    }

    public SimpleStringProperty averagePriceMatchedProperty() {
        return averagePriceMatched;
    }

    @Override
    public String toString() {
        return "UICurrentOrderSummary{" +
                "id=" + id +
                '}';
    }

    public String getMarketId() {
        return marketId;
    }
}


