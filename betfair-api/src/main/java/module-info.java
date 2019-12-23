module betfair.api {

    //requires javax.ws.rs.api;
    requires java.ws.rs;
    requires java.xml.bind;

    requires json.java;
    requires jersey.client;
    requires org.slf4j;
    requires gson;
    requires com.google.common;

    opens com.volna80.betfair.api.model to gson;

    exports com.volna80.betfair.api;
    exports com.volna80.betfair.api.impl;
    exports com.volna80.betfair.api.model;
    exports com.volna80.betfair.api.model.adapter;
    exports com.volna80.betfair.api.model.errors;
    exports com.volna80.betfair.api.util;
}