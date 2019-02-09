package com.stefanolupo.ndngame.libgdx.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.stefanolupo.ndngame.libgdx.components.AnimationComponent;
import com.stefanolupo.ndngame.libgdx.components.StateComponent;
import com.stefanolupo.ndngame.libgdx.components.TextureComponent;

public class AnimationSystem
        extends IteratingSystem
        implements HasComponentMappers{

    public AnimationSystem() {
        super(Family.all(TextureComponent.class, AnimationComponent.class, StateComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        AnimationComponent animationComponent = ANIMATION_MAPPER.get(entity);
        StateComponent stateComponent = STATE_MAPPER.get(entity);

        Animation<TextureRegion> animation = animationComponent.getAnimations().get(stateComponent.getState().getSpriteSheetRow());
        if (animation != null) {
            TEXTURE_MAPPER.get(entity).setRegion(animation.getKeyFrame(stateComponent.getTimeInState()));
        }
    }
}
