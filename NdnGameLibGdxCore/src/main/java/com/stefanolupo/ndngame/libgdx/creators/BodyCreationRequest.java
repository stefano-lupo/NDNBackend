package com.stefanolupo.ndngame.libgdx.creators;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class BodyCreationRequest {

    private final BodyDef bodyDef;
    private final FixtureDef fixtureDef;

    public BodyCreationRequest(BodyDef bodyDef, FixtureDef fixtureDef) {
        this.bodyDef = bodyDef;
        this.fixtureDef = fixtureDef;
    }

    public BodyDef getBodyDef() {
        return bodyDef;
    }

    public FixtureDef getFixtureDef() {
        return fixtureDef;
    }
}
