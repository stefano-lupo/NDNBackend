package com.stefanolupo.ndngame.libgdx.creators;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;

public enum Material {
    PLAYER(0.5f, 0f, 1f, BodyDef.BodyType.DynamicBody),
    PROJECTILE(0.3f, 0.3f, 0.3f, BodyDef.BodyType.DynamicBody),
    BLOCK(1f, 0,9f, BodyDef.BodyType.DynamicBody);

    private float density;
    private float friction;
    private float restitution;
    private BodyDef.BodyType bodyType;

    Material(float density, float friction, float restitution, BodyDef.BodyType bodyType) {
        this.density = density;
        this.friction = friction;
        this.restitution = restitution;
        this.bodyType = bodyType;
    }

    public BodyDef.BodyType getBodyType() {
        return bodyType;
    }

    public FixtureDef getFixtureDef(Shape shape) {
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = density;
        fixtureDef.friction = friction;
        fixtureDef.restitution = restitution;
        fixtureDef.shape = shape;
        return fixtureDef;
    }
}
