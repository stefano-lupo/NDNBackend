package com.stefanolupo.ndngame.backend.statistics;

public interface HistogramFactory {
    Histogram create(Class<?> clazz);
}
