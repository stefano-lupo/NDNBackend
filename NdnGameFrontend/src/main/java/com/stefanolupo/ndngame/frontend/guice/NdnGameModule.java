package com.stefanolupo.ndngame.frontend.guice;

import com.google.inject.AbstractModule;
import com.stefanolupo.ndngame.backend.guice.BackendModule;
import com.stefanolupo.ndngame.config.Config;

public class NdnGameModule extends AbstractModule {

    private Config config;

    public NdnGameModule(Config config) {
        this.config = config;
    }

    @Override
    protected void configure() {
        install(new BackendModule(config));
    }
}
