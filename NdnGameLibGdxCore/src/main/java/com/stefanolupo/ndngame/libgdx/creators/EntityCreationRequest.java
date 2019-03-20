package com.stefanolupo.ndngame.libgdx.creators;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.physics.box2d.Body;

import java.util.Collection;
import java.util.function.Consumer;

public class EntityCreationRequest {

    private final Collection<Component> components;
    private final BodyCreationRequest bodyCreationRequest;
    private final Consumer<Body> bodyCallback;

    public EntityCreationRequest(Collection<Component> components, BodyCreationRequest bodyCreationRequest, Consumer<Body> bodyCallback) {
        this.components = components;
        this.bodyCreationRequest = bodyCreationRequest;
        this.bodyCallback = bodyCallback;
    }

    public EntityCreationRequest(Collection<Component> components, BodyCreationRequest bodyCreationRequest) {
        this.components = components;
        this.bodyCreationRequest = bodyCreationRequest;
        bodyCallback = null;
    }

    public Collection<Component> getComponents() {
        return components;
    }

    public BodyCreationRequest getBodyCreationRequest() {
        return bodyCreationRequest;
    }

    public void applyCallback(Body body) {
        if (bodyCallback != null) bodyCallback.accept(body);
    }
}
