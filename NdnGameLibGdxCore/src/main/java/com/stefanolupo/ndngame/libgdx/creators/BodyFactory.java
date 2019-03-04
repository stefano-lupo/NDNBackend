package com.stefanolupo.ndngame.libgdx.creators;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.stefanolupo.ndngame.protos.GameObject;

@Singleton
class BodyFactory {

    private final World world;

    @Inject
    private BodyFactory(World world) {
        this.world = world;
    }

    Body makeCircleBody(float x, float y, float radius, Material material, boolean fixedRotation) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = material.getBodyType();
        bodyDef.position.x = x;
        bodyDef.position.y = y;
        bodyDef.fixedRotation = fixedRotation;
        Body body = world.createBody(bodyDef);

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(radius);
        body.createFixture(material.getFixtureDef(circleShape));
        circleShape.dispose();

        return body;
    }

    Body makeBoxBody(GameObject gameObject, Material material) {
        // create a definition
        BodyDef boxBodyDef = new BodyDef();
        boxBodyDef.type = material.getBodyType();
        boxBodyDef.position.x = gameObject.getX();
        boxBodyDef.position.y = gameObject.getY();
        boxBodyDef.angle = gameObject.getAngle();
        boxBodyDef.fixedRotation = gameObject.getIsFixedRotation();

        //create the body to attach said definition
        Body boxBody = world.createBody(boxBodyDef);
        PolygonShape poly = new PolygonShape();
        poly.setAsBox(gameObject.getWidth() / 2, gameObject.getHeight() / 2);
        boxBody.createFixture(material.getFixtureDef(poly));
        poly.dispose();

        return boxBody;
    }

    Body makePolygonBody(Vector2[] vertices, float x, float y, Material material) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = material.getBodyType();
        bodyDef.position.x = x;
        bodyDef.position.y = y;
        Body body =  world.createBody(bodyDef);

        PolygonShape polygonShape = new PolygonShape();
        polygonShape.set(vertices);
        body.createFixture(material.getFixtureDef(polygonShape));
        polygonShape.dispose();

        return body;

    }

    Body makeBoundary() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(0, 0);
        Body body = world.createBody(bodyDef);
        EdgeShape edgeShape = new EdgeShape();

        edgeShape.set(0,0, GameWorldCreator.WORLD_WIDTH, 0);
        body.createFixture(edgeShape, 0);

        edgeShape.set(0,0, 0, GameWorldCreator.WORLD_HEIGHT);
        body.createFixture(edgeShape, 0);

        edgeShape.set(GameWorldCreator.WORLD_WIDTH, GameWorldCreator.WORLD_HEIGHT, GameWorldCreator.WORLD_WIDTH, 0);
        body.createFixture(edgeShape, 0);

        edgeShape.set(GameWorldCreator.WORLD_WIDTH, GameWorldCreator.WORLD_HEIGHT, 0, GameWorldCreator.WORLD_HEIGHT);
        body.createFixture(edgeShape, 0);

        return body;
    }
}
