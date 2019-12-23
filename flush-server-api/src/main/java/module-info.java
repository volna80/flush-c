module flush.server.api {

    requires betfair.api;

    requires org.slf4j;
    requires commons.math3;

    requires java.rmi;
    requires akka.actor;
    requires typesafe.config;
    requires scala.library;

    requires com.google.common;


    exports com.volna80.flush.server;
    exports com.volna80.flush.server.algos;
    exports com.volna80.flush.server.downstream;
    exports com.volna80.flush.server.latency;
    exports com.volna80.flush.server.model;
    exports com.volna80.flush.server.model.exception;
    exports com.volna80.flush.server.sessions;
}