package com.stefanolupo.ndngame.libgdx.creators;

import com.badlogic.gdx.physics.box2d.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.stefanolupo.ndngame.protos.GameObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

@Singleton
class BodyFactory {

    private static final CircleShape CIRCLE_SHAPE = new CircleShape();
    private static final PolygonShape POLYGON_SHAPE = new PolygonShape();
    private static final EdgeShape EDGE_SHAPE = new EdgeShape();
    private static final float QUADRANT_GAP = GameWorldCreator.WORLD_WIDTH * 0.1f;
    private static final float QUADRANT_SIZE = (GameWorldCreator.WORLD_WIDTH / 2) - QUADRANT_GAP;

    private static final Logger LOG = LoggerFactory.getLogger(BodyFactory.class);

    private final World world;

    @Inject
    private BodyFactory(World world) {
        this.world = world;
        LOG.info("Using quadrant size of {} and quadrant gap of {}", QUADRANT_SIZE, QUADRANT_GAP);
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


    Body makeGameWorldBoundary() {
        return makeBoxBoundary(0, 0, GameWorldCreator.WORLD_WIDTH, GameWorldCreator.WORLD_HEIGHT);
    }

    List<Body> makeQuadrants() {
        // Q1
        return Arrays.asList(
                makeBoxBoundary(0, 0, QUADRANT_SIZE, QUADRANT_SIZE),
                makeBoxBoundary(0, QUADRANT_SIZE + 2*QUADRANT_GAP, QUADRANT_SIZE, QUADRANT_SIZE),
                makeBoxBoundary(QUADRANT_SIZE + 2*QUADRANT_GAP, 0, QUADRANT_SIZE, QUADRANT_SIZE),
                makeBoxBoundary(QUADRANT_SIZE + 2* QUADRANT_GAP, QUADRANT_SIZE + 2*QUADRANT_GAP, QUADRANT_SIZE, QUADRANT_SIZE)
        );
    }

    private Body makeBoxBoundary(float x, float y, float width, float height) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(0, 0);
        Body body = world.createBody(bodyDef);

        EDGE_SHAPE.set(x, y, x+width, y);
        body.createFixture(EDGE_SHAPE, 0);

        EDGE_SHAPE.set(x,y, x, y+height);
        body.createFixture(EDGE_SHAPE, 0);

        EDGE_SHAPE.set(x+width, y+width, x+width, y);
        body.createFixture(EDGE_SHAPE, 0);

        EDGE_SHAPE.set(x+width, y+width, x, y+height);
        body.createFixture(EDGE_SHAPE, 0);

        return body;
    }
}
