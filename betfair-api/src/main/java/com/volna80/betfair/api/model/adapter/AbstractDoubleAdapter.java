package com.volna80.betfair.api.model.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * <p>
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public abstract class AbstractDoubleAdapter extends TypeAdapter<Integer> {

    abstract String getName();

    @Override
    public void write(JsonWriter out, Integer value) throws IOException {
        out.name(getName());
        out.value(Precision.toExternal(value));
    }

    @Override
    public Integer read(JsonReader in) throws IOException {
        double value = in.nextDouble();
        return Precision.toInternal(value);
    }
}
