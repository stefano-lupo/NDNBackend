package com.stefanolupo.ndngame.libgdx.components.enums;

import com.stefanolupo.ndngame.protos.AttackType;

import java.util.Arrays;
import java.util.Optional;

public enum AttackState {
    REST("DIE"),
    SWING("ATTACK_DOWN"),
    CAST("BOW_DOWN"),
    EXPLOSION("BOW_UP"),
    SHIELD("BOW_UP"),
    DANCE("DANCE_UP");

    private String stringValue;

    AttackState(String stringValue) {
        this.stringValue = stringValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    public static boolean isAttackState(String stringValue) {
        return Arrays.stream(values())
                .anyMatch(ms -> ms.stringValue.equals(stringValue));
    }

    public static AttackState fromString(String stringValue) {
        Optional<AttackState> maybeAttackState = Arrays.stream(values())
                .filter(ss -> ss.stringValue.equals(stringValue))
                .findFirst();
        if (!maybeAttackState.isPresent()) {
            throw new IllegalArgumentException(String.format("%s is not an AttackState stringValue", stringValue));
        }

        return maybeAttackState.get();
    }

    public static AttackState fromAttackType(AttackType type) {
        switch (type) {
            case SWING: return SWING;
            case CAST: return CAST;
            case SHIELD: return SHIELD;
            case DANCE: return DANCE;
            default:
                throw new IllegalArgumentException(String.format("Unrecognised attack type: %s", type));
        }
    }
}
