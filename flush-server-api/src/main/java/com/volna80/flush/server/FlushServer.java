package com.volna80.flush.server;

import akka.actor.ActorSystem;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.volna80.flush.server.sessions.ServerSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Init and start all server-side services
 *
 * <p>
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class FlushServer {

    public static final Config config;
    public static final ActorSystem akka;
    public static final String APP_KEY;
    private static Logger logger = LoggerFactory.getLogger(FlushServer.class);

    static {
        File confFile = new File("./conf/application.conf");
        if (!confFile.exists()) {
            logger.error("could not find an application configuration: " + confFile);
            System.exit(-1);
        }
        config = ConfigFactory.parseFile(confFile);
        String key =  System.getProperty("flush.appKey");
        if(key == null){
            key = config.getString("flush.appKey");
        }
        APP_KEY = key;
        akka = ActorSystem.create("flush", config);
    }

    public void start() {
        logger.info("starting {}", akka);
        akka.actorOf(ServerSessionManager.props(), ServerSessionManager.NAME);
        logger.info("started ...");
    }

}
