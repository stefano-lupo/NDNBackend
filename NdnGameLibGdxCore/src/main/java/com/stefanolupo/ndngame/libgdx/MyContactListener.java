package com.stefanolupo.ndngame.libgdx;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.*;
import com.stefanolupo.ndngame.libgdx.components.CollisionComponent;

public class MyContactListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();

        System.out.println(fa.getBody().getType()+" has hit "+ fb.getBody().getType());

        if (fa.getBody().getUserData() instanceof Entity) {
            Entity entity = (Entity) fa.getBody().getUserData();
            entityCollision(entity, fb);
        } else if (fb.getBody().getUserData() instanceof Entity) {
            Entity entity = (Entity) fb.getBody().getUserData();
            entityCollision(entity, fa);
        }
    }

    private void entityCollision(Entity ent, Fixture fb) {
        if (!(fb.getBody().getUserData() instanceof Entity)) {
            return;
        }

        Entity colEnt = (Entity) fb.getBody().getUserData();

        CollisionComponent col = ent.getComponent(CollisionComponent.class);
        CollisionComponent colb = colEnt.getComponent(CollisionComponent.class);

        if(col != null){
            col.collisionEntity = colEnt;
        }else if(colb != null){
            colb.collisionEntity = ent;
        }

    }


    @Override
    public void endContact(Contact contact) {
        System.out.println("End contact");
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
