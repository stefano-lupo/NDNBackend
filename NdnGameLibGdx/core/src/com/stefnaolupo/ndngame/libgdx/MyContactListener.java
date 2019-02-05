package com.stefnaolupo.ndngame.libgdx;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class MyContactListener implements ContactListener {

    private Model parent;

    public MyContactListener(Model parent) {
        this.parent = parent;
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();
        System.out.println(fa.getBody().getType()+" has hit "+ fb.getBody().getType());
        if(fa.getBody().getType() == BodyDef.BodyType.StaticBody || fb.getBody().getType() == BodyDef.BodyType.StaticBody){
            System.out.println("Adding Force");
            fb.getBody().applyForceToCenter(new Vector2(-100000,-100000), true);
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
