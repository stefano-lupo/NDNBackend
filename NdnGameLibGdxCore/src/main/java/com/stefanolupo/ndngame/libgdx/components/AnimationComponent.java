package com.stefanolupo.ndngame.libgdx.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.IntMap;

public class AnimationComponent implements Component {

    private final IntMap<Animation<TextureRegion>> animations = new IntMap<>();

    public IntMap<Animation<TextureRegion>> getAnimations() {
        return animations;
    }
}
