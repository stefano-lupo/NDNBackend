package com.stefanolupo.ndngame.backend.statistics;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.hubspot.liveconfig.value.Value;
import com.stefanolupo.ndngame.backend.annotations.LogScheduleExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Singleton
public class StatisticsLogger {

    private static final Logger LOG = LoggerFactory.getLogger(StatisticsLogger.class);

    private final ConcurrentHashMap<StatisticKey, String> statisticsMap = new ConcurrentHashMap<>();
    private final Set<HasStatistics> statisticSources = new HashSet<>();

    @Inject
    public StatisticsLogger(@LogScheduleExecutor ScheduledExecutorService executorService,
                            @Named("statistics.logger.log.rate.sec") Value<Long> logRateSec) {
        executorService.scheduleAtFixedRate(
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
        LOG.debug("\n--------------- Statistics ---------------");
        statisticsMap.forEach((k, v) -> {
            LOG.debug("{}", k.toString());
            LOG.debug("{}", v);
        });
        System.out.println("------------------------------------------\n");
    }


}
