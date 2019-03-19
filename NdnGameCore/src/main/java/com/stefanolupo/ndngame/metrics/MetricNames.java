package com.stefanolupo.ndngame.metrics;

import com.stefanolupo.ndngame.names.BaseName;
import com.stefanolupo.ndngame.names.PlayerStatusName;
import net.named_data.jndn.Name;

public class MetricNames {

    public static String basePublisherInterestRate(Name listenName) {
        return String.format("pub-interest-rate-%s", trimAndSanitize(listenName));
    }

    public static String basePublisherUpdatePercentage(Name listenName) {
        return String.format("pub-update-percentage-%s", trimAndSanitize(listenName));
    }

    public static String playerStatusPositionDeltas(PlayerStatusName name) {
        return String.format("eng-status-delta-%s", name.getPlayerName().getName());
    }

    private static String trimAndSanitize(Name name) {
        String s = BaseName.trimBaseName(name).replace("/", "-");
        if (s.charAt(0) == '-') {
            s = s.substring(1);
        }

        return s;
    }

}
