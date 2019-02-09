package com.stefanolupo.ndngame.libgdx.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.stefanolupo.ndngame.libgdx.components.CollisionComponent;
import com.stefanolupo.ndngame.libgdx.components.TypeComponent;
import com.stefanolupo.ndngame.libgdx.components.enums.Type;
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
        Type colliededWithType = collidedWithEntity.getComponent(TypeComponent.class).getType();

        if (colliededWithType == null || myType == null) {
            LOG.error("Null type in collision between {} and {}", myType, colliededWithType);
            return;
        }
        
        switch (colliededWithType) {
            case PLAYER:
                LOG.debug("{} hit player", myType);
            case ENEMY:
                LOG.debug("{} hit enemy", myType);
                break;
            case SCENERY:
                LOG.debug("{} hit scenery", myType);
                break;
            case OTHER:
                LOG.debug("{} hit other", myType);
        }

        // Reset once handled
        collisionComponent.setCollidedWith(null);
    }
}
