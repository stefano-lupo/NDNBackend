package com.stefanolupo.ndngame.libgdx.systems.core;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.stefanolupo.ndngame.libgdx.components.CollisionComponent;
import com.stefanolupo.ndngame.libgdx.components.TypeComponent;
import com.stefanolupo.ndngame.libgdx.components.enums.Type;
import com.stefanolupo.ndngame.libgdx.systems.HasComponentMappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CollisionSystem
        extends IteratingSystem
        implements HasComponentMappers {
    
    private static final Logger LOG = LoggerFactory.getLogger(CollisionSystem.class);
    
    public CollisionSystem() {
        super(Family.all(CollisionComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        CollisionComponent collisionComponent = COLLISION_MAPPER.get(entity);
        Entity collidedWithEntity = collisionComponent.getCollidedWith();
        
        if (collidedWithEntity == null) {
            return;
        }


        Type myType = entity.getComponent(TypeComponent.class).getType();
        TypeComponent typeComponent = collidedWithEntity.getComponent(TypeComponent.class);

        if (typeComponent == null) {
            LOG.error("Type component was null for collision with {}", myType);
            collisionComponent.setCollidedWith(null);
            return;
        }

        Type collidedWithType = typeComponent.getType();
        if (typeComponent.getType() == null || myType == null) {
            LOG.error("Null type in collision between {} and {}", myType, typeComponent);
            collisionComponent.setCollidedWith(null);
            return;
        }
        
//        LOG.debug("{} collided with {}", myType, collidedWithType);
        // Reset once handled
        collisionComponent.setCollidedWith(null);
    }
}
