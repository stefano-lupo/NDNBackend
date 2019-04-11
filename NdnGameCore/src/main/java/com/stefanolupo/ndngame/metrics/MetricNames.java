package com.stefanolupo.ndngame.metrics;

import com.stefanolupo.ndngame.names.BaseName;
import com.stefanolupo.ndngame.names.PlayerStatusName;
import net.named_data.jndn.Name;

public class MetricNames {

    public enum DeadReckoningCounters {
        NULL,
        VELOCITY,
        THRESHOLD,
        SKIP
    }

    public enum PacketSizeType {
        STATUS,
        BLOCK,
        PROJECTILE
    }

    public static String basePublisherInterestRate(Name listenName) {
        return String.format("pub-interest-rate-%s", trimAndSanitize(listenName));
    }

    public static String basePublisherUpdatePercentage(Name listenName) {
        return String.format("pub-update-percentage-%s", trimAndSanitize(listenName));
    }

    public static String playerStatusPositionDeltas(PlayerStatusName name) {
        return String.format("eng-status-delta-%s", name.getPlayerName().getName());
    }

    public static String deadReckoningCounter(DeadReckoningCounters counter) {
        return String.format("dr-counter-%s", counter.name().toLowerCase());
    }

    public static String packetSizeHistogram(PacketSizeType packetSizeType) {
        return String.format("packet-size-%s", packetSizeType.name().toLowerCase());
    }

    private static String trimAndSanitize(Name name) {
        String s = BaseName.trimBaseName(name).replace("/", "-");
        if (s.charAt(0) == '-') {
            s = s.substring(1);
        }

        return s;
    }

}
