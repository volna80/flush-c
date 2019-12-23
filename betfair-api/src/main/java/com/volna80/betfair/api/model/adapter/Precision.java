package com.volna80.betfair.api.model.adapter;

/**
 * All calculation are done in longs. This class has methods to convert from/to doubles
 * <p/>
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class Precision {

    public static final int SCALE = 2;
    public static final int SCALE2 = SCALE * SCALE;

    public static final long MULTI = Math.round(Math.pow(10, SCALE));
    public static final double PRECISION = 0.0001;


    public static int toInternal(double d) {
        return (int) Math.round(d * MULTI);       //TODO
    }

    /**
     * convert an double string 1.01 to internal int representation (101)
     *
     * @param value double string
     * @return internal int representation
     */
    public static int toInternal(String value) {
        try {
            double qty = Double.valueOf(value);
            return toInternal(qty);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * convert an internal int representation (101) to a UI representation (1.01)
     *
     * @param value internal container value
     * @return external value
     */

    public static String toUI(int value) {
        return toUI(value, SCALE);
    }

    public static String toUI(int value, int scale) {
        double tmp = round(toExternal(value), scale);
        return scale > 0 ? tmp + "" : (int) tmp + "";
    }

    public static double toExternal(int l) {
        return (double) l / MULTI;         //TODO
    }


    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public static double round(double value) {
        return round(value, SCALE);
    }

    public static boolean isZero(double value) {
        return Math.abs(value) < PRECISION;
    }
}
