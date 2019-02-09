package com.stefanolupo.ndngame.libgdx;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.*;
import com.google.common.base.Preconditions;
import com.stefanolupo.ndngame.libgdx.components.CollisionComponent;

public class MyContactListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        Fixture f1 = contact.getFixtureA();
        Fixture f2 = contact.getFixtureB();

        throwIfNotEntity(f1);
        throwIfNotEntity(f2);

        Entity e1 = (Entity) f1.getBody().getUserData();
        Entity e2 = (Entity) f2.getBody().getUserData();

        createCollisionComponentIfEntityHasOne(e1, e2);
        createCollisionComponentIfEntityHasOne(e2, e1);
    }

    private void throwIfNotEntity(Fixture fixture) {
        Preconditions.checkArgument(fixture.getBody().getUserData() instanceof Entity,
                "Collided fixture was not an entity");
    }

    private void createCollisionComponentIfEntityHasOne(Entity e1, Entity e2) {
        CollisionComponent collisionComponent = e1.getComponent(CollisionComponent.class);
        if (collisionComponent != null) {
            collisionComponent.setCollidedWith(e2);
        }
    }

    @Override
    public void endContact(Contact contact) {
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
