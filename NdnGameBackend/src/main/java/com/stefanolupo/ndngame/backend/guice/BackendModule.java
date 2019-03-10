package com.stefanolupo.ndngame.backend.guice;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.CsvReporter;
import com.codahale.metrics.MetricRegistry;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.multibindings.Multibinder;
import com.hubspot.liveconfig.LiveConfig;
import com.hubspot.liveconfig.LiveConfigModule;
import com.stefanolupo.ndngame.backend.annotations.BackendMetrics;
import com.stefanolupo.ndngame.backend.annotations.LogScheduleExecutor;
import com.stefanolupo.ndngame.backend.chronosynced.ConfigManager;
import com.stefanolupo.ndngame.backend.chronosynced.DiscoveryManager;
import com.stefanolupo.ndngame.backend.chronosynced.OnPlayersDiscovered;
import com.stefanolupo.ndngame.backend.ndn.BasePublisherFactory;
import com.stefanolupo.ndngame.backend.publisher.BasePublisher;
import com.stefanolupo.ndngame.backend.statistics.Histogram;
import com.stefanolupo.ndngame.backend.statistics.HistogramFactory;
import com.stefanolupo.ndngame.backend.subscriber.BlockSubscriber;
import com.stefanolupo.ndngame.backend.subscriber.PlayerStatusSubscriber;
import com.stefanolupo.ndngame.backend.subscriber.ProjectileSubscriber;
import com.stefanolupo.ndngame.config.LocalConfig;
import net.named_data.jndn.security.KeyChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class BackendModule extends AbstractModule {

    private static final Logger LOG = LoggerFactory.getLogger(BackendModule.class);

    private static final String PROPERTIES_NAME = "backend.properties";
    private static final String METRICS_DIR_ENV_NAME = "METRICS_DIR";
    private static final Integer METRIC_LOG_RATE_INTERVAL_SEC = 10;
    private static final Collection<Class<? extends OnPlayersDiscovered>> PLAYER_DISCOVERY_CALLBACKS = Arrays.asList(
            PlayerStatusSubscriber.class,
            BlockSubscriber.class,
            ProjectileSubscriber.class
    );

    private final LocalConfig localConfig;

    public BackendModule(LocalConfig localConfig) {
        this.localConfig = localConfig;
    }

    @Override
    protected void configure() {
        Multibinder<OnPlayersDiscovered> onDiscoveryBinder =
                Multibinder.newSetBinder(binder(), OnPlayersDiscovered.class);
        PLAYER_DISCOVERY_CALLBACKS.forEach(pdc -> onDiscoveryBinder.addBinding().to(pdc));

        bind(DiscoveryManager.class).asEagerSingleton();

        install(new FactoryModuleBuilder()
                .implement(BasePublisher.class, BasePublisher.class)
                .build(BasePublisherFactory.class));

        install(new FactoryModuleBuilder()
                .implement(Histogram.class, Histogram.class)
                .build(HistogramFactory.class));

        Properties properties = loadInitialProperties();
        LiveConfig liveConfig = LiveConfig.builder()
                .usingProperties(properties)
                .usingResolver(new ConfigManager(localConfig, properties))
                .build();
        install(new LiveConfigModule(liveConfig));
    }

    @Provides
    @Singleton
    @BackendMetrics
    MetricRegistry providesMetricRegistery(Injector injector) {
        MetricRegistry metricRegistry = new MetricRegistry();
        ConsoleReporter reporter = ConsoleReporter.forRegistry(metricRegistry)
                .convertRatesTo(TimeUnit.MILLISECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
        reporter.start(METRIC_LOG_RATE_INTERVAL_SEC, METRIC_LOG_RATE_INTERVAL_SEC, TimeUnit.SECONDS);
        String metricsDir = System.getenv(METRICS_DIR_ENV_NAME);
        if (metricsDir == null) {
            LOG.warn("Environment var {} was null, not writing metrics to file", METRICS_DIR_ENV_NAME);
        } else {
            String namedMetricDir = String.format("%s/%s", metricsDir, localConfig.getPlayerName());
            LOG.debug("Writing metrics to {}", namedMetricDir);
            File directory = new File(namedMetricDir);
            if (!directory.exists()){
                directory.mkdirs();
                LOG.debug("{} didn't exist and was created", namedMetricDir);
            }
            CsvReporter csvReporter = CsvReporter.forRegistry(metricRegistry)
                    .convertRatesTo(TimeUnit.MILLISECONDS)
                    .convertDurationsTo(TimeUnit.MILLISECONDS)
                    .build(directory);
            csvReporter.start(METRIC_LOG_RATE_INTERVAL_SEC, METRIC_LOG_RATE_INTERVAL_SEC, TimeUnit.SECONDS);
        }
        return metricRegistry;
    }

    @Provides
    @Singleton
    LocalConfig providesSingletonConfig() {
        return localConfig;
    }

    @Provides
    @Singleton
    KeyChain providesSingletonKeyChain() {
        try {
            return new KeyChain();
        } catch (Exception e) {
            throw new RuntimeException("Unable to obtain keychain reference", e);
        }
    }

    @Provides
    @Singleton
    @LogScheduleExecutor
    ScheduledExecutorService providesScheduledExecutorService() {
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
                .setNameFormat("log-schedule-%d").build();
        return Executors.newScheduledThreadPool(1, namedThreadFactory);
    }


    private Properties loadInitialProperties() {
        Properties properties = new Properties();
        try {
            InputStream inputStream = BackendModule.class.getClassLoader().getResourceAsStream(PROPERTIES_NAME);
            properties.load(inputStream);
            return properties;
        }
        catch (NullPointerException e) {
            throw new RuntimeException(String.format("Could not find properties: %s", PROPERTIES_NAME), e);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
