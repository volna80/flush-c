module flush.ui {

    exports com.volna80.flush.ui to javafx.graphics;
    exports com.volna80.flush.ui.controllers to javafx.fxml, com.google.guice, com.google.common;
    exports com.volna80.flush.ui.server to akka.actor;
    exports com.volna80.flush.ui.server.session to akka.actor;

    opens com.volna80.flush.ui.controllers to com.google.guice, javafx.fxml;

    requires flush.server.api;
    requires betfair.api;

    requires org.slf4j;
    requires com.google.guice;
    requires com.google.common;


    requires typesafe.config;
    requires akka.actor;
    requires scala.library;

    requires java.prefs;
    requires java.sql;

    //requires javax.inject;

    requires javafx.graphics;
    requires javafx.web;
    requires javafx.controls;
    requires javafx.fxml;

}