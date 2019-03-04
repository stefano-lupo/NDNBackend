package com.stefanolupo.ndngame.libgdx.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.stefanolupo.ndngame.libgdx.components.*;

public interface HasComponentMappers {

    ComponentMapper<AnimationComponent> ANIMATION_MAPPER = ComponentMapper.getFor(AnimationComponent.class);
    ComponentMapper<BodyComponent> BODY_MAPPER = ComponentMapper.getFor(BodyComponent.class);
    ComponentMapper<CollisionComponent> COLLISION_MAPPER = ComponentMapper.getFor(CollisionComponent.class);
    ComponentMapper<LocalPlayerComponent> LOCAL_PLAYER_MAPPER = ComponentMapper.getFor(LocalPlayerComponent.class);
    ComponentMapper<StateComponent> STATE_MAPPER = ComponentMapper.getFor(StateComponent.class);
    ComponentMapper<TextureComponent> TEXTURE_MAPPER = ComponentMapper.getFor(TextureComponent.class);
    ComponentMapper<RenderComponent> RENDER_MAPPER = ComponentMapper.getFor(RenderComponent.class);
    ComponentMapper<TypeComponent> TYPE_MAPPER = ComponentMapper.getFor(TypeComponent.class);
    ComponentMapper<RemotePlayerComponent> REMOTE_PLAYER_MAPPER = ComponentMapper.getFor(RemotePlayerComponent.class);
    ComponentMapper<AttackComponent> ATTACK_MAPPER = ComponentMapper.getFor(AttackComponent.class);
    ComponentMapper<BlockComponent> BLOCK_MAPPER = ComponentMapper.getFor(BlockComponent.class);
    ComponentMapper<StatusComponent> STATUS_MAPPER = ComponentMapper.getFor(StatusComponent.class);
    ComponentMapper<ProjectileComponent> PROJECTILE_MAPPER = ComponentMapper.getFor(ProjectileComponent.class);
}
