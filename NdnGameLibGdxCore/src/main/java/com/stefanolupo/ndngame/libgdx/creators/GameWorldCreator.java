package com.stefanolupo.ndngame.libgdx.creators;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.physics.box2d.Body;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.stefanolupo.ndngame.libgdx.components.BodyComponent;
import com.stefanolupo.ndngame.libgdx.components.CollisionComponent;
import com.stefanolupo.ndngame.libgdx.components.TypeComponent;
import com.stefanolupo.ndngame.libgdx.components.enums.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class GameWorldCreator {

    public static final float WORLD_WIDTH = 50;
    public static final float WORLD_HEIGHT = 50;

    private static final Logger LOG = LoggerFactory.getLogger(GameWorldCreator.class);

    private final PooledEngine engine;
    private final BodyFactory bodyFactory;

    @Inject
    public GameWorldCreator(PooledEngine engine,
                            BodyFactory bodyFactory) {
        this.engine = engine;
        this.bodyFactory = bodyFactory;
    }

    public void createInitialWorld() {
        createWorldBoundary();
    }

    private void createWorldBoundary() {
        Entity entity = engine.createEntity();

        Body body = bodyFactory.makeBoundary();
        body.setUserData(entity);
        BodyComponent bodyComponent = engine.createComponent(BodyComponent.class);
        bodyComponent.setBody(body);
        entity.add(bodyComponent);

        TypeComponent typeComponent = engine.createComponent(TypeComponent.class);
        typeComponent.setType(Type.BOUNDARY);
        entity.add(typeComponent);

        CollisionComponent collisionComponent = engine.createComponent(CollisionComponent.class);
        entity.add(collisionComponent);

        engine.addEntity(entity);
    }
}
