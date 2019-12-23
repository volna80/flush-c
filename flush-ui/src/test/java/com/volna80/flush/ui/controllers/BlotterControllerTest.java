package com.volna80.flush.ui.controllers;

import com.volna80.flush.ui.Constants;
import com.volna80.flush.ui.server.IOrdersController;
import javafx.application.Platform;
import javafx.scene.control.TableView;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class BlotterControllerTest extends AbstractControllerTest {

    @Test
    public void test() throws InterruptedException {

        final ResourceBundle resourceBundle = ResourceBundle.getBundle(Constants.BUNDLES_FLUSH);

        final CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                BlotterController controller = new BlotterController();
                controller.activeOrders = new TableView<>();
                controller.matchedOrders = new TableView<>();
                controller.messages = new TableView<>();
                controller.ordersController = Mockito.mock(IOrdersController.class);


                controller.initialize(null, resourceBundle);
            } catch (Exception e) {
                fail(e);
            }
            latch.countDown();
        });

        latch.await(TIMEOUT, TimeUnit.SECONDS);

        verify();


    }
}
