package com.volna80.flush.server.latency;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public interface ILatencyService {

    /**
     * add a latency sample
     */
    void sample(String name, long latency);

    void shutdown();

}
