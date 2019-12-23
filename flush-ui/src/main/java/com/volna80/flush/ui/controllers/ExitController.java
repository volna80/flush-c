package com.volna80.flush.ui.controllers;

import com.volna80.flush.ui.ApplicationManager;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
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
public class ExitController implements Initializable, IController {

    private static Logger logger = LoggerFactory.getLogger(ExitController.class);
    private Stage stage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void init() {

    }

    public void onYes(ActionEvent event) {
        ApplicationManager.getInstance().exit("An user requests exit", false);

    }

    public void onNo(ActionEvent event) {
        stage.hide();
    }
}
