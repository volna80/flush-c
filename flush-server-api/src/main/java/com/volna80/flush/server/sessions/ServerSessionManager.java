package com.volna80.flush.server.sessions;

import akka.actor.*;
import com.volna80.flush.server.AbstractActor;
import com.volna80.flush.server.model.Logon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.concurrent.duration.Duration;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class ServerSessionManager extends AbstractActor {

    public static final String NAME = "server-sessions";
    private static Logger logger = LoggerFactory.getLogger(ServerSessionManager.class);
    private Map<String, ActorRef> sessions = new HashMap<>();

    private int serverHeartInt = 5;

    private SupervisorStrategy supervisorStrategy = new OneForOneStrategy(-1, Duration.create(5, TimeUnit.SECONDS), this::makeDecision);

    public static Props props() {
        return Props.create(ServerSessionManager.class);
    }

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return supervisorStrategy;
    }

    private SupervisorStrategy.Directive makeDecision(Throwable t) {
        logger.error("Something happened. Exception : " + t.getMessage(), t);
        return SupervisorStrategy.stop();
    }

    @Override
    public void preStart() throws Exception {
    }

    @Override
    public void postStop() throws Exception {
    }

    @Override
    public void onLogon(Logon logon) throws Exception {
        logger.info("got {}", logon);
        //check if the session is exist already
        if (sessions.keySet().contains(logon.sessionId)) {
            logger.error("got a logon msg for alive session. {}", logon);
            getContext().system().stop(sessions.get(logon.sessionId));
            return;
        }

        //create new session

        ActorRef ref = getContext().actorOf(ServerSession.props(logon.sessionId, serverHeartInt, logon.ssoid), logon.sessionId);
        sessions.put(logon.sessionId, ref);

        //subscribe to status changes
        getContext().watch(ref);


        logger.info("forward {} to new created server session: {}", logon, ref);
        ref.forward(logon, getContext());
    }

    @Override
    public void onTerminated(Terminated msg) {
        logger.info("clean a reference for " + msg.actor().path().name());
        sessions.put(msg.actor().path().name(), null);
    }
}
