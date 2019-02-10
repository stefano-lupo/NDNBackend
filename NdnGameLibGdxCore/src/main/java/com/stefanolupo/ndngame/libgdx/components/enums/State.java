package com.stefanolupo.ndngame.libgdx.components.enums;

public enum State {
    RESTING(0),
    MOVING_UP(2),
    MOVING_LEFT(3),
    MOVING_DOWN(0),
    MOVING_RIGHT(1);

    int state;

    State(int state) {
        this.state = state;
    }

    public int getSpriteSheetRow() {
        return state;
    }
}

