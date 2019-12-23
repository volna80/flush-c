package com.volna80.betfair.api.util;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class StringUtils {

    public static void removeLastChar(StringBuilder json, char c) {
        if (json.charAt(json.length() - 1) == c) {
            json.deleteCharAt(json.length() - 1);
        }
    }

}
