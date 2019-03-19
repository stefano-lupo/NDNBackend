package com.stefanolupo.ndngame.backend.subscriber.metrics;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Histogram;

public class BaseSubscriberMetrics {

    private final Histogram roundTripTime;
    private final Counter interestsExpressedCounter;

    public BaseSubscriberMetrics(Histogram roundTripTime,
                                 Counter interestsExpressedCounter) {
        this.roundTripTime = roundTripTime;
        this.interestsExpressedCounter = interestsExpressedCounter;
    }

    public Histogram getRoundTripTime() {
        return roundTripTime;
    }

    public Counter getInterestsExpressedCounter() {
        return interestsExpressedCounter;
    }
}
