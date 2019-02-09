package com.stefanolupo.ndngame.libgdx.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.stefanolupo.ndngame.libgdx.components.BodyComponent;
import com.stefanolupo.ndngame.libgdx.components.TransformComponent;

public class PhysicsSystem
        extends IteratingSystem
        implements HasComponentMappers {

    private static final float MAX_STEP_TIME = 1/45f;

    private final World world;
    private final Array<Entity> bodiesQueue;

    public PhysicsSystem(World world) {
        super(Family.all(BodyComponent.class, TransformComponent.class).get());
        this.world = world;
        bodiesQueue = new Array<>();
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        world.step(MAX_STEP_TIME, 6, 2);
        for (Entity entity : bodiesQueue) {
            TransformComponent transformComponent = TRANSFORM_MAPPER.get(entity);
            BodyComponent bodyComponent = BODY_MAPPER.get(entity);
            Vector2 position = bodyComponent.getBody().getPosition();
            transformComponent.getPosition().x = position.x;
            transformComponent.getPosition().y = position.y;
            transformComponent.setRotation(bodyComponent.getBody().getAngle() * MathUtils.radiansToDegrees);
        }

        bodiesQueue.clear();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        bodiesQueue.add(entity);
    }
}
