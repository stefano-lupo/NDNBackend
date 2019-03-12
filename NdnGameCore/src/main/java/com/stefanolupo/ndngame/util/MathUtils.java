package com.stefanolupo.ndngame.util;

import com.stefanolupo.ndngame.protos.GameObject;

public class MathUtils {
    public static final long MICRO_SECONDS_PER_SEC = (long) 1e6;

    public static double distanceBetween(GameObject object1, GameObject object2) {
        return distanceBetween(object1.getX(), object1.getY(), object2.getX(), object2.getY());
    }

    public static double distanceBetween(float x1, float y1, float x2, float y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y2 - y1, 2));
    }
}
