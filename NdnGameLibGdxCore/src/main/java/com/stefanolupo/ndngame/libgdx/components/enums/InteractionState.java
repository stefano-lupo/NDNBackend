package com.stefanolupo.ndngame.libgdx.components.enums;

import com.stefanolupo.ndngame.protos.InteractionType;

import java.util.Arrays;
import java.util.Optional;

public enum InteractionState {
    REST("DIE"),
    PLACE_BLOCK("ATTACK_DOWN"),
    DOOR_TOGGLE("DANCE_DOWN");

    private String stringValue;

    InteractionState(String stringValue) {
        this.stringValue = stringValue;
    }

    public static boolean isInteractionState(String stringValue) {
        return Arrays.stream(values())
                .anyMatch(ms -> ms.stringValue.equals(stringValue));
    }

    public String getStringValue() {
        return stringValue;
    }

    public static InteractionState fromString(String stringValue) {
        Optional<InteractionState> maybeInteractionState = Arrays.stream(values())
                .filter(ss -> ss.stringValue.equals(stringValue))
                .findFirst();
        if (!maybeInteractionState.isPresent()) {
            throw new IllegalArgumentException(String.format("%s is not an AttackState stringValue", stringValue));
        }

        return maybeInteractionState.get();
    }

    public static InteractionState fromInteractionType(InteractionType type) {
        switch (type) {
            case PLACE_BLOCK: return PLACE_BLOCK;
            case DOOR_TOGGLE: return DOOR_TOGGLE;
            default:
                throw new IllegalArgumentException(String.format("Unrecognised attack type: %s", type));
        }
    }
}
