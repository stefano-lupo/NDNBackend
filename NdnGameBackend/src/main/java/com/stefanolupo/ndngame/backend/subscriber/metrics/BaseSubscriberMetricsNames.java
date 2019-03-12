package com.stefanolupo.ndngame.backend.subscriber.metrics;

import com.stefanolupo.ndngame.names.PlayerName;

public class BaseSubscriberMetricsNames {

    // sub-object-metric-playerName
    private static final String FORMAT_STRING = "sub-%s-%s-%s";

    public enum ObjectType {
        STATUS("status"),
        PROJECTILE("projectile"),
        BLOCK("block");

        private String name;


        ObjectType(String name) {
            this.name = name;
        }
    }

    public enum MetricType {
        LATENCY("latency"),
        RTT("rtt"),
        CACHE_RATE("cacherate");

        private String name;

        MetricType(String name) {
            this.name = name;
        }
    }

    private String rttName;
    private String latencyName;
    private String cacheHitRateName;

    private BaseSubscriberMetricsNames() {}

    public static BaseSubscriberMetricsNames forNameAndType(PlayerName playerName, ObjectType objectType) {
        BaseSubscriberMetricsNames names = new BaseSubscriberMetricsNames();
        names.rttName = String.format(FORMAT_STRING, objectType.name, MetricType.RTT.name, playerName.getName());
        names.latencyName = String.format(FORMAT_STRING, objectType.name, MetricType.LATENCY.name, playerName.getName());
        names.cacheHitRateName = String.format(FORMAT_STRING, objectType.name, MetricType.CACHE_RATE.name, playerName.getName());

        return names;
    }

    public String getRttName() {
        return rttName;
    }

    public String getLatencyName() {
        return latencyName;
    }

    public String getCacheHitRateName() {
        return cacheHitRateName;
    }
}
