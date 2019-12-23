package com.volna80.betfair.api.util;

import com.volna80.betfair.api.impl.BettingAPI;
import com.volna80.betfair.api.model.JsonMessage;
import com.volna80.betfair.api.model.TimeRange;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class JsonBuilder {

    private static final ThreadLocal<SimpleDateFormat> dateFormat = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat(BettingAPI.DATE_FORMAT);
        }
    };

    public static void append(StringBuilder builder, String name, Boolean value) {
        if (value != null) {
            builder.append("\"").append(name).append("\":").append(value).append(",");
        }
    }

    public static void append(StringBuilder builder, String name, Set<String> values) {
        if (values != null && values.size() > 0) {
            builder.append("\"").append(name).append("\":[");
            Iterator<String> i = values.iterator();
            while (true) {
                builder.append("\"").append(i.next()).append("\"");
                if (i.hasNext()) {
                    builder.append(",");
                } else {
                    break;
                }
            }
            builder.append("],");
        }
    }

    public static void append(StringBuilder request, String name, int value) {
        request.append('"').append(name).append("\":").append(value).append(',');
    }

    public static void append(StringBuilder request, String name, Collection value) {
        if (value != null) {
            request.append('"').append(name).append("\":").append(toJson(value)).append(",");
        }
    }

    public static void appendMessages(StringBuilder request, String name, Collection<? extends JsonMessage> value) {
        if (value != null) {
            request.append('"').append(name).append("\":").append(toJsonMessages(value)).append(",");
        }
    }

    public static void append(StringBuilder request, String name, Object value) {
        if (value != null) {
            request.append('"').append(name).append("\":\"").append(value).append("\",");
        }
    }

    public static void append(StringBuilder request, String name, Long value) {
        if (value != null) {
            request.append('"').append(name).append("\":").append(value).append(",");
        }
    }

    public static void append(StringBuilder request, String name, Double value) {
        if (value != null && (!value.isNaN())) {
            request.append('"').append(name).append("\":").append(value).append(",");
        }
    }

    public static void append(StringBuilder request, String name, JsonMessage value) {
        if (value != null) {
            request.append('"').append(name).append("\":").append(value.toJson()).append(",");
        }
    }

    public static void append(StringBuilder builder, String name, TimeRange value) {

        if (value != null) {
            builder.append("\"").append(name)
                    .append("\":{\"from\":\"").append(dateFormat.get().format(value.getFrom())).append("\", \"to\":\"").append(dateFormat.get().format(value.getTo())).append("\"},");
        }
    }


    public static String toJson(Collection values) {
        StringBuilder str = new StringBuilder();
        str.append('[');
        Iterator i = values.iterator();
        boolean hasNext = i.hasNext();
        while (hasNext) {
            str.append('"').append(i.next()).append('"');
            if (hasNext = i.hasNext()) {
                str.append(',');
            }
        }
        str.append(']');
        return str.toString();
    }

    public static String toJsonMessages(Collection<? extends JsonMessage> values) {
        StringBuilder str = new StringBuilder();
        str.append('[');
        Iterator<? extends JsonMessage> i = values.iterator();
        boolean hasNext = i.hasNext();
        while (hasNext) {
            str.append(i.next().toJson());
            if (hasNext = i.hasNext()) {
                str.append(',');
            }
        }
        str.append(']');
        return str.toString();
    }


}
