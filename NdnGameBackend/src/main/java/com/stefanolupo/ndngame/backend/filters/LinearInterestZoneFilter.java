package com.stefanolupo.ndngame.backend.filters;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.hubspot.liveconfig.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LinearInterestZoneFilter {
    private static final Logger LOG = LoggerFactory.getLogger(LinearInterestZoneFilter.class);

    private final Value<Float> innerRadius;
    private final Value<Float> outerRadius;

    @Inject
    public LinearInterestZoneFilter(@Named("linear.interest.zone.filter.inner.radius")Value<Float> innerRadius,
                                    @Named("linear.interest.zone.filter.outer.radius")Value<Float> outerRadius) {
        this.innerRadius = innerRadius;
        this.outerRadius = outerRadius;
    }

    /**
     * Gets distance between two entities as factor of outer radius - inner radius
     * Returns 1 if entirely outside outer radius
     * Returns 0 if entirely inside inner radius
     * Linear interpolates between those
     */
    public double getSleepTimeFactor(float myX, float myY, float otherX, float otherY) {
        double distance = distanceBetweenPoints(myX, myY, otherX, otherY);

        float innerRadiusVal = innerRadius.get();
        float outerRadiusVal = outerRadius.get();

        LOG.debug("Using radii: {}, {}", innerRadiusVal, outerRadiusVal);

        if (distance < innerRadiusVal) return 0;
        if (distance > outerRadiusVal) return 1;

        double rangeDistanceFactor = (distance - innerRadiusVal) / (outerRadiusVal - innerRadiusVal);
        return 1 - rangeDistanceFactor;
    }

    private static double distanceBetweenPoints(float x1, float y1, float x2, float y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

}
