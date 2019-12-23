package com.volna80.flush.server.sessions;

import akka.actor.*;
import com.volna80.betfair.api.BetfairApiFactory;
import com.volna80.betfair.api.model.Ssoid;
import com.volna80.flush.server.AbstractActor;
import com.volna80.flush.server.FlushServer;
import com.volna80.flush.server.IReferenceProvider;
import com.volna80.flush.server.algos.BasicOrder;
import com.volna80.flush.server.downstream.Downstream;
import com.volna80.flush.server.model.Heartbeat;
import com.volna80.flush.server.model.Logon;
import com.volna80.flush.server.model.Logout;
import com.volna80.flush.server.model.NewOrderSingle;
import com.volna80.flush.server.model.exception.WrongParameterValuesException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class ServerSession extends AbstractActor {

    private static final AtomicInteger reincarnation = new AtomicInteger();
    private static Logger logger = LoggerFactory.getLogger(ServerSession.class);
    private final String sessionId;
    private final int heartInt;
    private final int instanceNumber = reincarnation.getAndIncrement();
    private final Ssoid ssoid;
    private ActorRef clientSession;
    private long lastRecvTime = System.currentTimeMillis();
    private Cancellable taskSendHeartbeat;
    private Cancellable taskCheckHeartbeat;
    private int clientHeartInt;
    private IReferenceProvider referenceProvider;
    private BetfairApiFactory factory;

    private Map<String, ActorRef> downstreamByMarketId = new HashMap<>();

    private SupervisorStrategy supervisorStrategy = new OneForOneStrategy(-1, Duration.create(0, TimeUnit.SECONDS), this::makeDecision, true);

    public ServerSession(String sessionId, int heartInt, Ssoid ssoid) {
        this.sessionId = sessionId;
        this.heartInt = heartInt;
        this.ssoid = ssoid;
    }

    public static Props props(String sessionId, int heartInt, Ssoid ssoid) {
        return Props.create(ServerSession.class, sessionId, heartInt, ssoid);
    }

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return supervisorStrategy;
    }

    private SupervisorStrategy.Directive makeDecision(Throwable t) {
        logger.error("Something happened.  Exception : " + t.getMessage(), t);
        return SupervisorStrategy.escalate();
    }

    @Override
    public void preStart() throws Exception {
        logger.info("started : {}", this);

        factory = new BetfairApiFactory();
        factory.setSsoid(ssoid);
        factory.setAppId(FlushServer.APP_KEY);
        factory.setAccountUrl(BetfairApiFactory.ACCOUNT_API_UK_EXCHANGE_JSON_REST);
        factory.setBettingUrl(BetfairApiFactory.BETTING_API_UK_EXCHANGE_JSON_REST);
        factory.init();

        referenceProvider = new IReferenceProvider() {

            private final AtomicInteger counter = new AtomicInteger();
            private final String prefix = "s-" + ssoid + "-" + instanceNumber + "-";

            @Override
            public String nextRef() {
                return prefix + counter.getAndIncrement();
            }
        };

    }

    @Override
    public void postStop() throws Exception {
        taskSendHeartbeat.cancel();
        taskCheckHeartbeat.cancel();
        factory.close();
    }

    @Override
    public void onLogon(Logon logon) throws Exception {
        //send ack

        if (logon.heartbeatInt <= 0) {
            throw new WrongParameterValuesException("Incorrect heartbeat interval in a client session. " + logon);
        }

        clientHeartInt = logon.heartbeatInt;

        clientSession = getSender();
        logger.info("got a logon from " + clientSession);

        //send heartbeat task
        FiniteDuration duration = FiniteDuration.create(heartInt, TimeUnit.SECONDS);

        taskSendHeartbeat = getContext().system().scheduler().schedule(
                duration,
                duration,
                (Runnable) this::sendHeartbeat,
                getContext().dispatcher()
        );

        //ack
        getSender().tell(new Logon(sessionId, logon.ssoid, heartInt), getSelf());

        FiniteDuration clientDuration = FiniteDuration.create(logon.heartbeatInt, TimeUnit.SECONDS);

        //schedule heartbeat check
        taskCheckHeartbeat = getContext().system().scheduler().schedule(
                clientDuration,
                clientDuration,
                (Runnable) this::checkHeartbeat,
                getContext().dispatcher()

        );
    }

    @Override
    public void onNewOrderSingle(NewOrderSingle msg) throws Exception {

        //make sure if we have a downstream for a given market
        ActorRef downstream = downstreamByMarketId.get(msg.getMarketId());
        if (downstream == null) {
            //start new one if no
            downstream = getContext().actorOf(Downstream.props(factory.makeService(), msg.getMarketId(), referenceProvider), "market-" + msg.getMarketId());
            downstreamByMarketId.put(msg.getMarketId(), downstream);
        }

        ActorRef order = getContext().actorOf(BasicOrder.props(downstream), "order-" + msg.getCorrelationId());
        order.forward(msg, getContext());

    }

    public void sendHeartbeat() {
        clientSession.tell(new Heartbeat(System.currentTimeMillis(), sessionId), getSelf());
    }

    public void checkHeartbeat() {
        if (System.currentTimeMillis() - lastRecvTime > 3 * (clientHeartInt * 1000)) {
            logger.info("lost a heartbeat from a client. Last time: {}, session : {}", lastRecvTime, sessionId);
            getContext().system().stop(getSelf());
        }
    }

    @Override
    public void onLogout(Logout logout) throws Exception {
        logger.info("onLogout() : {} ", logout);
        getContext().system().stop(getSelf());
    }

    @Override
    public void onHeartbeat(Heartbeat heartbeat) throws Exception {
        logger.debug("onHeartbeat() : {}", heartbeat);
        lastRecvTime = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "ServerSession{" +
                "ssoid=" + ssoid +
                ", instanceNumber=" + instanceNumber +
                ", sessionId='" + sessionId + '\'' +
                '}';
    }
}
