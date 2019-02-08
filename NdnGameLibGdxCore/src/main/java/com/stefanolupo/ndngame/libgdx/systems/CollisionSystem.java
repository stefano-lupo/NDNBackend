package com.stefanolupo.ndngame.libgdx.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.stefanolupo.ndngame.libgdx.components.CollisionComponent;
import com.stefanolupo.ndngame.libgdx.components.PlayerComponent;
import com.stefanolupo.ndngame.libgdx.components.TypeComponent;

public class CollisionSystem extends BaseSystem {

    public CollisionSystem() {
        super(Family.all(CollisionComponent.class, PlayerComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        CollisionComponent collisionComponent = collisionMapper.get(entity);
        Entity collidedEntity = collisionComponent.collisionEntity;
        if (collidedEntity == null) {
            return;
        }

        TypeComponent typeComponent = collidedEntity.getComponent(TypeComponent.class);
        if (typeComponent == null) {
            return;
        }
        switch (typeComponent.type) {
            case TypeComponent.ENEMY:
                //do player hit enemy thing
                System.out.println("player hit enemy");
                break;
            case TypeComponent.SCENERY:
                //do player hit scenery thing
                System.out.println("player hit scenery");
                break;
            case TypeComponent.SPRING:
                playerMapper.get(entity).onSping = true;
                System.out.println("Player hit spring");
            case TypeComponent.OTHER:
                //do player hit other thing
                System.out.println("player hit other");
        }

        // Reset once handled
        collisionComponent.collisionEntity = null;
    }
}
