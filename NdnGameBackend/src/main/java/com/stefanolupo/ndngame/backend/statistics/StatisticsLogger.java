package com.stefanolupo.ndngame.backend.statistics;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.hubspot.liveconfig.value.Value;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Singleton
public class StatisticsLogger {

    private final ConcurrentHashMap<StatisticKey, String> statisticsMap = new ConcurrentHashMap<>();
    private final Set<HasStatistics> statisticSources = new HashSet<>();

    @Inject
    public StatisticsLogger(@Named("statistics.logger.log.rate.sec") Value<Long> logRateSec) {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(
                this::generateStatistics,
                logRateSec.get(),
                logRateSec.get(),
                TimeUnit.SECONDS
        );
    }

    public void registerSource(HasStatistics statisticsSource) {
        statisticSources.add(statisticsSource);
    }

    public void addDefaultStatistic(Class<?> clazz, String val) {
        StatisticKey statisticKey = new StatisticKey(clazz, "default");
        statisticsMap.put(statisticKey, val);
    }

    public void addStatistic(StatisticKey statisticKey, String val) {
        statisticsMap.put(statisticKey, val);
    }

    public void addStatistic(Class<?> clazz, String key, String val) {
        statisticsMap.put(new StatisticKey(clazz, key), val);
    }

    private void generateStatistics() {
        statisticSources.forEach(ss -> statisticsMap.putAll(ss.getStatistics()));
        logStatistics();
    }

    private void logStatistics() {
        System.out.println("\n--------------- Statistics ---------------");
        statisticsMap.forEach((k, v) -> {
            System.out.println(k.toString());
            System.out.println(v);
        });
        System.out.println("------------------------------------------\n");
    }


}
