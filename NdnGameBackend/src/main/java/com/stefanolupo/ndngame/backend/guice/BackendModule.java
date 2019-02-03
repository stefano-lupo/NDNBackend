package com.stefanolupo.ndngame.backend.guice;

import com.google.inject.AbstractModule;
import com.stefanolupo.ndngame.config.Config;

public class BackendModule extends AbstractModule {

    private final Config config;

    public BackendModule(Config config) {
        this.config = config;
    }

    @Override
    protected void configure() {
        bind(Config.class).toInstance(config);
    }
}
