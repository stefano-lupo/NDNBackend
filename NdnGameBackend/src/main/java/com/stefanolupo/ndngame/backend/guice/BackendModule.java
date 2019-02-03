package com.stefanolupo.ndngame.backend.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.stefanolupo.ndngame.config.Config;
import net.named_data.jndn.security.KeyChain;

public class BackendModule extends AbstractModule {

    private final Config config;

    public BackendModule(Config config) {
        this.config = config;
    }

    @Override
    protected void configure() {
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
