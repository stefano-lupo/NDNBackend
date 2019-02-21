package com.stefanolupo.ndngame.libgdx.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.stefanolupo.ndngame.libgdx.components.enums.AttackState;
import com.stefanolupo.ndngame.libgdx.components.enums.InteractionState;
import com.stefanolupo.ndngame.libgdx.components.enums.MotionState;

import java.util.HashMap;
import java.util.Map;

public class AnimationComponent implements Component {

    private final Map<MotionState, Animation<TextureRegion>> motionAnimations = new HashMap<>();
    private final Map<AttackState, Animation<TextureRegion>> attackAnimations = new HashMap<>();
    private final Map<InteractionState, Animation<TextureRegion>> interactionAnimations = new HashMap<>();

    public Map<MotionState, Animation<TextureRegion>> getMotionAnimations() {
        return motionAnimations;
    }

    public Map<AttackState, Animation<TextureRegion>> getAttackAnimations() {
        return attackAnimations;
    }

    public Map<InteractionState, Animation<TextureRegion>> getInteractionAnimations() {
        return interactionAnimations;
    }
}
