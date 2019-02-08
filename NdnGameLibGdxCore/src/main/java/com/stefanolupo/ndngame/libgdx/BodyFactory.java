package com.stefanolupo.ndngame.libgdx;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class BodyFactory {

    public static final int STEEL = 0;
    public static final int WOOD = 1;
    public static final int RUBBER = 2;
    public static final int STONE = 3;

    private static final float DEGTORAD = 0.0174533f;
    private static BodyFactory instance;

    private World world;

    private BodyFactory(World world) {
        this.world = world;
    }

    public Body makeCirclePolyBody(float x, float y, float radius, int material, BodyDef.BodyType bodyType, boolean fixedRotation) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = bodyType;
        bodyDef.position.x = x;
        bodyDef.position.y = y;
        bodyDef.fixedRotation = fixedRotation;

        Body body = world.createBody(bodyDef);
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(radius);
        body.createFixture(makeFixture(material, circleShape));
        circleShape.dispose();

        return body;
    }

    public Body makeBoxPolyBody(float x, float y, float width, float height, int material, BodyDef.BodyType bodyType, boolean fixedRotation){
        // create a definition
        BodyDef boxBodyDef = new BodyDef();
        boxBodyDef.type = bodyType;
        boxBodyDef.position.x = x;
        boxBodyDef.position.y = y;
        boxBodyDef.fixedRotation = fixedRotation;

        //create the body to attach said definition
        Body boxBody = world.createBody(boxBodyDef);
        PolygonShape poly = new PolygonShape();
        poly.setAsBox(width / 2, height / 2);
        boxBody.createFixture(makeFixture(material,poly));
        poly.dispose();

        return boxBody;
    }

    public Body makeBoxPolyBody(float x, float y, float width, float height, int material, BodyDef.BodyType bodyType) {
        return makeBoxPolyBody(x, y, width, height, material, bodyType, false);
    }

    public void makeConeSensor(Body body, float size) {
        FixtureDef fixtureDef = new FixtureDef();
//        fixtureDef.isSensor = true;

        PolygonShape polygon = new PolygonShape();

        float radius = size;
        Vector2[] vertices = new Vector2[5];
        vertices[0] = new Vector2(0, 0);
        for (int i = 2; i < 6; i++) {
            float angle = (float) (i / 6.0 * 145 * DEGTORAD);
            vertices[i-1] = new Vector2(radius*((float) Math.cos(angle)), radius * ((float)Math.sin(angle)));
        }
        polygon.set(vertices);
        fixtureDef.shape = polygon;
        body.createFixture(fixtureDef);
        polygon.dispose();

    }

    public void makeAllFixturesSensors(Body body) {
        for (Fixture f : body.getFixtureList()) {
            f.setSensor(true);
        }
    }

    public Body makePolygonBody(Vector2[] vertices, float x, float y, int material, BodyDef.BodyType bodyType) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = bodyType;
        bodyDef.position.x = x;
        bodyDef.position.y = y;
        Body body =  world.createBody(bodyDef);

        PolygonShape polygonShape = new PolygonShape();
        polygonShape.set(vertices);
        body.createFixture(makeFixture(material, polygonShape));
        polygonShape.dispose();

        return body;

    }

    public static BodyFactory getInstance(World world) {
        if (instance == null) {
            instance = new BodyFactory(world);
        }

        return instance;
    }

    public static FixtureDef makeFixture(int material, Shape shape) {
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;

        switch(material){
            case 0:
                fixtureDef.density = 1f;
                fixtureDef.friction = 0.3f;
                fixtureDef.restitution = 0.1f;
                break;
            case 1:
                fixtureDef.density = 0.5f;
                fixtureDef.friction = 0.7f;
                fixtureDef.restitution = 0.3f;
                break;
            case 2:
                fixtureDef.density = 1f;
                fixtureDef.friction = 0f;
                fixtureDef.restitution = 1f;
                break;
            case 3:
                fixtureDef.density = 1f;
                fixtureDef.friction = 0.9f;
                fixtureDef.restitution = 0.01f;
            default:
                fixtureDef.density = 7f;
                fixtureDef.friction = 0.5f;
                fixtureDef.restitution = 0.3f;
        }
        return fixtureDef;
    }
}
