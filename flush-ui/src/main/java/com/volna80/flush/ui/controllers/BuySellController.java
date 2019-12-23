package com.volna80.flush.ui.controllers;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.volna80.betfair.api.model.*;
import com.volna80.betfair.api.model.adapter.Precision;
import com.volna80.flush.server.latency.ILatencyRecorder;
import com.volna80.flush.server.latency.LatencyRecorderFactory;
import com.volna80.flush.ui.ApplicationManager;
import com.volna80.flush.ui.Preferences;
import com.volna80.flush.ui.marketdata.IMDLevel;
import com.volna80.flush.ui.model.UIMarketInfo;
import com.volna80.flush.ui.server.IFlushAPI;
import com.volna80.flush.ui.server.IOrdersController;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.volna80.betfair.api.model.MarketStatus.CLOSED;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class BuySellController implements Initializable {

    public static final String STYLE_BUY_SELL_YES_WAITING_ORDER = "buy-sell-yes-waiting-order";
    public static final String STYLE_BUY_SELL_NO_WAITING_ORDER = "buy-sell-no-waiting-order";
    public static final String STYLE_BUY_SELL_YES_CANCELLING_ORDER = "buy-sell-yes-cancelling-order";
    public static final String STYLE_BUY_SELL_NO_CANCELLING_ORDER = "buy-sell-no-cancelling-order";
    //TODO adjust to screen size ???
    public static final int NUMBER_OF_ROWS = 75;
    private static final Logger log = LoggerFactory.getLogger(BuySellController.class);
    @Inject
    public IOrdersController ordersController;
    @Inject
    public IFlushAPI api;
    @Inject
    @Named("marketId")
    public String marketId;

    @Inject
    @Named("selectionId")
    public long selectionId;
    public Rectangle latency;
    public Label latencyLabel;
    public VBox leftPanel;
    public Button size1;
    public Button size2;
    public Button size3;
    public TextField quantity;
    public Label eventName;
    public Label marketName;
    public Label runnerName;
    public Label competitionName;
    public Label eventTypeName;
    public Label marketStatusLabel;
    public Rectangle marketStatus;
    public GridPane grid;
    @VisibleForTesting
    Timeline timeline;
    private boolean alive = true;
    private volatile MarketCatalogue marketInfo;
    private IMDLevel.Type priceType;
    private MarketStatus status = MarketStatus.UNKNOWN;
    private DecimalFormat format;
    private TradeVolumeMode tradeVolumeMode = TradeVolumeMode.ALL;
    //true - need to refresh prices
    private boolean indexDirty = true;
    //false - we have not receive market data yet
    private boolean initIndex = false;
    private int indexOffset = 0;
    private int[] pricesByIndex;
    private int[] indexByPrice;
    private Map<Integer, TradedVolume> tradedVolumeMap = new HashMap<>();
    private Row[] rows;
    private ILatencyRecorder recorder = LatencyRecorderFactory.getRecorder("ui.buy-sell");
    private Calendar calendar = new GregorianCalendar();

    public void onResizeWidth(Number value) {
        grid.setPrefWidth(Math.max(280, value.longValue() - 120));
    }

    public void onResizeHeight(Number value) {
        grid.setPrefHeight(value.longValue());
    }

    private void placeOrCancel(Side side, int index, int size) {
        OptionalInt price = getPrice(index);

        log.debug("placeOrCancel:{}:{}@{}", side, price, size);

        if (!price.isPresent()) {
            return;
        }


        final boolean hasOrders = !(side == Side.BACK ? rows[index].yes.getText().isEmpty() : rows[index].no.getText().isEmpty());


        if (hasOrders) {

            //if we have any reaming orders at a given side/price, we should cancel it

            List<CurrentOrderSummary> orders = ordersController.getCancelableOrders(marketId, selectionId).stream()
                    .filter(summary -> summary.getSide() == side)
                    .filter(summary -> summary.getPriceSize().getPrice() == price.getAsInt())
                    .collect(Collectors.toList());

            if (orders.size() > 0) {
                if (side == Side.BACK) {
                    rows[index].yesPane.getStyleClass().clear();
                    rows[index].yesPane.getStyleClass().add(STYLE_BUY_SELL_YES_CANCELLING_ORDER);
                } else {
                    rows[index].noPane.getStyleClass().clear();
                    rows[index].noPane.getStyleClass().add(STYLE_BUY_SELL_NO_CANCELLING_ORDER);
                }

                ordersController.cancelOrders(marketId, orders.stream().map(CurrentOrderSummary::getBetId).collect(Collectors.toList()));
            } else {
                log.debug("orders haven't been confirmed yet, try again a second later");
            }


        } else {

            if (size == 0) {
                return;
            }

            //ok, no unconfirmed and confirmed orders, place new order
            ordersController.placeNewOrder(marketId, selectionId, price.getAsInt(), size, side, api.nextRef());
            quantity.setText("");
            refreshUnconfirmedOrders();


        }
    }

    //return a price by a current row index
    private OptionalInt getPrice(int index) {
        return index + indexOffset < pricesByIndex.length ? OptionalInt.of(pricesByIndex[index + indexOffset]) : OptionalInt.empty();
    }

    //return a visiable row index for a given price or null
    private Optional<Integer> getIndex(int price) {
        final int index = indexByPrice[price] - indexOffset;
        //check range
        if (0 <= index && index < NUMBER_OF_ROWS) {
            return Optional.of(index);
        }
        return Optional.empty();
    }

    private int getQty() {
        if (quantity.getText().isEmpty()) {
            return 0;
        }
        return Precision.toInternal(quantity.getText());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        log.info("initialize " + this);

        checkNotNull(marketId);
        checkNotNull(selectionId);

        this.format = new DecimalFormat(resourceBundle.getString("buy-sell.latency.label"));


        final ContextMenu tradedContextMenu = new ContextMenu();
        final CheckMenuItem item1 = new CheckMenuItem("All"); //TODO i18n
        item1.setSelected(true);
        final CheckMenuItem item2 = new CheckMenuItem("Last minute");

//        item1.setSelected(true);
        item1.setOnAction(e -> {
            item1.setSelected(true);
            item2.setSelected(false);
            tradeVolumeMode = TradeVolumeMode.ALL;
        });

        item2.setOnAction(e -> {
            item1.setSelected(false);
            item2.setSelected(true);
            tradeVolumeMode = TradeVolumeMode.LAST_MINUTE;
        });
        tradedContextMenu.getItems().addAll(item1, item2);


        final ContextMenu leftMenu = new ContextMenu();

        leftPanel.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                leftMenu.show(leftPanel, javafx.geometry.Side.RIGHT, e.getSceneX(), e.getSceneY());
            }
        });

        loadMarketCatalogueInfo(leftMenu);

        refreshMarketStatus();

        size1.setText(Preferences.getBetSize1() + "");
        size2.setText(Preferences.getBetSize2() + "");
        size3.setText(Preferences.getBetSize3() + "");

        timeline = new Timeline(
                new KeyFrame(
                        Duration.seconds(1),
                        actionEvent -> {

                            recorder.start();
                            log.debug("refresh md {}:{}", marketId, selectionId);

                            if (marketId == null) {
                                return;
                            }

                            refreshUI();

                            recorder.stop();
                        }
                )
        );
        timeline.setDelay(Duration.seconds(1));
        timeline.setCycleCount(Timeline.INDEFINITE);

        UIMarketInfo info = api.getDictionary().getMarketInfo(marketId);
        eventName.textProperty().bind(info.eventNameProperty());
        marketName.textProperty().bind(info.marketNameProperty());
        runnerName.textProperty().bind(info.getRunner(selectionId));
        competitionName.textProperty().bind(info.competitionNameProperty());
        eventTypeName.textProperty().bind(info.eventTypeNameProperty());

        api.subscribe(marketId, selectionId);

        //init a ladder
        rows = new Row[NUMBER_OF_ROWS];
        for (int i = 0; i < NUMBER_OF_ROWS; i++) {

            Row row = new Row(i);
            row.init();
            rows[i] = row;

            //YES
            {
                grid.add(row.yesPane, 0, i);

            }

            //YES ORDERS
            {
                StackPane p = new StackPane();
                p.getStyleClass().add("buy-sell-cell-yes-" + (i % 2 == 0 ? "even" : "odd"));
                p.setAlignment(Pos.CENTER);
                p.getChildren().add(row.yesMarketQty);
                grid.add(p, 1, i);
            }

            //PRICE
            {
                grid.add(row.pricePane, 2, i);
            }


            //NO ORDERS
            {
                StackPane p = new StackPane();
                p.getStyleClass().addAll("buy-sell-cell-no-" + (i % 2 == 0 ? "even" : "odd"));
                p.setAlignment(Pos.CENTER);
                p.getChildren().add(row.noMarketQty);
                grid.add(p, 3, i);
            }

            //NO
            {
                grid.add(row.noPane, 4, i);

            }

            //TRADED
            {
                StackPane p1 = new StackPane();
                p1.getStyleClass().addAll("buy-sell-cell-price-" + (i % 2 == 0 ? "even" : "odd"));
                p1.setAlignment(Pos.CENTER);
                p1.getChildren().add(row.traded);

                p1.setOnMouseClicked(event -> {
                    tradedContextMenu.show(p1, javafx.geometry.Side.BOTTOM, 0, 0);

                });

                grid.add(p1, 5, i);
            }
        }

        //scrol grid
        grid.setOnScroll(this::onScrol);

    }

    private void onScrol(ScrollEvent event) {
        if (event.getDeltaY() < 0) {
            indexOffset++;
        } else {
            indexOffset--;
        }

        if (indexOffset < 0) {
            indexOffset = 0;
        }
//        if((indexOffset + NUMBER_OF_ROWS) > pricesByIndex.length) {indexOffset = pricesByIndex.length - NUMBER_OF_ROWS;}
        indexDirty = true;
        refreshUI();

    }

    void refreshUI() {

        if (!initIndex) {
            //scroll a ladder to a mid price
            MarketBook book = api.snap(marketId);
            Optional<Runner> runner = book.getRunners().stream().filter(r -> r.getSelectionId() == selectionId).findAny();
            if (runner.isPresent()) {
                indexOffset = indexByPrice[IMDLevel.getMidPrice(runner.get().getEx(), priceType)] - 10;
                if (indexOffset < 0) {
                    indexOffset = 0;
                }
                initIndex = true;
            }
        }


        //clear
        for (Row r : rows) {
            r.clear();
        }

        refreshMarketData();

        refreshExecutableOrders();

        refreshUnconfirmedOrders();

        refreshCancellingOrders();

        indexDirty = false;
    }

    private void loadMarketCatalogueInfo(ContextMenu leftMenu) {
        Preconditions.checkNotNull(marketId);

        Service<List<MarketCatalogue>> service = new Service<List<MarketCatalogue>>() {
            @Override
            protected Task<List<MarketCatalogue>> createTask() {
                return new Task<List<MarketCatalogue>>() {
                    @Override
                    protected List<MarketCatalogue> call() throws Exception {
                        //get market info
                        marketInfo = api.getMarketInfo(marketId);
                        Preconditions.checkNotNull(marketInfo);
                        //load all possible markets
                        return api.getAllMarketsForEvent(marketInfo.getEvent());
                    }
                };
            }
        };
        service.setExecutor(IFlushAPI.executor);
        service.setOnSucceeded(event -> {
            List<MarketCatalogue> result = (List<MarketCatalogue>) event.getSource().getValue();

            result.stream().sorted().forEach(
                    market -> {

                        Menu group = new Menu(market.getMarketName());
                        leftMenu.getItems().addAll(group);

                        market.getRunners().forEach(runner -> {
                            MenuItem item = new MenuItem(runner.getRunnerName() + (Double.isNaN(runner.getHandicap()) || Precision.isZero(runner.getHandicap()) ? "" : " " + runner.getHandicap()));
                            item.setOnAction(event1 -> {
                                ApplicationManager.getInstance().openBuySell(market.getMarketId(), runner.getSelectionId());
                            });
                            group.getItems().addAll(item);
                        });
                    }
            );

            log.info("the market list has been load for " + marketId);
            log.info("market info: {}", marketInfo);

            //init a ladder settings and price indexes
            final boolean isAsianHandicap = marketInfo.getDescription().getBettingType() == MarketBettingType.ASIAN_HANDICAP_DOUBLE_LINE ||
                    marketInfo.getDescription().getBettingType() == MarketBettingType.ASIAN_HANDICAP_SINGLE_LINE;

            pricesByIndex = isAsianHandicap ? IMDLevel.levels2 : IMDLevel.levels;
            indexByPrice = isAsianHandicap ? IMDLevel.index2ByPrice : IMDLevel.indexByPrice;
            priceType = isAsianHandicap ? IMDLevel.Type.ASIAN_HANDICAP : IMDLevel.Type.STANDARD;

            //start a ladder UI
            timeline.play();

        });

        service.setOnFailed(event -> {
            log.error("could not load list of markets for left menu. " + event);
        });
        service.start();


    }

    private void refreshCancellingOrders() {
        final Collection<CurrentOrderSummary> list = ordersController.getCancellingOrders(marketId, selectionId);

        list.stream().filter(summary -> summary.getSide() == Side.BACK).forEach(summary -> {

            Optional<Integer> index = getIndex(summary.getPriceSize().getPrice());

            if (index.isPresent()) {
                Label label = rows[index.get()].yes;
                label.setText(Precision.toUI(summary.getSizeRemaining()));
                rows[index.get()].yesPane.getStyleClass().clear();
                rows[index.get()].yesPane.getStyleClass().add(STYLE_BUY_SELL_YES_CANCELLING_ORDER);
            }

        });

        list.stream().filter(summary -> summary.getSide() == Side.LAY).forEach(summary -> {
            Optional<Integer> index = getIndex(summary.getPriceSize().getPrice());

            if (index.isPresent()) {
                Label label = rows[index.get()].no;
                label.setText(Precision.toUI(summary.getSizeRemaining()));
                rows[index.get()].noPane.getStyleClass().clear();
                rows[index.get()].noPane.getStyleClass().add(STYLE_BUY_SELL_NO_CANCELLING_ORDER);
            }
        });

    }

    private void refreshMarketStatus() {
        marketStatusLabel.setText(status.name()); //TODO i18n
        switch (status) {
            case CLOSED:
            case UNKNOWN:
                marketStatus.setFill(Paint.valueOf("#757575"));
                break;
            case INACTIVE:
            case SUSPENDED:
                marketStatus.setFill(Paint.valueOf("#EF6C00"));
                break;
            case OPEN:
                marketStatus.setFill(Paint.valueOf("#7986CB"));
        }
    }

    private void refreshLatency(long timestamp) {
        final double latency = (System.currentTimeMillis() - timestamp) / 1000.;
        latencyLabel.setText(format.format(latency));

        if (latency < 1) {
            this.latency.setFill(Paint.valueOf("#7986CB"));
        } else if (latency < 3) {
            this.latency.setFill(Paint.valueOf("#EF6C00"));
        } else {
            this.latency.setFill(Paint.valueOf("#F44336"));
        }

    }

    public void refreshMarketData() {

        MarketBook book = api.snap(marketId);

        Optional<Runner> runner = book.getRunners().stream().filter(r -> r.getSelectionId() == selectionId).findAny();

        if (!runner.isPresent()) {
            log.error("runner is not present in the order book. {}, {}", book, selectionId);
            return;
        }


        if (alive && !ordersController.isActive()) {
            //disconnected
            alive = false;
            marketStatusLabel.setText("DISCONNECTED");
            marketStatus.setFill(Paint.valueOf("#757575"));
        } else if (alive) {
            if (status != book.getStatus()) {
                status = book.getStatus();
                refreshMarketStatus();
            }
        }

        if (status == CLOSED) {
            //TODO close the window ???
            return;
        }

        refreshLatency(book.timestamp);

        for (PriceSize ps : runner.get().getEx().getAvailableToBack()) {
            Optional<Integer> index = getIndex(ps.getPrice());
            if (index.isPresent()) {
                Label l = rows[index.get()].noMarketQty;
                l.setText(Precision.toUI(ps.getSize(), 0));
            }
        }

        for (PriceSize ps : runner.get().getEx().getAvailableToLay()) {
            Optional<Integer> index = getIndex(ps.getPrice());
            if (index.isPresent()) {
                Label l = rows[index.get()].yesMarketQty;
                l.setText(Precision.toUI(ps.getSize(), 0));
            }
        }


        calendar.setTimeInMillis(System.currentTimeMillis());
        int seconds = calendar.get(Calendar.SECOND);
        int quarter = seconds / 15;

        if (runner.get().getEx().getTradedVolume() != null) {
            for (PriceSize ps : runner.get().getEx().getTradedVolume()) {

                TradedVolume tradedVolume = tradedVolumeMap.get(ps.getPrice());
                if (tradedVolume == null) {
                    tradedVolume = new TradedVolume();
                    tradedVolumeMap.put(ps.getPrice(), tradedVolume);
                }


                if (tradedVolume.savePoints[quarter] == 0 && ps.getSize() != 0) {
                    //first time
                    Arrays.fill(tradedVolume.savePoints, ps.getSize());
                }

                tradedVolume.savePoints[quarter] = ps.getSize();

                Optional<Integer> index = getIndex(ps.getPrice());
                if (index.isPresent()) {
                    Row row = rows[index.get()];
                    Label l = row.traded;


                    if (tradeVolumeMode == TradeVolumeMode.ALL) {
                        l.setText(Precision.toUI(ps.getSize(), 0));
                    } else if (tradeVolumeMode == TradeVolumeMode.LAST_MINUTE) {

                        //shift
                        //for example, now 5 sec. we will use the date from the previous 15-30 interval
                        //so, actually, we save and show the last 45-60 second
                        int tradedSize = ps.getSize() - tradedVolume.savePoints[(quarter + 1) % 4];

                        if (tradedSize > 0) {
                            l.setText(Precision.toUI(tradedSize, 0));
                        }
                    }
                }
            }
        }

    }

    private void refreshUnconfirmedOrders() {
        final Collection<CurrentOrderSummary> list = ordersController.getUnconfirmedOrders(marketId, selectionId);

        list.stream().filter(summary -> summary.getSide() == Side.BACK).forEach(summary -> {
            Optional<Integer> index = getIndex(summary.getPriceSize().getPrice());
            if (index.isPresent()) {
                Label label = rows[index.get()].yes;
                label.setText(Precision.toUI(summary.getSizeRemaining()));
                rows[index.get()].yesPane.getStyleClass().clear();
                rows[index.get()].yesPane.getStyleClass().add(STYLE_BUY_SELL_YES_WAITING_ORDER);
            }

        });

        list.stream().filter(summary -> summary.getSide() == Side.LAY).forEach(summary -> {
            Optional<Integer> index = getIndex(summary.getPriceSize().getPrice());
            if (index.isPresent()) {
                Label label = rows[index.get()].no;
                label.setText(Precision.toUI(summary.getSizeRemaining()));
                rows[index.get()].noPane.getStyleClass().clear();
                rows[index.get()].noPane.getStyleClass().add(STYLE_BUY_SELL_NO_WAITING_ORDER);
            }
        });
    }

    private void refreshExecutableOrders() {
        ordersController.getExecutableOrders(marketId, selectionId).stream().forEach(
                summary -> {
                    Optional<Integer> index = getIndex(summary.getPriceSize().getPrice());

                    if (index.isPresent()) {
                        Label label;
                        if (summary.getSide() == Side.BACK) {
                            label = rows[index.get()].yes;
                        } else {
                            label = rows[index.get()].no;
                        }

                        if (label.getText().isEmpty()) {
                            label.setText(Precision.toUI(summary.getSizeRemaining()));
                        } else {
                            int prev = Precision.toInternal(label.getText());
                            label.setText(Precision.toUI(summary.getSizeRemaining() + prev));
                        }
                    }
                }
        );
    }

    public void cancelAll(ActionEvent actionEvent) {
        ordersController.cancelOrders(marketId);
    }

    //press the button qty 1
    public void onQty1(ActionEvent event) {
        quantity.setText(size1.getText());
    }

    //press the button qty 2
    public void onQty2(ActionEvent event) {
        quantity.setText(size2.getText());
    }

    //press the button qty 3
    public void onQty3(ActionEvent event) {
        quantity.setText(size3.getText());
    }

    public void close() {
        timeline.stop();
        api.unsubscribe(marketId, selectionId);
    }

    private enum TradeVolumeMode {ALL, LAST_MINUTE}

    private class TradedVolume {
        //save point
        private int savePoints[] = new int[4];
    }

    private class Row {


        final StackPane noPane = new StackPane();
        final Label no = new Label();
        final Label noMarketQty = new Label();
        final StackPane pricePane = new StackPane();
        final Label price = new Label();
        final Label yesMarketQty = new Label();
        final Label yes = new Label();
        final StackPane yesPane = new StackPane();
        final Label traded = new Label();


        private final int index;

        private Row(int index) {
            this.index = index;
        }

        public void init() {

            yes.getStyleClass().add("buy-sell-cell-label");
            no.getStyleClass().add("buy-sell-cell-label");
            yesMarketQty.getStyleClass().add("buy-sell-cell-label");
            noMarketQty.getStyleClass().add("buy-sell-cell-label");
            traded.getStyleClass().addAll("buy-sell-cell-label");
            price.getStyleClass().add(".buy-sell-cell-label");

            yesPane.getStyleClass().addAll("buy-sell-cell-place-yes-" + (index % 2 == 0 ? "even" : "odd"));
            yesPane.setAlignment(Pos.CENTER);
            yesPane.getChildren().add(yes);
            yesPane.setOnMouseClicked(mouseEvent -> placeOrCancel(Side.BACK, index, getQty()));

            pricePane.getStyleClass().addAll("buy-sell-cell-price-" + (index % 2 == 0 ? "even" : "odd"));
            pricePane.setAlignment(Pos.CENTER);
            pricePane.getChildren().add(price);

            noPane.getStyleClass().addAll("buy-sell-cell-place-no-" + (index % 2 == 0 ? "even" : "odd"));
            noPane.setAlignment(Pos.CENTER);
            noPane.getChildren().add(no);
            noPane.setOnMouseClicked(mouseEvent -> placeOrCancel(Side.LAY, index, getQty()));

        }

        public void clear() {


            if (!yes.getText().isEmpty()) {
                yesPane.getStyleClass().clear();
                yesPane.getStyleClass().addAll("buy-sell-cell-place-yes-" + (index % 2 == 0 ? "even" : "odd"));
                yes.setText("");
            }

            if (!no.getText().isEmpty()) {
                noPane.getStyleClass().clear();
                noPane.getStyleClass().addAll("buy-sell-cell-place-no-" + (index % 2 == 0 ? "even" : "odd"));
                no.setText("");
            }

            refreshPrice();

            if (!noMarketQty.getText().isEmpty()) {
                noMarketQty.setText("");
            }
            if (!yesMarketQty.getText().isEmpty()) {
                yesMarketQty.setText("");
            }

            if (!traded.getText().isEmpty()) {
                traded.setText("");
            }

        }

        private void refreshPrice() {
            if (indexDirty) {
                final OptionalInt basePrice = getPrice(index);
                pricePane.getStyleClass().addAll("buy-sell-cell-price-" + (index % 2 == 0 ? "even" : "odd"));

                if (basePrice.isPresent()) {
                    double winProbability = Precision.round((1 / ((double) basePrice.getAsInt() / 100)) * 100, 1);
                    double looseProbability = Precision.round(100 - winProbability, 1);
                    price.setText(Precision.toUI(basePrice.getAsInt()));
                    price.setTooltip(new Tooltip("yes\t" + winProbability + " %\nno\t" + looseProbability + " %")); //TODO local
                } else {
                    price.setText("-");
                }
            }
        }
    }


}
