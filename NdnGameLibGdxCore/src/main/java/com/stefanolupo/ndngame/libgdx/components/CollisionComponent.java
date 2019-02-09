package com.stefanolupo.ndngame.libgdx.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

/**
 * This is always tied to an entity who has this component
 * So we only need to track the entity this entity collided with
 */
public class CollisionComponent implements Component {
    private Entity collidedWith;

    public void setCollidedWith(Entity collidedWith) {
        this.collidedWith = collidedWith;
    }

    public Entity getCollidedWith() {
        return collidedWith;
    }
}
