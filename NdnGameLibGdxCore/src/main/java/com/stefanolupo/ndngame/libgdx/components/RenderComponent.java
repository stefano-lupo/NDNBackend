package com.stefanolupo.ndngame.libgdx.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class RenderComponent implements Component {
    private final Vector3 position = new Vector3();
    private final Vector2 scale = new Vector2(1.0f, 1.0f);
    private float rotation = 0.0f;

    private float width;
    private float height;

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public void setPosition(float x, float y) {
        position.set(x, y, 0);
    }

    public void setScale(float x, float y) {
        scale.set(x, y);
    }

    public Vector3 getPosition() {
        return position;
    }

    public float getRotation() {
        return rotation;
    }

    public Vector2 getScale() {
        return scale;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }
}
