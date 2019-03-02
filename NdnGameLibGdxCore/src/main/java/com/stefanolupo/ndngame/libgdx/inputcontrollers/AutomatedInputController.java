package com.stefanolupo.ndngame.libgdx.inputcontrollers;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class AutomatedInputController implements InputController {

    private static final int WALK_TIME_MS = 400;

    private static int count = 0;

    private boolean isLeftPressed = false;
    private boolean isRightPressed = false;
    private boolean isUpPressed = false;
    private boolean isDownPressed = false;
    private boolean isSpacePressed = false;
    private boolean isMouse1Pressed = false;

    public AutomatedInputController() {
        ThreadFactory namedThreadFactory =
                new ThreadFactoryBuilder().setNameFormat("automated-controller-%d").build();
       Executors.newSingleThreadScheduledExecutor(namedThreadFactory)
                .scheduleAtFixedRate(this::moveOnPath, 2000, WALK_TIME_MS, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean isLeftPressed() {
        return isLeftPressed;
    }

    @Override
    public boolean isRightPressed() {
        return isRightPressed;
    }

    @Override
    public boolean isUpPressed() {
        return isUpPressed;
    }

    @Override
    public boolean isDownPressed() {
        return isDownPressed;
    }

    @Override
    public boolean isSpacePressed() {
        return isSpacePressed;
    }

    @Override
    public boolean isMouse1Down() {
        return isMouse1Pressed;
    }

    @Override
    public boolean isMouse2Down() {
        return false;
    }

    @Override
    public boolean isMouse3Down() {
        return false;
    }

    private void moveOnPath() {
        setAllFalse();
        if (count == 0) {
            isRightPressed = true;
        } else if (count == 1) {
            isDownPressed = true;
        } else if (count == 2) {
            isLeftPressed = true;
        } else if (count == 3) {
            isUpPressed = true;
        }

        // Using 5 should make them stand still for a tick
        count = ++count % 5;

//        if (Math.random() > 0.98) {
//            isSpacePressed = true;
//        }
//
//        if (Math.random() > 0.98) {
//            isMouse1Pressed = true;
//        }
    }

    private void setAllFalse() {
        isSpacePressed = false;
        isLeftPressed = false;
        isRightPressed = false;
        isUpPressed = false;
        isDownPressed = false;
        isMouse1Pressed = false;
    }
}
