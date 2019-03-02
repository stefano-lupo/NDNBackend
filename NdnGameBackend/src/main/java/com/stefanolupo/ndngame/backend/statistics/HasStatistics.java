package com.stefanolupo.ndngame.backend.statistics;

import java.util.Map;

public interface HasStatistics {
    Map<StatisticKey, String> getStatistics();
}
