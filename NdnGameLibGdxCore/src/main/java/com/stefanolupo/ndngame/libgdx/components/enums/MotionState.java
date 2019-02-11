package com.stefanolupo.ndngame.libgdx.components.enums;

import java.util.Arrays;
import java.util.Optional;

public enum MotionState {
    REST("DIE"),
    MOVE_UP("MOVE_UP"),
    MOVE_LEFT("MOVE_LEFT"),
    MOVE_DOWN("MOVE_DOWN"),
    MOVE_RIGHT("MOVE_RIGHT");

    String stringValue;

    MotionState(String stringValue) {
        this.stringValue = stringValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    public static boolean isMotionState(String stringValue) {
        return Arrays.stream(values())
                .anyMatch(ms -> ms.stringValue.equals(stringValue));
    }

    public static MotionState fromString(String stringValue) {
        Optional<MotionState> maybeMotionState = Arrays.stream(values())
                .filter(ss -> ss.stringValue.equals(stringValue))
                .findFirst();
        if (!maybeMotionState.isPresent()) {
            throw new IllegalArgumentException(String.format("%s is not an MotionState stringValue", stringValue));
        }

        return maybeMotionState.get();
    }
}

