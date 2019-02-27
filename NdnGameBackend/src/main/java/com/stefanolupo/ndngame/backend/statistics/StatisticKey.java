package com.stefanolupo.ndngame.backend.statistics;

public class StatisticKey {
    private final Class<?> clazz;
    private final String key;

    public StatisticKey(Class<?> clazz, String key) {
        this.clazz = clazz;
        this.key = key;
    }

    public String getClassString() {
        return clazz.getName();
    }

    public String getKey() {
        return key;
    }

    @Override
    public String toString() {
        return String.format("%s: %s", clazz.getName(), key);
    }
}