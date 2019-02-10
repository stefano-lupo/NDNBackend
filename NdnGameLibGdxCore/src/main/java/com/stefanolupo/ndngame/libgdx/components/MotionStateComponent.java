package com.stefanolupo.ndngame.libgdx.components;

import com.badlogic.ashley.core.Component;
import com.stefanolupo.ndngame.libgdx.components.enums.State;

public class MotionStateComponent implements Component {

    private State currentHozState = State.RESTING;
    private State currentVertState = State.RESTING;
    private float timeInState = 0.0f;

    public void updateState(State hozState, State vertState, float deltaTime){
        if (!(hozState == currentHozState && vertState == currentVertState)) {
            currentHozState = hozState;
            currentVertState = vertState;
            timeInState = 0;
        } else {
            timeInState += deltaTime;
        }
    }

    public void updateHozState(State hozState, float deltaTime) {
        updateState(hozState, currentVertState, deltaTime);
    }

    public void updateVertState(State vertState, float deltaTime) {
        updateState(currentHozState, vertState, deltaTime);
    }

    public State getHozState(){
        return currentHozState;
    }

    public State getVertState() {
        return currentVertState;
    }

    public float getTimeInState() {
        return timeInState;
    }

    public boolean isDiagonal() {
        return currentHozState != State.RESTING && currentVertState != State.RESTING;
    }
}
