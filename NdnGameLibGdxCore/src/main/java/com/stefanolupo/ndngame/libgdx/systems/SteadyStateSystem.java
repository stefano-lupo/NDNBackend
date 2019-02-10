package com.stefanolupo.ndngame.libgdx.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.physics.box2d.Body;
import com.stefanolupo.ndngame.libgdx.components.BodyComponent;
import com.stefanolupo.ndngame.libgdx.components.MotionStateComponent;
import com.stefanolupo.ndngame.libgdx.components.enums.State;

/**
 * Updates state based on changes from previous ticks world update
 * Happens before any player / remote input changes
 */
public class SteadyStateSystem extends IteratingSystem implements HasComponentMappers {

    public SteadyStateSystem() {
        super(Family.all(BodyComponent.class, MotionStateComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        MotionStateComponent motionStateComponent = MOTION_STATE_MAPPER.get(entity);
        Body body = BODY_MAPPER.get(entity).getBody();

        if (body.getLinearVelocity().x == 0) {
            motionStateComponent.updateHozState(State.RESTING, deltaTime);
        }

        if (body.getLinearVelocity().y == 0) {
            motionStateComponent.updateVertState(State.RESTING, deltaTime);
        }

//        if (body.getLinearVelocity().y > 0) {
//            motionStateComponent.updateVertState(State.MOVING_UP, deltaTime);
//        } else if (body.getLinearVelocity().y < 0) {
//            motionStateComponent.updateVertState(State.MOVING_DOWN, deltaTime);
//        } else {
//            motionStateComponent.updateVertState(State.RESTING, deltaTime);
//        }
//
//        if (body.getLinearVelocity().x > 0) {
//            motionStateComponent.updateHozState(State.MOVING_RIGHT, deltaTime);
//        } else if (body.getLinearVelocity().x < 0){
//            motionStateComponent.updateHozState(State.MOVING_LEFT, deltaTime);
//        } else {
//            motionStateComponent.updateHozState(State.RESTING, deltaTime);
//        }
    }
}
