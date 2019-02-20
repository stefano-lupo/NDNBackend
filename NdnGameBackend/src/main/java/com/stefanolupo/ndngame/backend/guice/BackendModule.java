package com.stefanolupo.ndngame.backend.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import com.stefanolupo.ndngame.backend.chronosynced.DiscoveryManager;
import com.stefanolupo.ndngame.backend.chronosynced.OnPlayersDiscovered;
import com.stefanolupo.ndngame.backend.subscriber.BlockSubscriber;
import com.stefanolupo.ndngame.backend.subscriber.PlayerStatusSubscriber;
import com.stefanolupo.ndngame.config.Config;
import net.named_data.jndn.security.KeyChain;

import java.util.Arrays;
import java.util.Collection;

public class BackendModule extends AbstractModule {

    private static final Collection<Class<? extends OnPlayersDiscovered>> PLAYER_DISCOVERY_CALLBACKS = Arrays.asList(
            PlayerStatusSubscriber.class,
            BlockSubscriber.class
    );

    private final Config config;

    public BackendModule(Config config) {
        this.config = config;
    }

    @Override
    protected void configure() {
        Multibinder<OnPlayersDiscovered> onDiscoveryBinder =
                Multibinder.newSetBinder(binder(), OnPlayersDiscovered.class);
        PLAYER_DISCOVERY_CALLBACKS.forEach(pdc -> onDiscoveryBinder.addBinding().to(pdc));

        bind(DiscoveryManager.class).asEagerSingleton();
    }

    @Provides
    @Singleton
    Config providesSingletonConfig() {
        return config;
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
}
