package com.stefanolupo.ndngame.libgdx.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.stefanolupo.ndngame.libgdx.components.*;

import java.util.Comparator;

public abstract class BaseSystem extends IteratingSystem {

    protected final ComponentMapper<AnimationComponent> animationMapper;
    protected final ComponentMapper<BodyComponent> bodyMapper;
    protected final ComponentMapper<CollisionComponent> collisionMapper;
    protected final ComponentMapper<PlayerComponent> playerMapper;
    protected final ComponentMapper<StateComponent> stateMapper;
    protected final ComponentMapper<TextureComponent> textureMapper;
    protected final ComponentMapper<TransformComponent> transformMapper;
    protected final ComponentMapper<TypeComponent> typeMapper;


    public BaseSystem(Family family) {
        super(family);
        animationMapper = ComponentMapper.getFor(AnimationComponent.class);
        bodyMapper = ComponentMapper.getFor(BodyComponent.class);
        collisionMapper = ComponentMapper.getFor(CollisionComponent.class);
        playerMapper = ComponentMapper.getFor(PlayerComponent.class);
        stateMapper = ComponentMapper.getFor(StateComponent.class);
        textureMapper = ComponentMapper.getFor(TextureComponent.class);
        transformMapper = ComponentMapper.getFor(TransformComponent.class);
        typeMapper = ComponentMapper.getFor(TypeComponent.class);;
    }
}
