package com.stefanolupo.ndngame.backend.guice;

import com.google.inject.AbstractModule;
import com.stefanolupo.ndngame.backend.setup.CommandLineHelper;
import com.stefanolupo.ndngame.config.Config;

public class AutomatedBackendModule extends AbstractModule {

    private final Config config;

    public AutomatedBackendModule(String[] args) {
        CommandLineHelper commandLineHelper = new CommandLineHelper();
        config = commandLineHelper.getConfig(args);
    }

    @Override
    protected void configure() {
        install(new BackendModule(config));
    }
}
