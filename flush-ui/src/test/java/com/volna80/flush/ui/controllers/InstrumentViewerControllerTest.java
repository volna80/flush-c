package com.volna80.flush.ui.controllers;

import com.volna80.flush.ui.Constants;
import javafx.application.Platform;
import javafx.scene.control.Accordion;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import org.junit.Test;

import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class InstrumentViewerControllerTest extends AbstractControllerTest {


    @Test
    public void test() throws InterruptedException {

        final CountDownLatch latch = new CountDownLatch(1);

        final ResourceBundle resourceBundle = ResourceBundle.getBundle(Constants.BUNDLES_FLUSH);

        Platform.runLater(() -> {
            try {
                InstrumentViewerController controller = new InstrumentViewerController();
                controller.competitions = new ListView<>();
                controller.countries = new ChoiceBox<>();
                controller.events = new Accordion();
                controller.eventTypes = new ChoiceBox<>();
                controller.markets = new Accordion();
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
