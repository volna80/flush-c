package com.volna80.flush.ui.server;

import akka.actor.ActorSystem;
import com.typesafe.config.Config;
import com.volna80.betfair.api.model.Ssoid;
import com.volna80.flush.server.FlushServer;
import com.volna80.flush.ui.server.session.ClientSessionManager;

/**
 * <p>
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class FlushClient {

    private static final Config config = FlushServer.config;
    public static final boolean EMBEDDED_MODE = config.getBoolean("flush.server.embedded");
    public static final int HEART_INT = config.getInt("flush.client.heartbeatInSec");
    public static final String REMOTE_ADDRESS = config.getString("flush.server.address");
    private static final ActorSystem akka = FlushServer.akka;
    private final Ssoid ssoid;
    private final IDictionary dictionary;

    public FlushClient(Ssoid ssoid, IDictionary dictionary) {
        this.ssoid = ssoid;
        this.dictionary = dictionary;
    }

    public void start() {

        if (EMBEDDED_MODE) {
            FlushServer server = new FlushServer();
            server.start();
        }

        akka.actorOf(ClientSessionManager.props(HEART_INT, ssoid, dictionary), ClientSessionManager.NAME);

    }


    public void stop() {
        akka.terminate();
    }
}
