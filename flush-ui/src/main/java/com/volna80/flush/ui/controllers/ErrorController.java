package com.volna80.flush.ui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * <p>
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class ErrorController implements Initializable {

    private static Logger logger = LoggerFactory.getLogger(ErrorController.class);

    @FXML
    Label text;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void onOk(ActionEvent event) {
        logger.info("Exiting ...");
        System.exit(0);

    }
}
