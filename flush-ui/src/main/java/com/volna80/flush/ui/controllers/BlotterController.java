package com.volna80.flush.ui.controllers;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.volna80.betfair.api.model.adapter.Precision;
import com.volna80.flush.ui.ApplicationManager;
import com.volna80.flush.ui.model.Message;
import com.volna80.flush.ui.model.UICurrentOrderSummary;
import com.volna80.flush.ui.server.IFlushAPI;
import com.volna80.flush.ui.server.IOrdersController;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.*;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class BlotterController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(BlotterController.class);


    @FXML
    public TableView<UICurrentOrderSummary> activeOrders;
    @FXML
    public TableView<UICurrentOrderSummary> matchedOrders;
    @FXML
    public TableView<Message> messages;

    @Inject
    public volatile IOrdersController ordersController;


    @Inject
    public IFlushAPI api;

    private Map<String, UICurrentOrderSummary> executable = new HashMap<>();
    private Set<String> complete = new HashSet<>();

    private Timeline timeline;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        log.info("initialize");

        Preconditions.checkNotNull(ordersController);

        activeOrders.getColumns().add(idColumn());
        matchedOrders.getColumns().add(idColumn());

        activeOrders.getColumns().add(eventColumn());
        matchedOrders.getColumns().add(eventColumn());

        activeOrders.getColumns().add(marketColumn());
        matchedOrders.getColumns().add(marketColumn());

        activeOrders.getColumns().add(instrumentColumn());
        matchedOrders.getColumns().add(instrumentColumn());

        activeOrders.getColumns().add(sideColumn());
        matchedOrders.getColumns().add(sideColumn());

        activeOrders.getColumns().add(priceColumn());
        matchedOrders.getColumns().add(priceColumn());

        activeOrders.getColumns().add(sizeColumn());
        matchedOrders.getColumns().add(sizeColumn());

        activeOrders.getColumns().add(dateColumn());
        matchedOrders.getColumns().add(dateColumn());

        activeOrders.getColumns().add(matchedDateColumn());
        matchedOrders.getColumns().add(matchedDateColumn());

        activeOrders.getColumns().add(averagePriceMatchedColumn());
        matchedOrders.getColumns().add(averagePriceMatchedColumn());

        activeOrders.getColumns().add(matchedColumn());
        matchedOrders.getColumns().add(matchedColumn());

        activeOrders.getColumns().add(remainingColumn());
        matchedOrders.getColumns().add(remainingColumn());

        activeOrders.getColumns().add(openBuySellColumn());
        matchedOrders.getColumns().add(openBuySellColumn());

        activeOrders.getColumns().add(cancelColumn());


        messages.getColumns().add(timestampColumn());
        messages.getColumns().add(actionColumn());
        messages.getColumns().add(correlationColumn());
        messages.getColumns().add(eventColumn2());
        messages.getColumns().add(marketColumn2());
        messages.getColumns().add(runnerColumn());
        messages.getColumns().add(sideColumn2());
        messages.getColumns().add(priceColumn2());
        messages.getColumns().add(orderQty2());
        messages.getColumns().add(leavesQty2());
        messages.getColumns().add(cumQty2());
        messages.getColumns().add(status2());
        messages.getColumns().add(execType2());
        messages.getColumns().add(messageColumn());


        refresh();

        timeline = new Timeline(
                new KeyFrame(
                        Duration.seconds(5),
                        actionEvent -> refresh()
                )
        );

        timeline.setDelay(Duration.seconds(5));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

    }


    private TableColumn<UICurrentOrderSummary, ?> openBuySellColumn() {
        TableColumn<UICurrentOrderSummary, UICurrentOrderSummary> column = new TableColumn<>("");
        column.setCellValueFactory(value -> new ReadOnlyObjectWrapper<>(value.getValue()));
        column.setCellFactory(new Callback<TableColumn<UICurrentOrderSummary, UICurrentOrderSummary>, TableCell<UICurrentOrderSummary, UICurrentOrderSummary>>() {
            @Override
            public TableCell<UICurrentOrderSummary, UICurrentOrderSummary> call(TableColumn<UICurrentOrderSummary, UICurrentOrderSummary> currentOrderSummaryCurrentOrderSummaryTableColumn) {
                return new TableCell<UICurrentOrderSummary, UICurrentOrderSummary>() {

                    private final Button button = new Button("buy/sell");

                    {
                        button.setMinWidth(30);
                    }


                    @Override
                    protected void updateItem(final UICurrentOrderSummary UICurrentOrderSummary, boolean empty) {
                        super.updateItem(UICurrentOrderSummary, empty);

                        if (UICurrentOrderSummary != null && !empty) {

                            button.setOnAction(actionEvent -> {
                                final String marketId = UICurrentOrderSummary.getMarketId();
                                final long selectionId = UICurrentOrderSummary.getSelectionId();

                                ApplicationManager.getInstance().openBuySell(marketId, selectionId);
                            });

                            setGraphic(button);

                        } else {
                            setGraphic(null);
                        }
                    }
                };
            }
        });
        column.setSortable(false);

        return column;
    }

    private TableColumn<UICurrentOrderSummary, ?> eventColumn() {
        TableColumn<UICurrentOrderSummary, String> column = new TableColumn<>("event");
        column.setCellValueFactory(currentOrderSummaryStringCellDataFeatures -> currentOrderSummaryStringCellDataFeatures.getValue().eventNameProperty());
        return column;
    }

    private TableColumn<UICurrentOrderSummary, ?> sideColumn() {
        TableColumn<UICurrentOrderSummary, String> column = new TableColumn<>("side");
        column.setCellValueFactory(currentOrderSummaryStringCellDataFeatures -> currentOrderSummaryStringCellDataFeatures.getValue().sideProperty());
        return column;
    }

    private TableColumn cancelColumn() {
        TableColumn<UICurrentOrderSummary, UICurrentOrderSummary> column = new TableColumn<>("");
        column.setCellValueFactory(value -> new ReadOnlyObjectWrapper<>(value.getValue()));
        column.setCellFactory(new Callback<TableColumn<UICurrentOrderSummary, UICurrentOrderSummary>, TableCell<UICurrentOrderSummary, UICurrentOrderSummary>>() {
            @Override
            public TableCell<UICurrentOrderSummary, UICurrentOrderSummary> call(TableColumn<UICurrentOrderSummary, UICurrentOrderSummary> currentOrderSummaryCurrentOrderSummaryTableColumn) {
                return new TableCell<UICurrentOrderSummary, UICurrentOrderSummary>() {

                    private final Button button = new Button("cancel");

                    {
                        button.setMinWidth(30);
                    }


                    @Override
                    protected void updateItem(final UICurrentOrderSummary UICurrentOrderSummary, boolean empty) {
                        super.updateItem(UICurrentOrderSummary, empty);

                        if (UICurrentOrderSummary != null && !empty) {

                            button.setOnAction(actionEvent -> {
                                final String betId = UICurrentOrderSummary.getId();
                                final String marketId = UICurrentOrderSummary.getMarketId();
                                log.info("cancel " + marketId + "#" + betId);
                                ordersController.cancelOrders(marketId, Collections.singletonList(betId));
                                refresh();//TODO
                            });

                            setGraphic(button);

                        } else {
                            setGraphic(null);
                        }
                    }
                };
            }
        });
        column.setSortable(false);

        return column;
    }

    private TableColumn<UICurrentOrderSummary, String> remainingColumn() {
        TableColumn<UICurrentOrderSummary, String> column = new TableColumn<>("remaining");
        column.setCellValueFactory(value -> value.getValue().sizeRemainingProperty());
        return column;
    }

    private TableColumn<UICurrentOrderSummary, String> matchedColumn() {
        TableColumn<UICurrentOrderSummary, String> column = new TableColumn<>("complete");
        column.setCellValueFactory(value -> value.getValue().sizeMatchedProperty());
        return column;
    }

    private TableColumn<UICurrentOrderSummary, ?> averagePriceMatchedColumn() {
        TableColumn<UICurrentOrderSummary, String> column = new TableColumn<>("average price");
        column.setCellValueFactory(value -> value.getValue().averagePriceMatchedProperty());
        return column;
    }

    private TableColumn<UICurrentOrderSummary, String> dateColumn() {
        TableColumn<UICurrentOrderSummary, String> column = new TableColumn<>("date");
        column.setCellValueFactory(value -> value.getValue().placedDateProperty());
        return column;
    }

    private TableColumn<Message, String> timestampColumn() {
        TableColumn<Message, String> column = new TableColumn<>("timestamp");
        column.setCellValueFactory(value -> value.getValue().timestampProperty());
        return column;
    }

    private TableColumn<Message, String> correlationColumn() {
        TableColumn<Message, String> column = new TableColumn<>("id");
        column.setCellValueFactory(value -> value.getValue().correlationIdProperty());
        return column;
    }

    private TableColumn<Message, String> actionColumn() {
        TableColumn<Message, String> column = new TableColumn<>("action");
        column.setCellValueFactory(value -> value.getValue().actionProperty());
        return column;
    }

    private TableColumn<Message, String> marketColumn2() {
        TableColumn<Message, String> column = new TableColumn<>("market");
        column.setCellValueFactory(value -> value.getValue().marketProperty());
        return column;
    }

    private TableColumn<Message, String> runnerColumn() {
        TableColumn<Message, String> column = new TableColumn<>("runner");
        column.setCellValueFactory(value -> value.getValue().runnerProperty());
        return column;
    }

    private TableColumn<Message, String> sideColumn2() {
        TableColumn<Message, String> column = new TableColumn<>("side");
        column.setCellValueFactory(value -> value.getValue().sideProperty());
        return column;
    }


    private TableColumn<Message, String> eventColumn2() {
        TableColumn<Message, String> column = new TableColumn<>("event");
        column.setCellValueFactory(value -> value.getValue().eventProperty());
        return column;
    }

    private TableColumn<Message, String> priceColumn2() {
        TableColumn<Message, String> column = new TableColumn<>("price");
        column.setCellValueFactory(value -> value.getValue().priceProperty());
        return column;
    }

    private TableColumn<Message, String> orderQty2() {
        TableColumn<Message, String> column = new TableColumn<>("orderQty");
        column.setCellValueFactory(value -> value.getValue().orderQtyProperty());
        return column;
    }

    private TableColumn<Message, String> leavesQty2() {
        TableColumn<Message, String> column = new TableColumn<>("leavesQty");
        column.setCellValueFactory(value -> value.getValue().leavesQtyProperty());
        return column;
    }

    private TableColumn<Message, String> cumQty2() {
        TableColumn<Message, String> column = new TableColumn<>("cumQty");
        column.setCellValueFactory(value -> value.getValue().cumQtyProperty());
        return column;
    }

    private TableColumn<Message, String> status2() {
        TableColumn<Message, String> column = new TableColumn<>("status");
        column.setCellValueFactory(value -> value.getValue().statusProperty());
        return column;
    }

    private TableColumn<Message, String> execType2() {
        TableColumn<Message, String> column = new TableColumn<>("execType");
        column.setCellValueFactory(value -> value.getValue().execTypeProperty());
        return column;
    }


    private TableColumn<Message, String> messageColumn() {
        TableColumn<Message, String> column = new TableColumn<>("message");
        column.setCellValueFactory(value -> value.getValue().messageProperty());
        return column;
    }

    private TableColumn<UICurrentOrderSummary, String> matchedDateColumn() {
        TableColumn<UICurrentOrderSummary, String> column = new TableColumn<>("matched");
        column.setCellValueFactory(value -> value.getValue().matchedDateProperty());
        return column;
    }

    private TableColumn<UICurrentOrderSummary, String> sizeColumn() {
        TableColumn<UICurrentOrderSummary, String> column = new TableColumn<>("size");
        column.setCellValueFactory(value -> value.getValue().sizeProperty());
        return column;
    }

    private TableColumn<UICurrentOrderSummary, String> priceColumn() {
        TableColumn<UICurrentOrderSummary, String> column = new TableColumn<>("price");
        column.setCellValueFactory(value -> value.getValue().priceProperty());
        return column;
    }

    private TableColumn<UICurrentOrderSummary, String> instrumentColumn() {
        TableColumn<UICurrentOrderSummary, String> column = new TableColumn<>("Runner");
        column.setCellValueFactory(value -> value.getValue().runnerNameProperty());
        return column;
    }

    private TableColumn<UICurrentOrderSummary, String> marketColumn() {
        TableColumn<UICurrentOrderSummary, String> column = new TableColumn<>("market");
        column.setCellValueFactory(value -> value.getValue().marketNameProperty());
        return column;
    }

    private TableColumn<UICurrentOrderSummary, String> idColumn() {
        final TableColumn<UICurrentOrderSummary, String> column = new TableColumn<>("id");
        column.setCellValueFactory(value -> value.getValue().idProperty());

        return column;
    }


    public void refresh() {

        try {
            Collection<com.volna80.betfair.api.model.CurrentOrderSummary> orders = ordersController.getExecutableOrders();

            HashSet<String> executableIds = new HashSet<>(orders.size());
            for (com.volna80.betfair.api.model.CurrentOrderSummary summary : orders) {
                executableIds.add(summary.getBetId());
                if (executable.containsKey(summary.getBetId())) {
                    //update the state
                    UICurrentOrderSummary item = executable.get(summary.getBetId());
                    item.sizeMatchedProperty().set(Precision.toUI(summary.getSizeMatched()));
                    item.sizeRemainingProperty().set(Precision.toUI(summary.getSizeRemaining()));
                    item.averagePriceMatchedProperty().set(Precision.toUI(summary.getAveragePriceMatched()));
                } else {
                    //new order
                    UICurrentOrderSummary item = new UICurrentOrderSummary(summary, api.getDictionary());
                    executable.put(item.getId(), item);
                    activeOrders.getItems().add(item);
                }
            }


            for (Iterator<Map.Entry<String, UICurrentOrderSummary>> i = executable.entrySet().iterator(); i.hasNext(); ) {
                UICurrentOrderSummary summary = i.next().getValue();
                if (!executableIds.contains(summary.getId())) {
                    //no such order in executable list
                    i.remove();
                    activeOrders.getItems().remove(summary);
                }
            }

            //done orders

            orders = ordersController.getCompleteOrders();

            for (com.volna80.betfair.api.model.CurrentOrderSummary summary : orders) {
                if (!complete.contains(summary.getBetId())) {
                    //new completed order
                    UICurrentOrderSummary item = new UICurrentOrderSummary(summary, api.getDictionary());
                    complete.add(item.getId());
                    matchedOrders.getItems().add(item);
                }
            }

            //TODO limit the size?
            messages.getItems().addAll(ordersController.getLastMessages());

            activeOrders.sort();
            matchedOrders.sort();
            messages.sort();

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }


    //TODO
    public void close() {
        timeline.stop();
    }
}
