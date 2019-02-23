package com.stefanolupo.ndngame.libgdx.converters;

import com.badlogic.ashley.core.Entity;
import com.stefanolupo.ndngame.libgdx.components.BodyComponent;
import com.stefanolupo.ndngame.libgdx.components.RenderComponent;
import com.stefanolupo.ndngame.libgdx.systems.HasComponentMappers;
import com.stefanolupo.ndngame.protos.GameObject;

public class GameObjectConverter implements HasComponentMappers {

    public static GameObject protoFromEntity(Entity entity) {
        RenderComponent renderComponent = RENDER_MAPPER.get(entity);
        return renderComponent.getGameObject();
    }

    public static void reconcileGameObject(Entity entity, GameObject gameObject) {
        BodyComponent bodyComponent = BODY_MAPPER.get(entity);
        RenderComponent renderComponent = RENDER_MAPPER.get(entity);

        bodyComponent.getBody()
                .setTransform(gameObject.getX(), gameObject.getY(), gameObject.getAngle());
        bodyComponent.getBody()
                .setLinearVelocity(gameObject.getVelX(), gameObject.getVelY());

        renderComponent.setGameObject(gameObject);
    }
}
