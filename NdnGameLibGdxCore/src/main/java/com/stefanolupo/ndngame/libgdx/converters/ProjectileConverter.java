package com.stefanolupo.ndngame.libgdx.converters;

import com.badlogic.ashley.core.Entity;
import com.stefanolupo.ndngame.libgdx.components.ProjectileComponent;
import com.stefanolupo.ndngame.libgdx.systems.HasComponentMappers;
import com.stefanolupo.ndngame.protos.Projectile;


public class ProjectileConverter implements HasComponentMappers {


    public static Projectile protoFromEntity(Entity entity) {
        ProjectileComponent projectileComponent = PROJECTILE_MAPPER.get(entity);

        return Projectile.newBuilder()
                .setId(projectileComponent.getProjectileName().getId())
                .setGameObject(GameObjectConverter.protoFromEntity(entity))
                .setDamage(projectileComponent.getDamage())
                .build();
    }

    public static void reconcileRemoteProjectile(Entity entity, Projectile projectile) {
        ProjectileComponent projectileComponent = PROJECTILE_MAPPER.get(entity);
        GameObjectConverter.reconcileGameObject(entity, projectile.getGameObject());
        projectileComponent.setDamage(projectile.getDamage());
    }
}
