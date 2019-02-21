package com.stefanolupo.ndngame.libgdx.inputcontrollers;

public interface InputController {

    boolean isLeftPressed();
    boolean isRightPressed();
    boolean isUpPressed();
    boolean isDownPressed();
    boolean isSpacePressed();

    boolean isMouse1Down();
    boolean isMouse2Down();
    boolean isMouse3Down();

    default boolean isAttackButtonPressed() {
        return isMouse1Down() || isMouse2Down() || isMouse3Down();
    }
    default boolean isInteractButtonPressed() {
        return isSpacePressed();
    }
}
