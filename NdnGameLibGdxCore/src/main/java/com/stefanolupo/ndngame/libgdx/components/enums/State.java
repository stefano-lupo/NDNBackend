package com.stefanolupo.ndngame.libgdx.components.enums;

public enum State {
    RESTING(0),
    MOVING_UP(0),
    MOVING_LEFT(1),
    MOVING_DOWN(2),
    MOVING_RIGHT(3);

    int state;

    State(int state) {
        this.state = state;
    }

    public int getSpriteSheetRow() {
        return state;
    }
}

