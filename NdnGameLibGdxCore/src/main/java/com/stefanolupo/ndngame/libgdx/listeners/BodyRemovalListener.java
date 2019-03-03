package com.stefanolupo.ndngame.libgdx.listeners;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.physics.box2d.World;
import com.google.inject.Inject;
import com.stefanolupo.ndngame.libgdx.components.BodyComponent;
import com.stefanolupo.ndngame.libgdx.systems.HasComponentMappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BodyRemovalListener implements EntityListener, HasComponentMappers {
    private static final Logger LOG = LoggerFactory.getLogger(BodyRemovalListener.class);
    public static final Family FAMILY = Family.all(BodyComponent.class).get();

    private final PooledEngine pooledEngine;
    private final World world;

    @Inject
    public BodyRemovalListener(PooledEngine pooledEngine, World world) {
        this.pooledEngine = pooledEngine;
        this.world = world;
    }

    @Override
    public void entityAdded(Entity entity) {

    }

    @Override
    public void entityRemoved(Entity entity) {
        LOG.debug("Removed: {}", TYPE_MAPPER.get(entity).getType());
        world.destroyBody(BODY_MAPPER.get(entity).getBody());
    }
}
