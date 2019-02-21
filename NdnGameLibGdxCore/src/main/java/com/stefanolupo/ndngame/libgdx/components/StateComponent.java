package com.stefanolupo.ndngame.libgdx.components;

import com.badlogic.ashley.core.Component;
import com.stefanolupo.ndngame.libgdx.components.enums.AttackState;
import com.stefanolupo.ndngame.libgdx.components.enums.InteractionState;
import com.stefanolupo.ndngame.libgdx.components.enums.MotionState;

public class StateComponent implements Component {

    public static final float ATTACK_STATE_ANIMATION_TIME_MS = 2;
    public static final float INTERACT_STATE_ANIMATION_TIME_MS = 1.5f;

    private MotionState currentHozMotionState = MotionState.REST;
    private MotionState currentVertMotionState = MotionState.REST;
    private AttackState attackState = AttackState.REST;
    private InteractionState interactionState = InteractionState.REST;

    private float timeInState = 0.0f;

    public void updateMotionState(float velX, float velY, float deltaTime) {
        MotionState newHozMotionState = MotionState.REST;
        MotionState newVertMotionState = MotionState.REST;

        if (velX > 0) {
            newHozMotionState = MotionState.MOVE_RIGHT;
        } else if (velX < 0) {
            newHozMotionState = MotionState.MOVE_LEFT;
        }

        if (velY > 0) {
            newVertMotionState = MotionState.MOVE_UP;
        } else if (velY < 0) {
            newVertMotionState = MotionState.MOVE_DOWN;
        }

        updateMotionState(newHozMotionState, newVertMotionState, deltaTime);
    }

    public void updateHozState(MotionState hozMotionState, float deltaTime) {
        updateMotionState(hozMotionState, currentVertMotionState, deltaTime);
    }

    public void updateVertState(MotionState vertMotionState, float deltaTime) {
        updateMotionState(currentHozMotionState, vertMotionState, deltaTime);
    }

    public void updateAttackState(AttackState attackState, float deltaTime) {
        if (this.attackState != attackState) {
            this.attackState = attackState;
            timeInState = 0;
        } else {
            timeInState += deltaTime;
        }
    }

    public void updateInteractionState(InteractionState interactionState, float deltaTime) {
        if (this.interactionState != interactionState) {
            this.interactionState = interactionState;
            timeInState = 0;
        } else {
            timeInState += deltaTime;
        }
    }

    public AttackState getAttackState() {
        return attackState;
    }

    public MotionState getHozState(){
        return currentHozMotionState;
    }

    public MotionState getVertState() {
        return currentVertMotionState;
    }

    public InteractionState getInteractionState() {
        return interactionState;
    }

    public float getTimeInState() {
        return timeInState;
    }

    public boolean isBusy() {
        return  isInAttackState() || isInInteractState();
    }

    public boolean isInAttackState() {
        return attackState != AttackState.REST;
    }

    public boolean isInInteractState() {
        return interactionState != InteractionState.REST;
    }

    private void updateMotionState(MotionState hozMotionState, MotionState vertMotionState, float deltaTime){
        if (!(hozMotionState == currentHozMotionState && vertMotionState == currentVertMotionState)) {
            currentHozMotionState = hozMotionState;
            currentVertMotionState = vertMotionState;
            timeInState = 0;
        } else {
            timeInState += deltaTime;
        }
    }

}
