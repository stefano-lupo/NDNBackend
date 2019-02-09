package com.stefanolupo.ndngame.libgdx.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.stefanolupo.ndngame.libgdx.components.AnimationComponent;
import com.stefanolupo.ndngame.libgdx.components.StateComponent;
import com.stefanolupo.ndngame.libgdx.components.TextureComponent;

import static com.stefanolupo.ndngame.libgdx.systems.Mappers.*;

public class AnimationSystem extends IteratingSystem {

    public AnimationSystem() {
        super(Family.all(TextureComponent.class, AnimationComponent.class, StateComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        AnimationComponent animationComponent = ANIMATION_MAPPER.get(entity);
        StateComponent stateComponent = STATE_MAPPER.get(entity);

        if (animationComponent.getAnimations().containsKey(stateComponent.get())) {
            TextureComponent textureComponent = TEXTURE_MAPPER.get(entity);
            Animation animation = animationComponent.getAnimations().get(stateComponent.get());
            textureComponent.region = (TextureRegion) animation.getKeyFrame(stateComponent.time, stateComponent.isLooping);
        }

        stateComponent.time += deltaTime;
    }
}
