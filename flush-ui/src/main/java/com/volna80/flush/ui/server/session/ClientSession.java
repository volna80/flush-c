package com.volna80.flush.ui.server.session;

import akka.actor.*;
import com.volna80.betfair.api.model.Ssoid;
import com.volna80.flush.server.model.Heartbeat;
import com.volna80.flush.server.model.Logon;
import com.volna80.flush.server.model.Logout;
import com.volna80.flush.server.model.exception.AlreadyLogonException;
import com.volna80.flush.server.model.exception.WrongParameterValuesException;
import com.volna80.flush.server.sessions.ServerSessionManager;
import com.volna80.flush.ui.server.FlushClient;
import com.volna80.flush.ui.server.IDictionary;
import com.volna80.flush.ui.server.OrdersControllerRemote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Handshake protocol
 * <p>
 * 1) A client sends logon msg with a client heartbeat interval
 * 2) A server replies with logon msg and server heartbeat interval
 * 3) A client and a server start sending heartbeats
 * 4) 2 missed heartbeat trigger closing session scenario
 *
 * <p>
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class ClientSession extends com.volna80.flush.server.AbstractActor {

    private static Logger logger = LoggerFactory.getLogger(ClientSession.class);

    private final IDictionary dictionary;

    private final Ssoid ssoid;
    private final int heartInt;
    private final String sessionId;
    private int serverHeartInt;
    private Cancellable checkHeartbeatTask;
    private Cancellable checkLogonResponseTask;
    private Cancellable sendHeartbeatTask;
    private Status status = Status.ESTABLISHING;
    private long lastServerHeartbeat;
    private ActorRef serverSession;

    public ClientSession(int heartInt, String sessionId, Ssoid ssoid, IDictionary dictionary) {
        this.heartInt = heartInt;
        this.sessionId = sessionId;
        this.ssoid = ssoid;
        this.dictionary = dictionary;
    }

    public static String makeSessionId() {
        String sessionId = "" + UUID.randomUUID();
        try {
            sessionId = InetAddress.getLocalHost().getCanonicalHostName() + "_" + sessionId;
        } catch (UnknownHostException e) {
            //
        }
        return sessionId;
    }

    public static Props props(final int heartInt, String sessionId, Ssoid ssoid, IDictionary dictionary) {
        return Props.create(ClientSession.class, heartInt, sessionId, ssoid, dictionary);
    }

    @Override
    public void onLogon(Logon logon) throws Exception {
        logger.info("onLogon() {}", logon);

        if (status == Status.ESTABLISHING) {
            status = Status.ESTABLISHED;
            lastServerHeartbeat = System.currentTimeMillis();
            this.serverHeartInt = logon.heartbeatInt;
            this.serverSession = getSender();

            if (serverHeartInt <= 0) {
                logger.error("Incorrect server heartbeat interval. {}", logon);
                throw new WrongParameterValuesException("Server heartbeat should be bigger than zero. session[" + sessionId + "]");
            }

            checkLogonResponseTask.cancel();

            final FiniteDuration clientDuration = FiniteDuration.create(heartInt, TimeUnit.SECONDS);

            //schedule heartbeat send task
            sendHeartbeatTask = getContext().system().scheduler().schedule(
                    clientDuration,
                    clientDuration,
                    (Runnable) this::sendHeartbeat,
                    getContext().system().dispatcher()
            );

            checkHeartbeatTask = getContext().system().scheduler().schedule(
                    Duration.create(serverHeartInt, TimeUnit.SECONDS),
                    Duration.create(serverHeartInt, TimeUnit.SECONDS),
                    (Runnable) this::heartbeatCheck,
                    getContext().dispatcher());

            //init Remote Order Controller
            getContext().actorOf(OrdersControllerRemote.props(getSender(), dictionary));

        } else {
            throw new AlreadyLogonException("The session [" + sessionId + "] for " + ssoid + " is already ESTABLISHED");
        }
    }

    @Override
    public void onLogout(Logout logout) {
        logger.info("onLogout() {}", logout);
        //send logout ack
        serverSession.tell(new Logout("The session is stopped successfully.", sessionId), getSelf());

        status = Status.TERMINATING;

        //stop actor
        getContext().stop(getSelf());
    }

    @Override
    public void onHeartbeat(Heartbeat heartbeat) {
        logger.debug("onHeartbeat() : {}", heartbeat);
        lastServerHeartbeat = heartbeat.sendTime;
    }

    @Override
    public void preStart() throws Exception {

        logger.info("preStart for " + sessionId);

        final FiniteDuration duration = FiniteDuration.create(heartInt, TimeUnit.SECONDS);

        getContext().system().scheduler().scheduleOnce(
                FiniteDuration.create(0, TimeUnit.SECONDS),
                (Runnable) this::sendLogon,
                getContext().dispatcher()
        );

        //schedule timeout task
        checkLogonResponseTask = getContext().system().scheduler().scheduleOnce(
                duration,
                (Runnable) this::logonTimeout,
                getContext().dispatcher()
        );

    }

    @Override
    public void postStop() throws Exception {

        logger.info("postStop for " + sessionId);

        status = Status.TERMINATED;

        if (sendHeartbeatTask != null) sendHeartbeatTask.cancel();
        if (checkLogonResponseTask != null) checkLogonResponseTask.cancel();
        if (checkHeartbeatTask != null) checkHeartbeatTask.cancel();

    }

    public void sendLogon() {
        Logon logon = new Logon(sessionId, ssoid, heartInt);
        ActorSelection server = getServerSessionManager();
        logger.info("sending initial logon[{}] to {}", logon, server);
        //send initial logon
        server.tell(logon, getSelf());
    }


    /**
     * @return a server side connection point
     */
    private ActorSelection getServerSessionManager() {
        if (FlushClient.EMBEDDED_MODE) {
            return getContext().actorSelection("/user/" + ServerSessionManager.NAME);
        } else {
            return getContext().actorSelection(FlushClient.REMOTE_ADDRESS + "/user/" + ServerSessionManager.NAME);
        }
    }


    public void sendHeartbeat() {
        Heartbeat heartbeat = new Heartbeat(System.currentTimeMillis(), sessionId);
        logger.debug("sending {}", heartbeat);
        serverSession.tell(heartbeat, getSelf());
    }

    public void heartbeatCheck() {
        if (System.currentTimeMillis() - lastServerHeartbeat > 3 * (serverHeartInt * 1000)) {
            logger.info("missed 3 heartbeat messages. lastServerHeartbeat={}, serverHeartInt={}, {}", lastServerHeartbeat, serverHeartInt, this);
            status = Status.TERMINATING;
            getSelf().tell(Kill.getInstance(), getSelf());
        }
    }


    public void logonTimeout() {
        logger.info("have not got a response for session [{}]", sessionId);
        getSelf().tell(Kill.getInstance(), getSelf());
    }

    @Override
    public String toString() {
        return "ClientSession{" +
                "status=" + status +
                ", ssoid='" + ssoid + '\'' +
                ", sessionId='" + sessionId + '\'' +
                '}';
    }

    enum Status {
        ESTABLISHING, ESTABLISHED, TERMINATING, TERMINATED
    }

}
