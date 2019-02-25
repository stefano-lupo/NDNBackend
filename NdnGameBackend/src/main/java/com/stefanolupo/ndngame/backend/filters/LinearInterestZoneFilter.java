package com.stefanolupo.ndngame.backend.filters;

public class LinearInterestZoneFilter {

    private static final float INNER_CRITICAL_RADIUS = 10;
    private static final float OUTER_CRITICAL_RADIUS = 20;

    /**
     * Gets distance between two entities as factor of outer radius - inner radius
     * Returns 1 if entirely outside outer radius
     * Returns 0 if entirely inside inner radius
     * Linear interpolates between those
     */
    public static double getSleepTimeFactor(float myX, float myY, float otherX, float otherY) {
        double distance = distanceBetweenPoints(myX, myY, otherX, otherY);
        if (distance < INNER_CRITICAL_RADIUS) return 0;
        if (distance > OUTER_CRITICAL_RADIUS) return 1;

        double rangeDistanceFactor = (distance - INNER_CRITICAL_RADIUS) / (OUTER_CRITICAL_RADIUS - INNER_CRITICAL_RADIUS);
        return 1 - rangeDistanceFactor;
    }

    private static double distanceBetweenPoints(float x1, float y1, float x2, float y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

}
