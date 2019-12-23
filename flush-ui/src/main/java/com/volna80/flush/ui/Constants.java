package com.volna80.flush.ui;

/**
 * <p>
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public final class Constants {
    public static String BUNDLES_FLUSH = "bundles.flush";
    public static String FLUSH_CSS;

    static {
        FLUSH_CSS = "file:///" + (System.getProperty("user.dir").replaceAll("\\\\", "/")) + "/conf/flush.css";
    }


}
