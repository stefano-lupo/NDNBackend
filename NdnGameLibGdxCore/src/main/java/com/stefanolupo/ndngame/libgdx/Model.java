package com.stefanolupo.ndngame.libgdx;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class Model {

    private final World world;
    private Body dynamicBody;
    private Body staticBody;
    private Body kinematicBody;

    public Model() {
        world = new World(new Vector2(0, -10f), true);
        world.setContactListener(new MyContactListener(this));
        createFloor();
        createObject();
        createMovingObject();

        BodyFactory bodyFactory = BodyFactory.getInstance(world);
        bodyFactory.makeCirclePolyBody(10, 1, 2, BodyFactory.RUBBER, BodyDef.BodyType.DynamicBody, false);
        bodyFactory.makeCirclePolyBody(4, 1, 2, BodyFactory.STEEL, BodyDef.BodyType.DynamicBody, false);
        bodyFactory.makeBoxPolyBody(-4, 1, 1, 1, BodyFactory.STONE, BodyDef.BodyType.DynamicBody, true);
        Vector2[] vector2s = {new Vector2(1, 0), new Vector2(0, 1), new Vector2(2, 1)};
        bodyFactory.makePolygonBody(vector2s, 3, 0, BodyFactory.WOOD, BodyDef.BodyType.DynamicBody);
    }

    public void logicStep(float delta) {
        world.step(delta, 3, 3);
    }

    public World getWorld() {
        return world;
    }

    private void createObject() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(0, 0);

        dynamicBody = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(5, 1);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;

        // Probably meant to use fixture def here
        dynamicBody.createFixture(fixtureDef);

        shape.dispose();
    }

    private void createFloor() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(0, -10);

        staticBody = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(50, 1);

        staticBody.createFixture(shape, 0.0f);
        shape.dispose();
    }

    private void createMovingObject() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        bodyDef.position.set(0, -12);

        kinematicBody = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(1, 1);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;

        kinematicBody.createFixture(fixtureDef);

        shape.dispose();
        kinematicBody.setLinearVelocity(0, 0.75f);
    }
}
