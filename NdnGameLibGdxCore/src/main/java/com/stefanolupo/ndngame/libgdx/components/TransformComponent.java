package com.stefanolupo.ndngame.libgdx.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class TransformComponent implements Component {
    private final Vector3 position = new Vector3();
    private final Vector2 scale = new Vector2(1.0f, 1.0f);
    private float rotation = 0.0f;
    private final boolean isHidden = false;

    public boolean isHidden() {
        return isHidden;
    }

    public Vector3 getPosition() {
        return position;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public Vector2 getScale() {
        return scale;
    }
}
