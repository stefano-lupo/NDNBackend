package com.stefanolupo.ndngame.libgdx.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.stefanolupo.ndngame.libgdx.components.*;

public class Mappers {

    static final ComponentMapper<AnimationComponent> ANIMATION_MAPPER = ComponentMapper.getFor(AnimationComponent.class);
    static final ComponentMapper<BodyComponent> BODY_MAPPER = ComponentMapper.getFor(BodyComponent.class);
    static final ComponentMapper<CollisionComponent> COLLISION_MAPPER = ComponentMapper.getFor(CollisionComponent.class);
    static final ComponentMapper<EnemyComponent> ENEMY_MAPPER = ComponentMapper.getFor(EnemyComponent.class);
    static final ComponentMapper<PlayerComponent> PLAYER_MAPPER = ComponentMapper.getFor(PlayerComponent.class);
    static final ComponentMapper<StateComponent> STATE_MAPPER = ComponentMapper.getFor(StateComponent.class);
    static final ComponentMapper<TextureComponent> TEXTURE_MAPPER = ComponentMapper.getFor(TextureComponent.class);
    static final ComponentMapper<TransformComponent> TRANSFORM_MAPPER = ComponentMapper.getFor(TransformComponent.class);
    static final ComponentMapper<TypeComponent> TYPE_MAPPER = ComponentMapper.getFor(TypeComponent.class);
}
