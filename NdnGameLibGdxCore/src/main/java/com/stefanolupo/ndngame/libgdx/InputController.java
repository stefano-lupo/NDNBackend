package com.stefanolupo.ndngame.libgdx;

public interface InputController {

    boolean isLeftPressed();
    boolean isRightPressed();
    boolean isUpPressed();
    boolean isDownPressed();
    boolean isSpacePressed();

    boolean isMouse1Down();
    boolean isMouse2Down();
    boolean isMouse3Down();

    default boolean isMouseButtonDown() {
        return isMouse1Down() || isMouse2Down() || isMouse3Down();
    }
}
