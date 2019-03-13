package com.stefanolupo.ndngame.backend.subscriber.metrics;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Histogram;
import com.stefanolupo.ndngame.backend.metrics.PercentageGauge;

public class BaseSubscriberMetrics {

    private final Histogram roundTripTime;
    private final Histogram latency;
    private final PercentageGauge percentageGauge;
    private final Counter interestsExpressedCounter;

    public BaseSubscriberMetrics(Histogram roundTripTime,
                                 Histogram latency,
                                 PercentageGauge percentageGauge,
                                 Counter interestsExpressedCounter) {
        this.roundTripTime = roundTripTime;
        this.latency = latency;
        this.percentageGauge = percentageGauge;
        this.interestsExpressedCounter = interestsExpressedCounter;
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

    public Counter getInterestsExpressedCounter() {
        return interestsExpressedCounter;
    }
}
