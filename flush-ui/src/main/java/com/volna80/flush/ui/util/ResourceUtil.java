package com.volna80.flush.ui.util;

import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * <p>
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class ResourceUtil {

    public static List<String> getCountries(ResourceBundle resources) {
        return get(resources, "country.");
    }

    public static List<String> getEventTypes(ResourceBundle resources) {
        return get(resources, "event-type.");
    }

    public static List<String> getMarketTypes(ResourceBundle resources) {
        return get(resources, "market-type.");
    }

    private static List<String> get(ResourceBundle resources, String prefix) {
        return resources.keySet().stream().filter(v -> v.startsWith(prefix))
                .map(v -> v.substring(prefix.length())).collect(Collectors.toList());
    }

}
