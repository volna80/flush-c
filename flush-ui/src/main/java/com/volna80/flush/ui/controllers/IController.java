package com.volna80.flush.ui.controllers;

import javafx.stage.Stage;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 * @deprecated use guice
 */
//TODO replace by guice
@Deprecated
public interface IController {

    void setStage(Stage stage);

    /**
     * Initialize a controller (if it needs a stage)
     */
    void init();
}
