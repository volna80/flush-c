package com.volna80.flush.ui.windows;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.volna80.flush.ui.ApplicationManager;
import com.volna80.flush.ui.Constants;
import com.volna80.flush.ui.DefaultModule;
import com.volna80.flush.ui.controllers.BuySellController;
import com.volna80.flush.ui.model.UIMarketInfo;
import com.volna80.flush.ui.server.IFlushAPI;
import com.volna80.flush.ui.server.IOrdersController;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ResourceBundle;

/**
 * <p>
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class BuySellWindow extends Stage {

    private static Logger logger = LoggerFactory.getLogger(BuySellWindow.class);

    public BuySellWindow(String market, long selectionId, IOrdersController ordersController, IFlushAPI api) {

        Injector injector = Guice.createInjector(new DefaultModule(ordersController, api, null, market, selectionId));

        FXMLLoader loader = new FXMLLoader(ApplicationManager.class.getResource("/flush/buy-sell.fxml"));
        loader.setControllerFactory(injector::getInstance);
        loader.setResources(ResourceBundle.getBundle(Constants.BUNDLES_FLUSH));
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        final BuySellController controller = loader.getController();

        //
        setHeight(600);

        Parent root = loader.getRoot();

        Scene scene = new Scene(root);
        scene.getStylesheets().add(Constants.FLUSH_CSS);

        this.setOnCloseRequest(
                windowEvent -> {
                    logger.info("closing buySell for {}:{}", market, selectionId);
                    controller.close();
                    this.titleProperty().unbind();
                }
        );

        this.widthProperty().addListener((observable, oldValue, newValue) -> {
            controller.onResizeWidth(newValue);
        });

        this.heightProperty().addListener((observable, oldValue, newValue) -> {
            controller.onResizeHeight(newValue);
        });

        UIMarketInfo info = api.getDictionary().getMarketInfo(market);
        this.titleProperty().bind(Bindings.concat(info.eventNameProperty(), " : ", info.getRunner(selectionId)));

        this.setScene(scene);
    }
}
