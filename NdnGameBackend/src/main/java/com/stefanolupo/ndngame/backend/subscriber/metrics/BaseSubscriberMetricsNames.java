package com.stefanolupo.ndngame.backend.subscriber.metrics;

import com.stefanolupo.ndngame.names.PlayerName;

public class BaseSubscriberMetricsNames {

    // sub-object-metric-playerName
    private static final String FORMAT_STRING = "sub-%s-%s-%s";

    public enum ObjectType {
        STATUS("status"),
        PROJECTILES("projectiles"),
        BLOCKS("blocks");

        private String name;


        ObjectType(String name) {
            this.name = name;
        }
    }

    public enum MetricType {
        RTT("rtt"),
        INTERESTS_EXPRESSED_COUNTER("interestscounter");

        private String name;

        MetricType(String name) {
            this.name = name;
        }
    }

    private String rttName;
    private String interestExpressedCounterName;

    private BaseSubscriberMetricsNames() {}

    public static BaseSubscriberMetricsNames forNameAndType(PlayerName playerName, ObjectType objectType) {
        BaseSubscriberMetricsNames names = new BaseSubscriberMetricsNames();
        names.rttName = String.format(FORMAT_STRING, objectType.name, MetricType.RTT.name, playerName.getName());
        names.interestExpressedCounterName = String.format(FORMAT_STRING, objectType.name, MetricType.INTERESTS_EXPRESSED_COUNTER.name, playerName.getName());
        return names;
    }

    public String getRttName() {
        return rttName;
    }

    public String getInterestExpressedCounterName() {
        return interestExpressedCounterName;
    }
}
