package com.stefanolupo.ndngame.libgdx.guice;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.stefanolupo.ndngame.backend.guice.BackendModule;
import com.stefanolupo.ndngame.config.Config;
import com.stefanolupo.ndngame.libgdx.inputcontrollers.AutomatedInputController;
import com.stefanolupo.ndngame.libgdx.inputcontrollers.InputController;
import com.stefanolupo.ndngame.libgdx.inputcontrollers.RealInputController;

public class LibGdxGameModule extends AbstractModule {

    private static final Vector2 WORLD_GRAVITY = new Vector2(0, 0);

    private final Config config;

    public LibGdxGameModule(Config config) {
        this.config = config;
    }

    @Override
    protected void configure() {
        install(new BackendModule(config));

    }

    @Provides
    @Singleton
    World providesGameWorld() {
        return new World(WORLD_GRAVITY, true);
    }

    @Provides
    @Singleton
    InputController providesInputController() {
        return config.isAutomated() ?
                new AutomatedInputController() :
                new RealInputController();
    }

    @Provides
    @Singleton
    PooledEngine providesSingletonEngine() {
        return new PooledEngine();
    }
}
