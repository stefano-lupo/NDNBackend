package com.stefanolupo.ndngame.libgdx.creators;

import com.google.inject.Singleton;
import com.stefanolupo.ndngame.protos.GameObject;

@Singleton
public class GameObjectFactory {

    private static final float DEFAULT_SCALE = 1;

    public static GameObject buildBasicGameObject(float x, float y, float width, float height) {
        return getBasicGameObjectBuilder(x, y, width, height).build();
    }

    public static GameObject buildBasicGameObject(float x, float y, float radius) {
        return buildBasicGameObject(x, y ,radius, radius);
    }

    public static GameObject.Builder getBasicGameObjectBuilder(float x, float y, float width, float height) {
        return GameObject.newBuilder()
                .setX(x)
                .setY(y)
                .setZ(0)
                .setWidth(width)
                .setHeight(height)
                .setAngle(0)
                .setIsFixedRotation(true)
                .setScaleX(DEFAULT_SCALE)
                .setScaleY(DEFAULT_SCALE);
    }

    public static GameObject.Builder getBasicGameObjectBuilder(float x, float y, float radius) {
        return getBasicGameObjectBuilder(x, y, radius, radius);
    }
}
