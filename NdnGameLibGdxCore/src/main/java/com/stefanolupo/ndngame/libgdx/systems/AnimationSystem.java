package com.stefanolupo.ndngame.libgdx.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.stefanolupo.ndngame.libgdx.components.AnimationComponent;
import com.stefanolupo.ndngame.libgdx.components.StateComponent;
import com.stefanolupo.ndngame.libgdx.components.TextureComponent;
import com.stefanolupo.ndngame.libgdx.components.enums.MotionState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Animates all entites with AnimationComponents
 * Prioritises attack animations over motion animations
 * Sets the TextureComponents of the entity for rendering later
 */
public class AnimationSystem
        extends IteratingSystem
        implements HasComponentMappers{

    private static final Logger LOG = LoggerFactory.getLogger(AnimationSystem.class);

    public AnimationSystem() {
        super(Family.all(TextureComponent.class, AnimationComponent.class, StateComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        AnimationComponent animationComponent = ANIMATION_MAPPER.get(entity);
        StateComponent stateComponent = STATE_MAPPER.get(entity);
        TextureComponent textureComponent = TEXTURE_MAPPER.get(entity);

        Animation<TextureRegion> animationToUse;
        if (stateComponent.isCurrentlyAttacking()) {
            LOG.debug("Using attack animation as: {}", stateComponent.getAttackState());
            animationToUse = animationComponent.getAttackAnimations().get(stateComponent.getAttackState());
        } else if (stateComponent.getVertState() != MotionState.REST) {
            animationToUse = animationComponent.getMotionAnimations().get(stateComponent.getVertState());
            LOG.debug("Using vertical animation as: {}", stateComponent.getVertState());
        } else {
            animationToUse = animationComponent.getMotionAnimations().get(stateComponent.getHozState());
            LOG.debug("Using horizontal animation as: {}", stateComponent.getHozState());
        }

        if (animationToUse == null) {
            LOG.error("Could not find an animation for: \n{}", stateComponent);
            return;
        }

        setTextureRegion(textureComponent, animationToUse, stateComponent);
    }

    private void setTextureRegion(TextureComponent textureComponent,
                                  Animation<TextureRegion> animation,
                                  StateComponent stateComponent) {
        TextureRegion textureRegion = animation.getKeyFrame(stateComponent.getTimeInState());
        if (textureRegion == null) {
            LOG.error("Texture region was null for\n{}\n{}", animation, stateComponent);
        }
//        else {
//            LOG.debug("Using texture region for: {} - kfi: {}", textureComponent, animation.getKeyFrameIndex(stateComponent.getTimeInState()));
//        }
        textureComponent.setRegion(textureRegion);
    }

}
