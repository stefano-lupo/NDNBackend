package com.stefanolupo.ndngame.libgdx.guice;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import com.hubspot.liveconfig.LiveConfig;
import com.hubspot.liveconfig.LiveConfigModule;
import com.stefanolupo.ndngame.backend.chronosynced.OnPlayersDiscovered;
import com.stefanolupo.ndngame.backend.guice.BackendModule;
import com.stefanolupo.ndngame.config.Config;
import com.stefanolupo.ndngame.libgdx.EntityCreator;
import com.stefanolupo.ndngame.libgdx.inputcontrollers.AutomatedInputController;
import com.stefanolupo.ndngame.libgdx.inputcontrollers.InputController;
import com.stefanolupo.ndngame.libgdx.inputcontrollers.RealInputController;

import java.util.HashMap;
import java.util.Map;

public class LibGdxGameModule extends AbstractModule {

    private static final Vector2 WORLD_GRAVITY = new Vector2(0, 0);

    private final Config config;

    public LibGdxGameModule(Config config) {
        this.config = config;
    }

    @Override
    protected void configure() {
        install(new BackendModule(config));
        Map<String, String> test = new HashMap<>();
        test.put("key", "val");
        LiveConfig liveConfig = LiveConfig.fromMap(test);
        install(new LiveConfigModule(liveConfig));

        // Mutlibinders are additive so these will add to the ones set in the backend module
        Multibinder<OnPlayersDiscovered> onDiscoveryBinder =
                Multibinder.newSetBinder(binder(), OnPlayersDiscovered.class);
        onDiscoveryBinder.addBinding().to(EntityCreator.class);
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


    @Provides
    @Singleton
    OrthographicCamera providesCamera() {
        return new OrthographicCamera();
    }
}
