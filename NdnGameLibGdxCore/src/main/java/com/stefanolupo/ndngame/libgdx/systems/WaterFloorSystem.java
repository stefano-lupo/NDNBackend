package com.stefanolupo.ndngame.libgdx.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.physics.box2d.Body;
import com.stefanolupo.ndngame.libgdx.components.BodyComponent;
import com.stefanolupo.ndngame.libgdx.components.WaterFloorComponent;

public class WaterFloorSystem extends BaseSystem {

    private final Entity player;

    public WaterFloorSystem(Entity player) {
        super(Family.all(WaterFloorComponent.class).get());
        this.player = player;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        float currentLevel = player.getComponent(BodyComponent.class).body.getPosition().y;

        // Body of thing we're updating
        Body body = bodyMapper.get(entity).body;

        float speed = currentLevel / 1000;
        speed = speed > 1 ? speed : 1;
        body.setTransform(body.getPosition().x, body.getPosition().y+speed, body.getAngle());
    }
}
