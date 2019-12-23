package com.volna80.flush.ui.server.session;

import akka.actor.OneForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.actor.UntypedActor;
import com.volna80.betfair.api.model.Ssoid;
import com.volna80.flush.server.model.exception.UnexpectedMessageException;
import com.volna80.flush.ui.server.IDictionary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import java.util.concurrent.TimeUnit;

/**
 * It creates a user session and handle any exception with user session.
 *
 * <p>
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class ClientSessionManager extends UntypedActor implements IClientSessionManager {

    public static final String NAME = "client-sessions";

    private static Logger logger = LoggerFactory.getLogger(ClientSessionManager.class);

    private final IDictionary dictionary;
    private final Ssoid ssoid;
    private final int heartInt;

    private SupervisorStrategy supervisorStrategy = new OneForOneStrategy(-1, Duration.create(5, TimeUnit.SECONDS),
            this::makeDecision);

    public ClientSessionManager(int heartInt, Ssoid ssoid, IDictionary dictionary) {
        this.heartInt = heartInt;
        this.ssoid = ssoid;
        this.dictionary = dictionary;
    }

    public static Props props(final int heartInt, final Ssoid ssoid, IDictionary dictionary) {
        return Props.create(ClientSessionManager.class, heartInt, ssoid, dictionary);
    }

    private SupervisorStrategy.Directive makeDecision(Throwable t) {
        logger.info("something happened with the session, reschedule to start new. Msg : {}", t.getMessage());
        FiniteDuration d = FiniteDuration.create(5, TimeUnit.SECONDS);
        getContext().system().scheduler().scheduleOnce(d, (Runnable) this::startSession, getContext().dispatcher());
        return SupervisorStrategy.stop();
    }

    @Override
    public void preStart() throws Exception {

        //start a client session
        startSession();

    }

    private void startSession() {
        final String sessionId = ClientSession.makeSessionId();
        logger.info("Starting a client session: {}", sessionId);
        getContext().actorOf(ClientSession.props(heartInt, sessionId, ssoid, dictionary), sessionId);
    }

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return supervisorStrategy;
    }

    @Override
    public void onReceive(Object msg) throws Exception {
        //don't expect any message
        throw new UnexpectedMessageException("Unexpected msg : " + msg);
    }
}
