package com.volna80.betfair.api.model;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class MsgBuilder<T> implements IMsgBuilder<T> {

    private static final Logger log = LoggerFactory.getLogger(MsgBuilder.class);

    private final Class<T> clazz;
    private final Gson gson;

    public MsgBuilder(Class<T> clazz, Gson gson) {
        this.clazz = clazz;
        this.gson = gson;
    }


    @Override
    public T build(String json) {
        return gson.fromJson(json, clazz);
    }
}
