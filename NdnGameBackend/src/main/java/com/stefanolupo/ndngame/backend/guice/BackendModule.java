package com.stefanolupo.ndngame.backend.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.multibindings.Multibinder;
import com.hubspot.liveconfig.LiveConfig;
import com.hubspot.liveconfig.LiveConfigModule;
import com.stefanolupo.ndngame.backend.chronosynced.ConfigManager;
import com.stefanolupo.ndngame.backend.chronosynced.DiscoveryManager;
import com.stefanolupo.ndngame.backend.chronosynced.OnPlayersDiscovered;
import com.stefanolupo.ndngame.backend.ndn.BasePublisherFactory;
import com.stefanolupo.ndngame.backend.publisher.BasePublisher;
import com.stefanolupo.ndngame.backend.subscriber.BlockSubscriber;
import com.stefanolupo.ndngame.backend.subscriber.PlayerStatusSubscriber;
import com.stefanolupo.ndngame.config.LocalConfig;
import net.named_data.jndn.security.KeyChain;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;

public class BackendModule extends AbstractModule {

    private static final String PROPERTIES_NAME = "backend.properties";
    private static final Collection<Class<? extends OnPlayersDiscovered>> PLAYER_DISCOVERY_CALLBACKS = Arrays.asList(
            PlayerStatusSubscriber.class,
            BlockSubscriber.class
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

        Properties properties = loadInitialProperties();
        LiveConfig liveConfig = LiveConfig.builder()
                .usingProperties(properties)
                .usingResolver(new ConfigManager(localConfig, properties))
                .build();
        install(new LiveConfigModule(liveConfig));
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
