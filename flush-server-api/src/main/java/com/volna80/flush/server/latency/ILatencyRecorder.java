package com.volna80.flush.server.latency;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public interface ILatencyRecorder {

    void start();

    void start(long milliseconds);

    void stop();

}
