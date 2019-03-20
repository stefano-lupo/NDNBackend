package com.stefanolupo.ndngame.libgdx.creators;

import com.badlogic.gdx.physics.box2d.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.stefanolupo.ndngame.protos.GameObject;

@Singleton
class BodyFactory {

    private static final CircleShape CIRCLE_SHAPE = new CircleShape();
    private static final PolygonShape POLYGON_SHAPE = new PolygonShape();
    private static final EdgeShape EDGE_SHAPE = new EdgeShape();

    private final World world;

    @Inject
    private BodyFactory(World world) {
        this.world = world;
    }

    BodyCreationRequest circleBody(float x, float y, float radius, Material material, boolean fixedRotation) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = material.getBodyType();
        bodyDef.position.x = x;
        bodyDef.position.y = y;
        bodyDef.fixedRotation = fixedRotation;

        CIRCLE_SHAPE.setRadius(radius);
        FixtureDef fixtureDef = material.getFixtureDef(CIRCLE_SHAPE);

        return new BodyCreationRequest(bodyDef, fixtureDef);
    }

    BodyCreationRequest boxBody(GameObject gameObject, Material material) {
        BodyDef boxBodyDef = new BodyDef();
        boxBodyDef.type = material.getBodyType();
        boxBodyDef.position.x = gameObject.getX();
        boxBodyDef.position.y = gameObject.getY();
        boxBodyDef.angle = gameObject.getAngle();
        boxBodyDef.fixedRotation = gameObject.getIsFixedRotation();

        POLYGON_SHAPE.setAsBox(gameObject.getWidth() / 2, gameObject.getHeight() / 2);
        FixtureDef fixtureDef = material.getFixtureDef(POLYGON_SHAPE);

        return new BodyCreationRequest(boxBodyDef, fixtureDef);
    }


    Body makeBoundary() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(0, 0);
        Body body = world.createBody(bodyDef);

        EDGE_SHAPE.set(0,0, GameWorldCreator.WORLD_WIDTH, 0);
        body.createFixture(EDGE_SHAPE, 0);

        EDGE_SHAPE.set(0,0, 0, GameWorldCreator.WORLD_HEIGHT);
        body.createFixture(EDGE_SHAPE, 0);

        EDGE_SHAPE.set(GameWorldCreator.WORLD_WIDTH, GameWorldCreator.WORLD_HEIGHT, GameWorldCreator.WORLD_WIDTH, 0);
        body.createFixture(EDGE_SHAPE, 0);

        EDGE_SHAPE.set(GameWorldCreator.WORLD_WIDTH, GameWorldCreator.WORLD_HEIGHT, 0, GameWorldCreator.WORLD_HEIGHT);
        body.createFixture(EDGE_SHAPE, 0);

        return body;
    }
}
