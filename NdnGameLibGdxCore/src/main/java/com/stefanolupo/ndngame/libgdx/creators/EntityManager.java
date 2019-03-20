package com.stefanolupo.ndngame.libgdx.creators;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.stefanolupo.ndngame.libgdx.components.BodyComponent;
import com.stefanolupo.ndngame.libgdx.systems.HasComponentMappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class EntityManager implements EntityListener, HasComponentMappers {

    public static final Family FAMILY = Family.all(BodyComponent.class).get();

    private static final Logger LOG = LoggerFactory.getLogger(EntityManager.class);

    private final Set<EntityCreationRequest> entityCreationRequests = ConcurrentHashMap.newKeySet();
    private final Set<Body> bodiesToRemove = ConcurrentHashMap.newKeySet();

    private final PooledEngine engine;
    private final World world;

    @Inject
    public EntityManager(PooledEngine engine,
                         World world) {
        this.engine = engine;
        this.world = world;
    }

    @Override
    public void entityAdded(Entity entity) {

    }

    @Override
    public void entityRemoved(Entity entity) {
        bodiesToRemove.add(BODY_MAPPER.get(entity).getBody());
    }

    public void addEntityCreationRequest(EntityCreationRequest entityCreationRequest) {
        entityCreationRequests.add(entityCreationRequest);
    }

    /**
     * Only to be called when world is not updating
     */
    public void safelyHandleEntityUpdates() {
        createEntities();
        deleteBodies();
    }

    private void createEntities() {
        for (EntityCreationRequest entityCreationRequest : entityCreationRequests) {
            BodyCreationRequest bodyCreationRequest = entityCreationRequest.getBodyCreationRequest();
            Body body = world.createBody(bodyCreationRequest.getBodyDef());
            body.createFixture(bodyCreationRequest.getFixtureDef());

            // TODO: the shape used in bodyfactory were being disposed of
            // But I have a feeling GC will get them once they go out of scope
            // bodyCreationRequest.getFixtureDef().shape;

            Entity entity = engine.createEntity();
            body.setUserData(entity);

            BodyComponent bodyComponent = engine.createComponent(BodyComponent.class);
            bodyComponent.setBody(body);
            entity.add(bodyComponent);

            entityCreationRequest.getComponents().forEach(entity::add);
            entityCreationRequest.applyCallback(body);

            engine.addEntity(entity);
        }

        entityCreationRequests.clear();
    }

    private void deleteBodies() {
        for (Iterator<Body> it = bodiesToRemove.iterator(); it.hasNext();) {
            LOG.debug("Destroying body");
            world.destroyBody(it.next());
            it.remove();
        }
    }
}
