package com.volna80.flush.ui.controllers;

import com.google.inject.Inject;
import com.volna80.betfair.api.model.MarketBook;
import com.volna80.betfair.api.model.MarketCatalogue;
import com.volna80.betfair.api.model.MarketStatus;
import com.volna80.betfair.api.model.Side;
import com.volna80.flush.ui.ApplicationManager;
import com.volna80.flush.ui.server.IFlushAPI;
import com.volna80.flush.ui.server.IOrdersController;
import com.volna80.flush.ui.services.RiskCalculator;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Callback;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.volna80.betfair.api.model.adapter.Precision.toExternal;
import static com.volna80.betfair.api.model.adapter.Precision.toUI;
import static com.volna80.flush.ui.util.StringUtils.formatMoney;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class MarketOverviewController implements Initializable {

    private static final Logger logger = LoggerFactory.getLogger(MarketOverviewController.class);

    @Inject
    public IFlushAPI api;
    @Inject
    public volatile IOrdersController ordersController;

    @FXML
    public TableView<Record> table;
    public TextField search;
    private Timeline mdTimeline;
    private Timeline riskTimeline;

    @Override
    public void initialize(URL location, ResourceBundle resources) {


        TableColumn<Record, Long> timestamp = timestamp();
        table.getColumns().addAll(
                timestamp,
                date(),
                status(),
//                eventName(),
//                debugInfo(),
                eventCompetition(),
                runner1(),
                runner2(),
                runner1odds(),
                drawodds(),
                runner2odds(),
                openBuySellColumn(),
                riskColumn("openRisk", param -> param.getValue().openRisk),
                riskColumn2("MaxLost", param -> param.getValue().maxR),
                riskColumn2("PNL", param -> param.getValue().pnl),
                totalMatched(),
                totalAvailable()
        );

        table.getSortOrder().add(timestamp);


        //header

        search.setOnAction(event -> {
            Service<List<MarketCatalogue>> loadEvents = new Service<List<MarketCatalogue>>() {
                @Override
                protected Task<List<MarketCatalogue>> createTask() {
                    return new Task<List<MarketCatalogue>>() {
                        @Override
                        protected List<MarketCatalogue> call() throws Exception {
                            return api.getSoccerEvents(search.getText());
                        }


                    };
                }
            };
            loadEvents.setExecutor(IFlushAPI.executor);
            loadEvents.setOnSucceeded(event1 -> {
                List<MarketCatalogue> events = (List<MarketCatalogue>) event1.getSource().getValue();
                logger.info("load events: " + events);

                for (Record r : table.getItems()) {
                    api.unsubscribe(r.markedId, r.selection.s1);
                }

                table.getItems().clear();

                int i = 0;
                for (MarketCatalogue evnt : events) {
                    Record rec = new Record(evnt);
                    table.getItems().add(rec);

                    api.subscribe(rec.markedId, rec.selection.s1);//it is enough as it subscribe to a market
                }

                table.sort();

                enableSearch();


            });
            loadEvents.setOnFailed(event1 -> {
                logger.error("failed to load events. Reason: " + event1);
                enableSearch();
            });
            loadEvents.setOnRunning(event1 -> {
                logger.debug("loading: " + event1);
            });
            loadEvents.start();
            //block until we get a response from a market
            disableSearch();
        });

        mdTimeline = new Timeline(
                new KeyFrame(
                        Duration.seconds(10),
                        actionEvent -> {
                            logger.trace("refresh ui");


                            for (Record record : table.getItems()) {

                                MarketBook md = api.snap(record.markedId);

                                record.x1.setValue(toUI(RiskCalculator.getBestPrice(md, record.selection.s1, Side.BACK)) + " - " + toUI(RiskCalculator.getBestPrice(md, record.selection.s1, Side.LAY)));
                                record.x2.setValue(toUI(RiskCalculator.getBestPrice(md, record.selection.s2, Side.BACK)) + " - " + toUI(RiskCalculator.getBestPrice(md, record.selection.s2, Side.LAY)));
                                record.x.setValue(toUI(RiskCalculator.getBestPrice(md, record.selection.s3, Side.BACK)) + " - " + toUI(RiskCalculator.getBestPrice(md, record.selection.s3, Side.LAY)));

                                record.totalMatched.setValue(formatMoney(md.getTotalMatched()));
                                record.totalAvailable.setValue(formatMoney(md.getTotalAvailable()));

                                record.status.setValue(md.getStatus() == MarketStatus.CLOSED ? "X" :
                                        md.isInplay() ? "InPlay" : ""
                                );

                            }

                        }
                )
        );
        mdTimeline.setDelay(Duration.seconds(10));
        mdTimeline.setCycleCount(Timeline.INDEFINITE);
        mdTimeline.play();


        riskTimeline = new Timeline(new KeyFrame(
                Duration.seconds(30),
                actionEvent -> {

                    for (Record rec : table.getItems()) {

                        //TODO do all risk calculation outside of UI thread

                        Map<Side, Integer> openRisk = RiskCalculator.calcNotMatchedMoney(ordersController.getExecutableOrders(rec.markedId));
                        rec.openRisk.setValue(toExternal(openRisk.get(Side.BACK) + openRisk.get(Side.LAY)));

                        rec.maxR.setValue(toExternal(RiskCalculator.maxLost(ordersController.getMatchedOrders(rec.markedId), rec.selection)));

                        rec.pnl.setValue(toExternal(RiskCalculator.pnl(ordersController.getCompleteOrders(rec.markedId), api.snap(rec.markedId), rec.selection)));
                    }

                }
        ));
        riskTimeline.setDelay(Duration.seconds(30));
        riskTimeline.setCycleCount(Timeline.INDEFINITE);
        riskTimeline.play();
    }


    private void enableSearch() {
//        search.setEditable(true);
        search.setDisable(false);
//        search.getStyleClass().clear();
//        search.getStyleClass().add("text-field");
    }

    private void disableSearch() {
        search.setDisable(true);
//        search.getStyleClass().clear();
//        search.getStyleClass().add("text-input-disabled");
    }

    private TableColumn<Record, ?> totalAvailable() {
        final TableColumn<Record, String> column = new TableColumn<>("Total Available");
        column.setCellValueFactory(value -> value.getValue().totalAvailable);
        column.setSortable(true);
        column.setPrefWidth(90);

        return column;
    }

    private TableColumn<Record, ?> totalMatched() {
        final TableColumn<Record, String> column = new TableColumn<>("Total Matched");
        column.setCellValueFactory(value -> value.getValue().totalMatched);
        column.setSortable(true);
        column.setPrefWidth(90);

        return column;
    }


    private TableColumn<Record, String> date() {
        final TableColumn<Record, String> column = new TableColumn<>("Date");
        column.setCellValueFactory(value -> value.getValue().date);
        column.setPrefWidth(90);

        return column;
    }

    private TableColumn<Record, Long> timestamp() {
        final TableColumn<Record, Long> column = new TableColumn<>("TS");
        column.setCellValueFactory(value -> value.getValue().ts.asObject());
        column.setSortable(true);
        column.setPrefWidth(10);

        return column;
    }


    private TableColumn<Record, String> runner1() {
        final TableColumn<Record, String> column = new TableColumn<>("Runner 1");
        column.setCellValueFactory(value -> value.getValue().runner1);
        column.setPrefWidth(150);

        return column;
    }


    private TableColumn<Record, String> runner2() {
        final TableColumn<Record, String> column = new TableColumn<>("Runner 2");
        column.setCellValueFactory(value -> value.getValue().runner2);
        column.setPrefWidth(150);

        return column;
    }


    private TableColumn<Record, String> runner1odds() {
        final TableColumn<Record, String> column = new TableColumn<>("1");
        column.setCellValueFactory(value -> value.getValue().x1);
        return column;
    }


    private TableColumn<Record, String> drawodds() {
        final TableColumn<Record, String> column = new TableColumn<>("X");
        column.setCellValueFactory(value -> value.getValue().x);

        return column;
    }


    private TableColumn<Record, String> runner2odds() {
        final TableColumn<Record, String> column = new TableColumn<>("2");
        column.setCellValueFactory(value -> value.getValue().x2);
        return column;
    }

    private TableColumn<Record, ?> openBuySellColumn() {
        TableColumn<Record, Record> column = new TableColumn<>("");
        column.setCellValueFactory(value -> new ReadOnlyObjectWrapper(value.getValue()));
        column.setCellFactory(new Callback<TableColumn<Record, Record>, TableCell<Record, Record>>() {
            @Override
            public TableCell<Record, Record> call(TableColumn<Record, Record> currentOrderSummaryCurrentOrderSummaryTableColumn) {
                return new TableCell<Record, Record>() {

                    private final Button button = new Button("buy/sell");

                    {
                        button.setMinWidth(30);
                    }


                    @Override
                    protected void updateItem(final Record rec, boolean empty) {
                        super.updateItem(rec, empty);

                        if (rec != null && !empty) {

                            button.setOnAction(actionEvent -> {
                                final String marketId = rec.markedId;
                                final long selectionId = rec.selection.s1;

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

    private TableColumn<Record, ?> riskColumn(String title, Callback<TableColumn.CellDataFeatures<Record, Number>, ObservableValue<Number>> value) {
        final TableColumn<Record, Number> column = new TableColumn<>(title);
        column.setCellValueFactory(value);
        column.setCellFactory(new Callback<TableColumn<Record, Number>, TableCell<Record, Number>>() {
            @Override
            public TableCell<Record, Number> call(TableColumn<Record, Number> param) {
                return new TableCell<Record, Number>() {


                    @Override
                    protected void updateItem(Number item, boolean empty) {
                        super.updateItem(item, empty);

                        getStyleClass().clear();

                        if (empty) {
                            setText("");
                            getStyleClass().add("table-cell-green");
                            return;
                        }

                        setText(item.doubleValue() + "");

                        if (item.intValue() > 40) {
                            getStyleClass().add("table-cell-error");
                        } else if (item.intValue() > 20) {
                            getStyleClass().add("table-cell-warning");
                        } else {
                            getStyleClass().add("table-cell-green");
                        }

                    }
                };
            }
        });
        return column;
    }

    private TableColumn<Record, ?> riskColumn2(String title, Callback<TableColumn.CellDataFeatures<Record, Number>, ObservableValue<Number>> value) {
        final TableColumn<Record, Number> column = new TableColumn<>(title);
        column.setCellValueFactory(value);
        column.setCellFactory(new Callback<TableColumn<Record, Number>, TableCell<Record, Number>>() {
            @Override
            public TableCell<Record, Number> call(TableColumn<Record, Number> param) {
                return new TableCell<Record, Number>() {


                    @Override
                    protected void updateItem(Number item, boolean empty) {
                        super.updateItem(item, empty);

                        getStyleClass().clear();

                        if (empty) {
                            setText("");
                            getStyleClass().add("table-cell-green");
                            return;
                        }

                        setText(item.doubleValue() + "");

                        if (item.intValue() < -40) {
                            getStyleClass().add("table-cell-error");
                        } else if (item.intValue() < 0) {
                            getStyleClass().add("table-cell-warning");
                        } else {
                            getStyleClass().add("table-cell-green");
                        }

                    }
                };
            }
        });
        return column;
    }

    private TableColumn<Record, String> status() {
        final TableColumn<Record, String> column = new TableColumn<>("Status");
        column.setCellValueFactory(value -> value.getValue().status);
        column.setPrefWidth(50);
        return column;
    }


    private TableColumn<Record, String> eventCompetition() {
        final TableColumn<Record, String> column = new TableColumn<>("Competition");
        column.setCellValueFactory(value -> value.getValue().competition);
        column.setPrefWidth(150);
        return column;
    }

    public void close() {
        mdTimeline.stop();
        riskTimeline.stop();
        for (Record record : table.getItems()) {
            api.unsubscribe(record.markedId, record.selection.s1);
        }
    }

    public static class Record {

        private final StringProperty id;
        private final StringProperty eventName;
        private final StringProperty debugInfo;
        private final StringProperty competition;

        private final StringProperty runner1;
        private final StringProperty runner2;

        private final RiskCalculator.Selection selection;

        private final StringProperty x1 = new SimpleStringProperty();
        private final StringProperty x = new SimpleStringProperty();
        private final StringProperty x2 = new SimpleStringProperty();
        private final String markedId;
        private final StringProperty date = new SimpleStringProperty();
        ;
        private final StringProperty status = new SimpleStringProperty();

        //money in open orders
        private final DoubleProperty openRisk = new SimpleDoubleProperty();

        //a max possible lost (for matched bets)
        private final DoubleProperty maxR = new SimpleDoubleProperty();
        private final DoubleProperty pnl = new SimpleDoubleProperty();
        private final StringProperty totalMatched = new SimpleStringProperty();
        private final StringProperty totalAvailable = new SimpleStringProperty();
        private final SimpleLongProperty ts = new SimpleLongProperty();


        Record(MarketCatalogue evnt) {
            id = new SimpleStringProperty(evnt.getEvent().getId());
            eventName = new SimpleStringProperty(evnt.getEvent().getName());
            debugInfo = new SimpleStringProperty(evnt.getDescription().getMarketType());
            competition = new SimpleStringProperty(evnt.getCompetition().getName());

            runner1 = new SimpleStringProperty(evnt.getRunners().get(0).getRunnerName());
            runner2 = new SimpleStringProperty(evnt.getRunners().get(1).getRunnerName());

            selection = RiskCalculator.Selection.valueOf(evnt.getRunners().get(0).getSelectionId(), evnt.getRunners().get(1).getSelectionId(), evnt.getRunners().get(2).getSelectionId());

            markedId = evnt.getMarketId();

            GregorianCalendar from = new GregorianCalendar();
            from.setTime(evnt.getEvent().getOpenDate());

            GregorianCalendar to = new GregorianCalendar();
            to.setTimeZone(TimeZone.getTimeZone(evnt.getEvent().getTimezone()));
            to.set(Calendar.YEAR, from.get(Calendar.YEAR));
            to.set(Calendar.MONTH, from.get(Calendar.MONTH));
            to.set(Calendar.DAY_OF_MONTH, from.get(Calendar.DAY_OF_MONTH));
            to.set(Calendar.HOUR_OF_DAY, from.get(Calendar.HOUR_OF_DAY));
            to.set(Calendar.MINUTE, from.get(Calendar.MINUTE));
            to.set(Calendar.SECOND, from.get(Calendar.SECOND));
            to.set(Calendar.MILLISECOND, 0);


            LocalDateTime openDate = to.toZonedDateTime().withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();

            if (openDate.toLocalDate().equals(LocalDateTime.now().toLocalDate())) {
                date.setValue("Today (at " + openDate.format(DateTimeFormatter.ISO_LOCAL_TIME) + ")"); //TODO locale
            } else if (openDate.toLocalDate().equals(LocalDateTime.now().plusDays(1).toLocalDate())) {
                date.setValue("Tomorrow (at " + openDate.format(DateTimeFormatter.ISO_LOCAL_TIME) + ")"); //TODO locale
            } else {
                date.setValue(openDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            }

            ts.setValue(evnt.getEvent().getOpenDate().getTime());
        }
    }

}
