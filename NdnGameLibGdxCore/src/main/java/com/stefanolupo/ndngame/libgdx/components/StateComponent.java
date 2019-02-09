package com.stefanolupo.ndngame.libgdx.components;

import com.badlogic.ashley.core.Component;
import com.stefanolupo.ndngame.libgdx.components.enums.State;

public class StateComponent implements Component {

    private State currentState = State.RESTING;
    private float timeInState = 0.0f;

    public void updateState(State newState, float deltaTime){
        if (newState != currentState) {
            currentState = newState;
            timeInState = 0;
        } else {
            timeInState += deltaTime;
        }
    }

    public State getState(){
        return currentState;
    }

    public float getTimeInState() {
        return timeInState;
    }
}
