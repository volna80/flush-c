package com.volna80.flush.server.latency;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.volna80.betfair.api.model.adapter.Precision.round;


/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class LatencyService implements ILatencyService, Runnable {

    private final static LatencyService instance;
    private final static AtomicInteger c = new AtomicInteger(0);
    //max number of samples for a single latency type
    public static int MAX_NUMBER = 1000;
    //time interval for calculation of avg latencies
    public static long AGGREGATION_INTERVAL = 60 * 1000;

    static {

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(
                r -> {
                    if (r != null) {
                        return new Thread(r, "latency-" + c.getAndIncrement());
                    }
                    return new Thread("latency-" + c.getAndIncrement());
                }
        );

        LatencyService service = new LatencyService(executor, LoggerFactory.getLogger(LatencyService.class));
        service.init();
        instance = service;
    }

    private final Logger log;
    private final ScheduledExecutorService executor;
    private Map<String, ArrayBlockingQueue<Long>> samples = new ConcurrentHashMap<>();

    public LatencyService(ScheduledExecutorService executor, Logger log) {
        this.executor = executor;
        this.log = log;
    }

    public static ILatencyService getInstance() {
        return instance;
    }

    public void init() {
        executor.scheduleWithFixedDelay(this, AGGREGATION_INTERVAL, AGGREGATION_INTERVAL, TimeUnit.MILLISECONDS);
        log.info("latency service has been started");
    }

    @Override
    public void sample(String name, long latency) {

        ArrayBlockingQueue<Long> queue = samples.get(name);
        if (queue == null) {
            samples.put(name, new ArrayBlockingQueue<>(MAX_NUMBER));
            queue = samples.get(name);
        }
        queue.offer(latency);

    }

    @Override
    public void shutdown() {
        executor.shutdown();
    }

    @Override
    public void run() {

        for (String name : samples.keySet()) {

            List<Long> data = new ArrayList<>();
            samples.get(name).drainTo(data);

            SummaryStatistics stats = new SummaryStatistics();
            for (long s : data) {
                stats.addValue(s);
            }

            log.info("{} = mean: {} ms, std: {} ms, count: {}", name, round(stats.getMean(), 2), round(stats.getStandardDeviation(), 2), stats.getN());
        }

    }

}
