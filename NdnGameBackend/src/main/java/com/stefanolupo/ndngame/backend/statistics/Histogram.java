package com.stefanolupo.ndngame.backend.statistics;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import java.util.HashMap;
import java.util.Map;

public class Histogram implements HasStatistics {

    private final int[] HISTOGRAM = {0, 0, 0, 0, 0};
    private static final int HISTOGRAM_BIN_SIZE_MS = 20;

    private final Map<StatisticKey, String> statisticsMap = new HashMap<>();
    private final StatisticKey statisticKey;

    private final Class<?> clazz;
    private final StatisticsLogger statisticsLogger;

    @Inject
    public Histogram(StatisticsLogger statisticsLogger,
                     @Assisted Class<?> clazz) {
        this.clazz = clazz;
        this.statisticsLogger = statisticsLogger;
        statisticsLogger.registerSource(this);

        statisticKey = new StatisticKey(clazz, "Histogram");
        statisticsMap.put(statisticKey, "No data gathered");
    }

    public void addDatapoint(long value) {
        long bin = value / HISTOGRAM_BIN_SIZE_MS;
        updateHistogram(bin);
    }

    @Override
    public Map<StatisticKey, String> getStatistics() {
        statisticsMap.put(statisticKey, generateString());
        return statisticsMap;
    }

    private String generateString() {
        StringBuilder stringBuilder = new StringBuilder(HISTOGRAM.length);
        String format = "[%d - %d]: %d\t\t";
        for (int i=0; i<HISTOGRAM.length; i++) {
            stringBuilder.append(String.format(format, i*HISTOGRAM_BIN_SIZE_MS, (i+1)*HISTOGRAM_BIN_SIZE_MS, HISTOGRAM[i]));
        }

        return stringBuilder.toString();
    }


    private void updateHistogram(long bin) {
        int safeBin = (int) Math.max(0, Math.min(bin, HISTOGRAM.length - 1));
        HISTOGRAM[safeBin] = ++HISTOGRAM[safeBin];
    }
}
