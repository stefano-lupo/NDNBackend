package com.stefanolupo.ndngame.libgdx.guice;

import com.google.inject.AbstractModule;
import com.stefanolupo.ndngame.backend.guice.BackendModule;
import com.stefanolupo.ndngame.config.Config;
import com.stefanolupo.ndngame.libgdx.LibGdxGame;

public class LibGdxGameModule extends AbstractModule {

    private final Config config;

    public LibGdxGameModule(Config config) {
        this.config = config;
    }

    @Override
    protected void configure() {
        install(new BackendModule(config));
    }
}
