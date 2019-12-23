package com.volna80.flush.server.latency;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

import java.util.concurrent.ScheduledExecutorService;

import static org.mockito.Mockito.*;

/**
 * <p>
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class LatencyServiceTest {

    private LatencyService service;
    private Logger logger;

    @Before
    public void setup() {

        ScheduledExecutorService executor = mock(ScheduledExecutorService.class);
        logger = mock(Logger.class);


        service = new LatencyService(executor, logger);
        service.init();
    }

    @After
    public void tearDown() {
        service.shutdown();
    }

    @Test
    public void test() {


        service.sample("test", 10);
        service.sample("test", 20);
        service.sample("test", 30);

        service.run();

        verify(logger, times(1)).info("latency service has been started");
        verify(logger, times(1)).info("{} = mean: {} ms, std: {} ms, count: {}", "test", 20., 10., 3l);

    }

}
