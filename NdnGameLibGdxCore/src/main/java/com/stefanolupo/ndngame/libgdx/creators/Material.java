package com.stefanolupo.ndngame.libgdx.creators;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;

public enum Material {
    PLAYER(0.001f, 1f, 1f, BodyDef.BodyType.DynamicBody),
    PROJECTILE(0.3f, 0.3f, 0.3f, BodyDef.BodyType.DynamicBody),
    BLOCK(5f, 5f,10f, BodyDef.BodyType.StaticBody);

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
