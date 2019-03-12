package com.stefanolupo.ndngame.backend.metrics;

import com.codahale.metrics.RatioGauge;

public class PercentageGauge extends RatioGauge {

    private long numerator = 0;
    private long total = 0;

    private PercentageGauge() {}

    public void hit() {
        numerator++;
        total++;
    }

    public void miss() {
        total++;
    }

    public static PercentageGauge getInstance() {
        return new PercentageGauge();
    }

    @Override
    protected Ratio getRatio() {
        return Ratio.of(numerator, total);
    }

}
