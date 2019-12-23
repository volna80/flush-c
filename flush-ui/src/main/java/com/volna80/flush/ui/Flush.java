package com.volna80.flush.ui;

import javafx.application.Application;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;


/**
 * MIT License
 * <p>
 * Copyright (c) 2019 Nikolay Volnov
 *
 * @author nikolay.volnov@gmail.com
 */
public class Flush extends Application {

    private static Logger logger = LoggerFactory.getLogger(Flush.class);

    public static void main(String[] args) {

        Locale.setDefault(Preferences.getLocale());
        logger.info("locale = " + Locale.getDefault());
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        ApplicationManager.init();
        ApplicationManager.getInstance().start();
    }


}
