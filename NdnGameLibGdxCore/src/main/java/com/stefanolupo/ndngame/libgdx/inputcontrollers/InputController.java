package com.stefanolupo.ndngame.libgdx.inputcontrollers;

import com.badlogic.gdx.math.Vector2;

public interface InputController {

    boolean isLeftPressed();
    boolean isRightPressed();
    boolean isUpPressed();
    boolean isDownPressed();
    boolean isSpacePressed();

    boolean isMouse1Down();
    boolean isMouse2Down();
    boolean isMouse3Down();

    Vector2 getMouseCoords();

    default boolean isAttackButtonPressed() {
        return isMouse1Down() || isMouse2Down() || isMouse3Down();
    }
    default boolean isInteractButtonPressed() {
        return isSpacePressed();
    }
}
