package com.volna80.flush.ui;

import javafx.application.Application;
import javafx.stage.Stage;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.fail;

/**
 * <p>
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class AsNonApp extends Application {

    private static AtomicBoolean started = new AtomicBoolean(false);

    public static void initJFX() {

        if (started.compareAndSet(false, true)) {
            final CountDownLatch latch = new CountDownLatch(1);
            Thread t = new Thread("JavaFX Init Thread") {
                public void run() {
                    try {
                        Application.launch(AsNonApp.class, new String[0]);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        latch.countDown();
                    }

                }
            };
            t.setDaemon(true);
            t.start();

            try {
                latch.await(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                fail("" + e);
            }

        }


    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // noop
    }
}
