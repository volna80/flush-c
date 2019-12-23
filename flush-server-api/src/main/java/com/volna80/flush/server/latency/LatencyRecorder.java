package com.volna80.flush.server.latency;

import com.google.common.base.Preconditions;

/**
 * Simple implementation of latency recorder;
 * <p/>
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class LatencyRecorder implements ILatencyRecorder {

    private final String name;
    private final ILatencyService service;

    private long start;

    public LatencyRecorder(String name, ILatencyService service) {
        this.name = Preconditions.checkNotNull(name);
        this.service = Preconditions.checkNotNull(service);
    }

    @Override
    public void start() {
        start = System.currentTimeMillis();
    }

    @Override
    public void start(long milliseconds) {
        this.start = milliseconds;
    }

    @Override
    public void stop() {
        final long latency = System.currentTimeMillis() - start;
        service.sample(name, latency);
    }
}
