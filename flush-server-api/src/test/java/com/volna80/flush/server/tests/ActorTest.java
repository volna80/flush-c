package com.volna80.flush.server.tests;

import akka.actor.ActorSystem;
import akka.testkit.TestKit;
import org.junit.After;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import java.util.concurrent.TimeUnit;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class ActorTest extends TestKit {

    protected static final Duration TIMEOUT = Duration.create(100, TimeUnit.MILLISECONDS);
    protected static final FiniteDuration TOUT = FiniteDuration.create(100, TimeUnit.MILLISECONDS);

    public ActorTest(String name) {
        super(ActorSystem.create("test-" + name));
    }

    @After
    public void tearDown() {
        shutdownActorSystem(system(), TIMEOUT, false);
    }


}
