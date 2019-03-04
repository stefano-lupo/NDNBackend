package com.stefanolupo.ndngame.libgdx.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;

/**
 * This is always tied to an entity who has this component
 * So we only need to track the entity this entity collided with
 */
public class CollisionComponent implements Component {
    private Entity collidedWith;
    private Vector2 collisionLocation;

    public void setCollidedWith(Entity collidedWith) {
        this.collidedWith = collidedWith;
    }

    public Entity getCollidedWith() {
        return collidedWith;
    }

    public Vector2 getCollisionLocation() {
        return collisionLocation;
    }

    public void setCollisionLocation(Vector2 collisionLocation) {
        this.collisionLocation = collisionLocation;
    }
}
