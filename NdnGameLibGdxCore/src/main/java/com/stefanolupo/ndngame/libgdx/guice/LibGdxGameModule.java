package com.stefanolupo.ndngame.libgdx.guice;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.World;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.stefanolupo.ndngame.backend.guice.BackendModule;
import com.stefanolupo.ndngame.config.Config;
import com.stefanolupo.ndngame.libgdx.BodyFactory;
import com.stefanolupo.ndngame.libgdx.GameAssetManager;
import com.stefanolupo.ndngame.libgdx.MyContactListener;

public class LibGdxGameModule extends AbstractModule {

    private static final Vector2 WORLD_GRAVITY = new Vector2(0, -10f);

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
    GameAssetManager providesGameAssetManager() {
        // Note this is will not load the game assets yet
        // as that has to be done at a very specific time (after Gdx.files is initialized)
        return new GameAssetManager();
    }

    @Provides
    @Singleton
    World providesGameWorld() {
        return new World(WORLD_GRAVITY, true);
    }

    @Provides
    @Singleton
    ContactListener providesMyContactLisener() {
        return new MyContactListener();
    }

    @Provides
    @Singleton
    BodyFactory providesBodyFactory(World world) {
        return BodyFactory.getInstance(world);
    }
}
