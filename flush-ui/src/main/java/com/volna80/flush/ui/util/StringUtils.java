package com.volna80.flush.ui.util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class StringUtils {


    public static String convertTimestamp(long timestamp) {
        return convertTimestamp(new Date(timestamp));
    }

    public static String convertTimestamp(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }

    public static String convertTime(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.format(date.toInstant().atZone(ZoneId.systemDefault()));
    }


    public static String formatMoney(double value) {
        DecimalFormat format = new DecimalFormat("###,###,###");
        return format.format((long) value);
    }
}
