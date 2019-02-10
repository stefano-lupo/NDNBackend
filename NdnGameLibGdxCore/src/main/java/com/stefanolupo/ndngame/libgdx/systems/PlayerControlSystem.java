package com.stefanolupo.ndngame.libgdx.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.google.inject.Inject;
import com.stefanolupo.ndngame.libgdx.InputController;
import com.stefanolupo.ndngame.libgdx.components.LocalPlayerComponent;
import com.stefanolupo.ndngame.libgdx.components.MotionStateComponent;
import com.stefanolupo.ndngame.libgdx.components.enums.State;

/**
 * Updates MotionStateComponents based on input from the InputController
 */
public class PlayerControlSystem
        extends IteratingSystem
        implements HasComponentMappers {

    private final InputController inputController;

    @Inject
    public PlayerControlSystem(InputController inputController) {
        super(Family.all(LocalPlayerComponent.class).get());
        this.inputController = inputController;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        MotionStateComponent motionStateComponent = MOTION_STATE_MAPPER.get(entity);

        if(inputController.left){
            motionStateComponent.updateHozState(State.MOVING_LEFT, deltaTime);
        } else if(inputController.right){
            motionStateComponent.updateHozState(State.MOVING_RIGHT, deltaTime);
        } else {
            motionStateComponent.updateHozState(State.RESTING, deltaTime);
        }

        if(inputController.up){
            motionStateComponent.updateVertState(State.MOVING_UP, deltaTime);
        } else if(inputController.down){
            motionStateComponent.updateVertState(State.MOVING_DOWN, deltaTime);
        } else {
            motionStateComponent.updateVertState(State.RESTING, deltaTime);
        }
    }
}
