package com.stefanolupo.ndngame.backend.subscriber.metrics;

import com.codahale.metrics.Histogram;
import com.stefanolupo.ndngame.backend.metrics.PercentageGauge;

public class BaseSubscriberMetrics {

    private final Histogram roundTripTime;
    private final Histogram latency;
    private final PercentageGauge percentageGauge;

    public BaseSubscriberMetrics(Histogram roundTripTime, Histogram latency, PercentageGauge percentageGauge) {
        this.roundTripTime = roundTripTime;
        this.latency = latency;
        this.percentageGauge = percentageGauge;
    }

    public Histogram getRoundTripTime() {
        return roundTripTime;
    }

    public Histogram getLatency() {
        return latency;
    }

    public PercentageGauge getPercentageGauge() {
        return percentageGauge;
    }
}
