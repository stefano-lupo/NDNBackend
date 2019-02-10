package com.stefanolupo.ndngame.libgdx.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.stefanolupo.ndngame.libgdx.components.AnimationComponent;
import com.stefanolupo.ndngame.libgdx.components.MotionStateComponent;
import com.stefanolupo.ndngame.libgdx.components.TextureComponent;
import com.stefanolupo.ndngame.libgdx.components.enums.State;

public class AnimationSystem
        extends IteratingSystem
        implements HasComponentMappers{

    public AnimationSystem() {
        super(Family.all(TextureComponent.class, AnimationComponent.class, MotionStateComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        AnimationComponent animationComponent = ANIMATION_MAPPER.get(entity);
        MotionStateComponent motionStateComponent = MOTION_STATE_MAPPER.get(entity);

        if (motionStateComponent.getVertState() == State.RESTING) {
            Animation<TextureRegion> horiztonalAnimation = animationComponent.getAnimations().get(motionStateComponent.getHozState().getSpriteSheetRow());
            if (horiztonalAnimation != null) {
                TEXTURE_MAPPER.get(entity).setRegion(horiztonalAnimation.getKeyFrame(motionStateComponent.getTimeInState()));
            }
        } else {
            Animation<TextureRegion> verticalAnimation = animationComponent.getAnimations().get(motionStateComponent.getVertState().getSpriteSheetRow());
            if (verticalAnimation != null) {
                TEXTURE_MAPPER.get(entity).setRegion(verticalAnimation.getKeyFrame(motionStateComponent.getTimeInState()));
            }
        }
    }
}
