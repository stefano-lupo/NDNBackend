package com.stefanolupo.ndngame.libgdx.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Body;

import java.util.Optional;

public class BodyComponent implements Component {

    private Body body;
    private Optional<Entity> maybeOwner;

    public void setBody(Body body) {
        this.body = body;

        if (body.getUserData() instanceof Entity) {
            maybeOwner = Optional.of((Entity) body.getUserData());
        } else {
            maybeOwner = Optional.empty();
        }
    }

    public Body getBody() {
        return body;
    }

    public Optional<Entity> getMaybeOwner() {
        return maybeOwner;
    }
}
