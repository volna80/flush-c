package com.volna80.flush.ui.model;

import com.volna80.betfair.api.model.adapter.Precision;
import com.volna80.flush.server.model.ExecutionReport;
import com.volna80.flush.server.model.NewOrderSingle;
import com.volna80.flush.server.model.OrderCancelReject;
import com.volna80.flush.server.model.OrderCancelRequest;
import com.volna80.flush.ui.server.IDictionary;
import com.volna80.flush.ui.util.StringUtils;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class Message {

    private static final ReadOnlyStringWrapper EMPTY = new ReadOnlyStringWrapper("");


    private final ReadOnlyStringWrapper timestamp;

    private final ReadOnlyStringWrapper action;
    private final ReadOnlyStringWrapper correlationId;
    private final SimpleStringProperty event;
    private final SimpleStringProperty market;
    private final StringProperty runner;
    private final ReadOnlyStringWrapper side;
    private final ReadOnlyStringWrapper price;
    private final ReadOnlyStringWrapper orderQty;
    private final ReadOnlyStringWrapper leavesQty;
    private final ReadOnlyStringWrapper cumQty;

    private final ReadOnlyStringWrapper status;
    private final ReadOnlyStringWrapper execType;

    private final ReadOnlyStringWrapper message;

    public Message(long timestamp, NewOrderSingle nos, IDictionary dictionary) {
        this.timestamp = new ReadOnlyStringWrapper(StringUtils.convertTimestamp(timestamp));
        this.action = new ReadOnlyStringWrapper("NEW ORDER"); //TODO locale
        this.correlationId = new ReadOnlyStringWrapper(nos.getCorrelationId());
        this.event = dictionary.getMarketInfo(nos.getMarketId()).eventNameProperty();
        this.market = dictionary.getMarketInfo(nos.getMarketId()).marketNameProperty();
        this.runner = dictionary.getMarketInfo(nos.getMarketId()).getRunner(nos.getSelectionId());
        this.price = new ReadOnlyStringWrapper(Precision.toUI(nos.getPrice()));
        this.orderQty = new ReadOnlyStringWrapper(Precision.toUI(nos.getOrderQty()));
        this.side = new ReadOnlyStringWrapper(nos.getSide() + ""); //TODO locale
        this.leavesQty = EMPTY;
        this.cumQty = EMPTY;
        this.status = EMPTY;
        this.execType = EMPTY;
        this.message = EMPTY;
    }

    public Message(long timestamp, OrderCancelRequest ocr, IDictionary dictionary) {
        this.timestamp = new ReadOnlyStringWrapper(StringUtils.convertTimestamp(timestamp));
        this.action = new ReadOnlyStringWrapper("CANCEL"); //TODO locale
        this.correlationId = new ReadOnlyStringWrapper(ocr.getCorrelationId());
        this.event = dictionary.getMarketInfo(ocr.getMarketId()).eventNameProperty();
        this.market = dictionary.getMarketInfo(ocr.getMarketId()).marketNameProperty();
        this.runner = dictionary.getMarketInfo(ocr.getMarketId()).getRunner(ocr.getSelectionId());
        this.side = EMPTY;
        this.price = EMPTY;
        this.orderQty = EMPTY;
        this.leavesQty = EMPTY;
        this.cumQty = EMPTY;
        this.status = EMPTY;
        this.execType = EMPTY;
        this.message = EMPTY;
    }

    public Message(long timestamp, ExecutionReport er, IDictionary dictionary) {
        this.timestamp = new ReadOnlyStringWrapper(StringUtils.convertTimestamp(timestamp));
        this.action = new ReadOnlyStringWrapper("STATUS"); //TODO locale
        this.correlationId = new ReadOnlyStringWrapper(er.getCorrelationId());
        this.event = dictionary.getMarketInfo(er.getMarketId()).eventNameProperty();
        this.market = dictionary.getMarketInfo(er.getMarketId()).marketNameProperty();
        this.runner = dictionary.getMarketInfo(er.getMarketId()).getRunner(er.getSelectionId());
        this.price = new ReadOnlyStringWrapper(Precision.toUI(er.getPrice()));
        this.orderQty = new ReadOnlyStringWrapper(Precision.toUI(er.getOrderQty()));
        this.leavesQty = new ReadOnlyStringWrapper(Precision.toUI(er.getLeavesQty()));
        this.cumQty = new ReadOnlyStringWrapper(Precision.toUI(er.getCumQty()));
        this.status = new ReadOnlyStringWrapper(er.getStatus() + "");
        this.execType = er.getExecType() != null ? new ReadOnlyStringWrapper(er.getExecType() + "") : EMPTY;
        this.message = new ReadOnlyStringWrapper(er.getText());
        this.side = EMPTY;
    }

    public Message(long timestamp, OrderCancelReject reject, IDictionary dictionary) {
        this.timestamp = new ReadOnlyStringWrapper(StringUtils.convertTimestamp(timestamp));
        this.action = new ReadOnlyStringWrapper("REJECT"); //TODO locale
        this.correlationId = new ReadOnlyStringWrapper(reject.getCorrelationId());
        this.event = dictionary.getMarketInfo(reject.getMarketId()).eventNameProperty();
        this.market = dictionary.getMarketInfo(reject.getMarketId()).marketNameProperty();
        this.runner = dictionary.getMarketInfo(reject.getMarketId()).getRunner(reject.getSelectionId());
        this.price = EMPTY;
        this.orderQty = EMPTY;
        this.leavesQty = EMPTY;
        this.cumQty = EMPTY;
        this.status = EMPTY;
        this.execType = EMPTY;
        this.message = new ReadOnlyStringWrapper(reject.getReason());
        this.side = EMPTY;
    }

    public String getRunner() {
        return runner.get();
    }

    public StringProperty runnerProperty() {
        return runner;
    }

    public String getMessage() {
        return message.get();
    }

    public StringProperty messageProperty() {
        return message;
    }

    public String getTimestamp() {
        return timestamp.get();
    }

    public ReadOnlyStringWrapper timestampProperty() {
        return timestamp;
    }

    public String getAction() {
        return action.get();
    }

    public ReadOnlyStringWrapper actionProperty() {
        return action;
    }

    public String getCorrelationId() {
        return correlationId.get();
    }

    public ReadOnlyStringWrapper correlationIdProperty() {
        return correlationId;
    }

    public String getMarket() {
        return market.get();
    }

    public SimpleStringProperty marketProperty() {
        return market;
    }

    public String getPrice() {
        return price.get();
    }

    public ReadOnlyStringWrapper priceProperty() {
        return price;
    }

    public String getOrderQty() {
        return orderQty.get();
    }

    public ReadOnlyStringWrapper orderQtyProperty() {
        return orderQty;
    }

    public String getLeavesQty() {
        return leavesQty.get();
    }

    public ReadOnlyStringWrapper leavesQtyProperty() {
        return leavesQty;
    }

    public String getCumQty() {
        return cumQty.get();
    }

    public ReadOnlyStringWrapper cumQtyProperty() {
        return cumQty;
    }

    public String getStatus() {
        return status.get();
    }

    public ReadOnlyStringWrapper statusProperty() {
        return status;
    }

    public String getExecType() {
        return execType.get();
    }

    public ReadOnlyStringWrapper execTypeProperty() {
        return execType;
    }

    public String getEvent() {
        return event.get();
    }

    public SimpleStringProperty eventProperty() {
        return event;
    }

    public String getSide() {
        return side.get();
    }

    public ReadOnlyStringWrapper sideProperty() {
        return side;
    }

    @Override
    public String toString() {
        return "Message{" + message + '}';
    }
}
