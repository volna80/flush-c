package com.volna80.flush.ui.controllers;

import com.volna80.betfair.api.BetfairException;
import com.volna80.flush.ui.ApplicationManager;
import com.volna80.flush.ui.AsNonApp;
import com.volna80.flush.ui.IApplicationManager;
import com.volna80.flush.ui.server.IDictionary;
import com.volna80.flush.ui.server.IFlushAPI;
import com.volna80.flush.ui.server.IOrdersController;
import javafx.application.Platform;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * <p>
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class AbstractControllerTest {

    public static final int TIMEOUT = 10;
    private static Logger logger = LoggerFactory.getLogger(AbstractControllerTest.class);
    protected IApplicationManager manager;
    protected volatile IFlushAPI api;
    protected volatile IOrdersController ordersController;
    protected IDictionary dictionary;

    protected boolean fail = false;
    protected String message;

    @BeforeClass
    public static void initJFX() {
        AsNonApp.initJFX();
    }

    @Before
    public void setup() throws BetfairException {
        manager = mock(IApplicationManager.class);
        ordersController = mock(IOrdersController.class);
        when(ordersController.isActive()).thenReturn(true);

        api = mock(IFlushAPI.class);
        dictionary = mock(IDictionary.class);
        when(api.getDictionary()).thenReturn(dictionary);

        Platform.runLater(() -> {
            ApplicationManager.setInstance(manager);
        });

    }

    @After
    public void tearDown() {
        api = null;
    }

    protected final void fail(Exception e) {
        logger.error(e.getMessage(), e);
        fail = true;
        message = e.getMessage();
    }

    protected final void verify() {
        assertFalse("Unexpected exception: " + message, fail);
    }

}
