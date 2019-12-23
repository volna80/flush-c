package com.volna80.flush.server.latency;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class LatencyRecorderFactory {

    public static ILatencyService service = LatencyService.getInstance();


    public static ILatencyRecorder getRecorder(String name) {
        return new LatencyRecorder(name, service);
    }

}
