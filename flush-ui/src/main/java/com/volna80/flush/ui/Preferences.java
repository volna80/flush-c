package com.volna80.flush.ui;

import com.volna80.flush.ui.util.ResourceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.prefs.BackingStoreException;
import java.util.stream.Collectors;

/**
 * <p>
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class Preferences {

    public static final String COUNTRY_CODE = "countries";
    public static final String EVENT_TYPES = "eventTypes";
    public static final String BUYSELL_BETSIZE_1 = "buysell.betsize.1";
    public static final String BUYSELL_BETSIZE_2 = "buysell.betsize.2";
    public static final String BUYSELL_BETSIZE_3 = "buysell.betsize.3";
    private static final String EMPTY_COUNTRIES = "";
    private static final Locale ENGLISH = Locale.ENGLISH;
    private static final Locale RUSSIAN = Locale.forLanguageTag("ru");
    private static Logger logger = LoggerFactory.getLogger(Preferences.class);
    private static java.util.prefs.Preferences prefs = java.util.prefs.Preferences.userRoot().node("com/volna80/flush");

    //initialize preferences (if it runs first time)
    public static void init(ResourceBundle resourceBundle) {

        if (prefs.get("countries", null) == null) {
            saveCountries(ResourceUtil.getCountries(resourceBundle));
        }

        if (prefs.get(EVENT_TYPES, null) == null) {
            saveEventTypes(ResourceUtil.getEventTypes(resourceBundle));
        }

    }


    public static Locale getLocale() {

        String l = prefs.get("locale", Locale.ENGLISH.getLanguage());

        if (l.equals(ENGLISH.getLanguage())) {
            return ENGLISH;
        } else if (l.equals(RUSSIAN.getLanguage())) {
            return RUSSIAN;
        }

        return ENGLISH;
    }

    public static void saveLocale(String code) {
        prefs.put("locale", code);

        try {
            prefs.flush();
        } catch (BackingStoreException e) {
            logger.error("could not store preferences", e);
        }
    }


    public static List<String> getEventTypes() {
        return Arrays.asList(prefs.get(EVENT_TYPES, "").split(",")).stream().sorted().collect(Collectors.toList());
    }

    public static void saveEventTypes(Collection<String> eventTypes) {
        logger.info("eventTypes={}", eventTypes);
        prefs.put(EVENT_TYPES, eventTypes.stream().collect(Collectors.joining(",")));
    }

    public static List<String> getCountries() {
        return Arrays.asList(prefs.get(COUNTRY_CODE, EMPTY_COUNTRIES).split(",")).stream().sorted().collect(Collectors.toList());
    }

    public static void saveCountries(Collection<String> countries) {
        logger.info("onCountries: {}", countries);
        prefs.put(COUNTRY_CODE, countries.stream().collect(Collectors.joining(",")));
    }


    public static int getBetSize1() {
        return prefs.getInt(BUYSELL_BETSIZE_1, 4);
    }

    public static void setBetSize1(int size) {
        prefs.putInt(BUYSELL_BETSIZE_1, size);
    }

    public static int getBetSize2() {
        return prefs.getInt(BUYSELL_BETSIZE_2, 8);
    }

    public static void setBetSize2(int size) {
        prefs.putInt(BUYSELL_BETSIZE_2, size);
    }

    public static int getBetSize3() {
        return prefs.getInt(BUYSELL_BETSIZE_3, 16);
    }

    public static void setBetSize3(int size) {
        prefs.putInt(BUYSELL_BETSIZE_3, size);
    }
}
