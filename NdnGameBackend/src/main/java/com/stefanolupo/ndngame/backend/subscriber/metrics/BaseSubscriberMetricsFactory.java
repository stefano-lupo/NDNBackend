package com.stefanolupo.ndngame.backend.subscriber.metrics;

import com.codahale.metrics.MetricRegistry;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.stefanolupo.ndngame.backend.annotations.BackendMetrics;
import com.stefanolupo.ndngame.backend.metrics.PercentageGauge;
import com.stefanolupo.ndngame.names.PlayerName;

@Singleton
public class BaseSubscriberMetricsFactory {

    private final MetricRegistry metrics;

    @Inject
    public BaseSubscriberMetricsFactory(@BackendMetrics MetricRegistry metrics) {
        this.metrics = metrics;
    }

    public BaseSubscriberMetrics forNameAndType(PlayerName playerName, BaseSubscriberMetricsNames.ObjectType objectType) {
        return buildForName(BaseSubscriberMetricsNames.forNameAndType(playerName, objectType));
    }

    private BaseSubscriberMetrics buildForName(BaseSubscriberMetricsNames names) {
        return new BaseSubscriberMetrics(
                metrics.histogram(names.getRttName()),
                metrics.histogram(names.getLatencyName()),
                metrics.register(names.getCacheHitRateName(), PercentageGauge.getInstance())
        );
    }
}
