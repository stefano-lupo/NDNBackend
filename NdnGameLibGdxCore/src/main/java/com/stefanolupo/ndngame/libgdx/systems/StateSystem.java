package com.stefanolupo.ndngame.libgdx.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.physics.box2d.Body;
import com.stefanolupo.ndngame.libgdx.components.BodyComponent;
import com.stefanolupo.ndngame.libgdx.components.enums.State;
import com.stefanolupo.ndngame.libgdx.components.StateComponent;

public class StateSystem extends IteratingSystem implements HasComponentMappers {

    public StateSystem() {
        super(Family.all(BodyComponent.class, StateComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        StateComponent stateComponent = STATE_MAPPER.get(entity);
        Body body = BODY_MAPPER.get(entity).getBody();

        if (body.getLinearVelocity().y > 0) {
            stateComponent.updateState(State.MOVING_UP, deltaTime);
            return;
        } else if (body.getLinearVelocity().y < 0) {
            stateComponent.updateState(State.MOVING_DOWN, deltaTime);
            return;
        }

        // No y movement so check if moving or resting
        if (body.getLinearVelocity().x > 0) {
            stateComponent.updateState(State.MOVING_RIGHT, deltaTime);
        } else if (body.getLinearVelocity().x < 0){
            stateComponent.updateState(State.MOVING_LEFT, deltaTime);
        } else {
            stateComponent.updateState(State.RESTING, deltaTime);
        }


    }
}
