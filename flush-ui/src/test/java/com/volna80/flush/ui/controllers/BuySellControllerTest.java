package com.volna80.flush.ui.controllers;

import com.volna80.betfair.api.BetfairException;
import com.volna80.betfair.api.model.*;
import com.volna80.flush.ui.Constants;
import com.volna80.flush.ui.Preferences;
import com.volna80.flush.ui.model.UIMarketInfo;
import javafx.animation.Animation;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * <p>
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class BuySellControllerTest extends AbstractControllerTest {

    private static final String MARKET_ID = "marketId";
    private static final int SELECTION_ID = 1001;

    private static final int YES_MD_COL = 1;
    private static final int PRICE_COL = 2;
    private static final int NO_MD_COL = 3;


    private BuySellController controller;


    @Before
    public void setup() throws BetfairException {
        super.setup();
        controller = null;
    }


    @Test
    public void test() throws Exception {


        MarketBook book = new MarketBook();
        book.setStatus(MarketStatus.OPEN);

        Runner runner = new Runner();
        runner.setSelectionId(SELECTION_ID);
        runner.setEx(new ExchangePrices());
        runner.getEx().setAvailableToBack(Arrays.asList(new PriceSize(101, 100)));
        runner.getEx().setAvailableToLay(Arrays.asList(new PriceSize(102, 100)));
        book.setRunners(Arrays.asList(runner));

        when(api.snap(MARKET_ID)).thenReturn(book);

        final UIMarketInfo info = new UIMarketInfo(MARKET_ID);
        when(dictionary.getMarketInfo(MARKET_ID)).thenReturn(info);

        Event event = new Event();
        MarketCatalogue marketCatalogue = new MarketCatalogue();
        marketCatalogue.setEvent(event);
        marketCatalogue.setMarketName("marketName");
        marketCatalogue.setMarketId(MARKET_ID);
        marketCatalogue.setRunners(Collections.singletonList(new RunnerCatalog()));
        marketCatalogue.setDescription(new MarketDescription());
        when(api.getMarketInfo(MARKET_ID)).thenReturn(marketCatalogue);

        when(api.getAllMarketsForEvent(event)).thenReturn(Collections.singletonList(marketCatalogue));

        final CountDownLatch latch = new CountDownLatch(1);

        final ResourceBundle resourceBundle = ResourceBundle.getBundle(Constants.BUNDLES_FLUSH);

        Platform.runLater(() -> {
            try {
                controller = new BuySellController();
                controller.size1 = new Button();
                controller.size2 = new Button();
                controller.size3 = new Button();
                controller.grid = new GridPane();
                controller.leftPanel = new VBox();
                controller.quantity = new TextField();
                controller.eventName = new Label();
                controller.marketName = new Label();
                controller.runnerName = new Label();
                controller.competitionName = new Label();
                controller.eventTypeName = new Label();
                controller.marketStatus = new Rectangle();
                controller.marketStatusLabel = new Label();
                controller.latencyLabel = new Label();
                controller.latency = new Rectangle();
                controller.api = BuySellControllerTest.this.api;
                controller.ordersController = BuySellControllerTest.this.ordersController;
                controller.marketId = MARKET_ID;
                controller.selectionId = SELECTION_ID;
                controller.initialize(null, resourceBundle);

            } catch (Exception e) {
                fail(e);
            }
            latch.countDown();
        });

        latch.await(TIMEOUT, TimeUnit.SECONDS);

        Mockito.verify(api).subscribe(MARKET_ID, SELECTION_ID);
        verify();


        for (int i = 0; i < 10; i++) {
            if (controller.timeline.getStatus() != Animation.Status.RUNNING) {
                Thread.sleep(100);
            }
        }

        final CountDownLatch latch2 = new CountDownLatch(1);
        Platform.runLater(() -> {
            info.marketNameProperty().set("MarketName");
            controller.refreshUI();
            latch2.countDown();
        });

        latch2.await(TIMEOUT, TimeUnit.SECONDS);


        Mockito.verify(api, Mockito.atLeastOnce()).snap(MARKET_ID);

        assertEquals("1.01", getNodeFromGridPane(controller.grid, PRICE_COL, 0));
        assertEquals("1", getNodeFromGridPane(controller.grid, NO_MD_COL, 0));
        assertEquals("", getNodeFromGridPane(controller.grid, YES_MD_COL, 0));

        assertEquals(Preferences.getBetSize1(), (int) Integer.valueOf(controller.size1.getText()));
        assertEquals(Preferences.getBetSize2(), (int) Integer.valueOf(controller.size2.getText()));
        assertEquals(Preferences.getBetSize3(), (int) Integer.valueOf(controller.size3.getText()));

    }

    private String getNodeFromGridPane(GridPane gridPane, int col, int row) {
        for (Node node : gridPane.getChildren()) {
            if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                return ((Label) ((StackPane) node).getChildren().get(0)).getText();
            }
        }
        return null;
    }
}
