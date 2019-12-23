package com.volna80.flush.ui.controllers;

import com.volna80.betfair.api.model.Ssoid;
import com.volna80.flush.server.FlushServer;
import com.volna80.flush.ui.ApplicationManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class LogonController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(LogonController.class);


    @FXML
    public AnchorPane root;

    private WebView browser;
    private WebEngine webEngine;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        log.debug("initialize ...");

        browser = new WebView();


        webEngine = browser.getEngine();
        webEngine.load("https://identitysso.betfair.com/view/login?product=" + FlushServer.APP_KEY + "&url=https://www.betfair.com");

        webEngine.setOnAlert(new EventHandler<WebEvent<String>>() {
            @Override
            public void handle(WebEvent<String> stringWebEvent) {
                log.debug("handle:{}", stringWebEvent);
            }
        });

        webEngine.setOnStatusChanged(new EventHandler<WebEvent<String>>() {
            @Override
            public void handle(WebEvent<String> stringWebEvent) {
                log.debug("status changed : {}", stringWebEvent);

            }
        });


        webEngine.getLoadWorker().stateProperty().addListener(
                new ChangeListener<Worker.State>() {
                    @Override
                    public void changed(ObservableValue<? extends Worker.State> observableValue, Worker.State oldState, Worker.State newState) {
                        if (newState == Worker.State.SUCCEEDED) {
                            log.debug("loaded : {}", webEngine.getLocation());


                        }
                    }
                }
        );

        webEngine.locationProperty().addListener(
                new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observableValue, String s, String s2) {
                        log.debug("location changed : {}, {}", s, s2);

                        if ("https://www.betfair.com/".equals(s2)) {
                            //done
                            String js = "" + webEngine.executeScript("document.cookie;");

                            String[] tokens = js.split(";");
                            for (String token : tokens) {
                                if (token.trim().startsWith("ssoid")) {
                                    //found a token
                                    Ssoid ssoid = new Ssoid(token.substring(7));
                                    log.info("logon successful. {}", ssoid);
                                    logonPassed(ssoid);

                                }
                            }

                        }

                    }
                }
        );

        root.getChildren().add(browser);

        //browser.autosize();


    }

    private void logonPassed(Ssoid ssoid) {
        ApplicationManager.getInstance().logonPassed(ssoid);
        ApplicationManager.getInstance().closeLogon();
    }

    public void close() {
        root.getChildren().clear();
        webEngine.getLoadWorker().cancel();
    }
}
