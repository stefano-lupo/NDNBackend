package com.stefanolupo.ndngame.libgdx;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.google.common.base.Strings;

public class MyContactListener implements ContactListener {

    private MyModel parent;

    public MyContactListener(MyModel parent) {
        this.parent = parent;
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();

        if (isTheSea(fa) || isTheSea(fb)) {
            System.out.println("Skipping as was water");
            parent.isSwimming = true;
            return;
        }

        System.out.println(fa.getBody().getType()+" has hit "+ fb.getBody().getType());
        if(fa.getBody().getType() == BodyDef.BodyType.StaticBody){
            shootUpInAir(fa, fb);
        } else if (fb.getBody().getType() == BodyDef.BodyType.StaticBody) {
            shootUpInAir(fb, fa);
        }
    }

    private void shootUpInAir(Fixture staticFixture, Fixture otherFixture) {
        System.out.println("Adding Force");
        parent.playSound(0);
//        otherFixture.getBody().applyForceToCenter(new Vector2(-100000,-100000), true);
    }

    private boolean isTheSea(Fixture fixture) {
        return fixture.getBody().getUserData() != null;
    }

    @Override
    public void endContact(Contact contact) {
        System.out.println("End contact");
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();

        if (isTheSea(fa) || isTheSea(fb)) {
            parent.isSwimming = false;
        }

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
