package com.volna80.flush.ui.controllers;

import com.google.inject.Inject;
import com.volna80.betfair.api.model.AccountDetails;
import com.volna80.flush.ui.ApplicationManager;
import com.volna80.flush.ui.server.IFlushAPI;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class MainMenuController implements Initializable, IController {

    private static final Logger log = LoggerFactory.getLogger(MainMenuController.class);
    private Stage stage;

    @Inject
    private IFlushAPI api;

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void init() {

        Service<AccountDetails> getDetails = new Service<AccountDetails>() {
            @Override
            protected Task<AccountDetails> createTask() {
                return new Task<AccountDetails>() {
                    @Override
                    protected AccountDetails call() throws Exception {
                        log.debug("call{} getDetails");
                        return api.getAccountDetails();
                    }
                };
            }
        };

        getDetails.setExecutor(IFlushAPI.executor);

        getDetails.setOnSucceeded(
                workerStateEvent -> {
                    AccountDetails a = (AccountDetails) workerStateEvent.getSource().getValue();
                    log.debug("has load account details {}", a);
                    stage.setTitle(a.getFirstName() + " " + a.getLastName());
                }
        );

        getDetails.start();

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void onInstruments() {
        log.debug("onInstruments()");
        ApplicationManager.getInstance().showInstrumentViewer();
    }

    public void onBlotter() {
        log.debug("onBlotter()");
        ApplicationManager.getInstance().showBlotter();
    }

    public void onPreferences() {
        log.debug("onPreferences");
        ApplicationManager.getInstance().showPreferences();
    }

    public void onMarketOverview() {
        log.debug("onMarketOverview");
        ApplicationManager.getInstance().showMarketOverview();
    }


}
