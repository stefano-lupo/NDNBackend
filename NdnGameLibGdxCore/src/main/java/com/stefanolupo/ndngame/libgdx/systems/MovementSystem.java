package com.stefanolupo.ndngame.libgdx.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.stefanolupo.ndngame.libgdx.components.MotionStateComponent;
import com.stefanolupo.ndngame.libgdx.components.enums.State;

/**
 * Sets the velocities of all of the Body objects of entities with MotionStateComponents
 * The PhysicsSystem updates all of their positions using these updated velocities
 */
public class MovementSystem extends IteratingSystem implements HasComponentMappers {

    private static final Float MAX_VEL = 5f;

    public MovementSystem() {
        super(Family.all(MotionStateComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        MotionStateComponent motionStateComponent = MOTION_STATE_MAPPER.get(entity);
        Body body = BODY_MAPPER.get(entity).getBody();

        if (motionStateComponent.getHozState() == State.MOVING_RIGHT) {
            lerpVelocityX(body, MAX_VEL);
        } else if (motionStateComponent.getHozState() == State.MOVING_LEFT) {
            lerpVelocityX(body, -MAX_VEL);
        } else {
            lerpVelocityX(body, 0);
        }

        if (motionStateComponent.getVertState() == State.MOVING_UP) {
            lerpVelocityY(body, MAX_VEL);
        } else if (motionStateComponent.getVertState() == State.MOVING_DOWN) {
            lerpVelocityY(body, -MAX_VEL);
        } else {
            lerpVelocityY(body, 0);
        }
    }


    private void lerpVelocityX(Body body, float toValue) {
        body.setLinearVelocity(MathUtils.lerp(body.getLinearVelocity().x, toValue, 0.2f), body.getLinearVelocity().y);
    }

    private void lerpVelocityY(Body body, float toValue) {
        body.setLinearVelocity(body.getLinearVelocity().x, MathUtils.lerp(body.getLinearVelocity().y, toValue, 0.2f));
    }
}
