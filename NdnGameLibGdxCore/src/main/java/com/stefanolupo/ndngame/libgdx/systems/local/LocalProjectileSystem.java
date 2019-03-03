package com.stefanolupo.ndngame.libgdx.systems.local;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.stefanolupo.ndngame.backend.publisher.ProjectilePublisher;
import com.stefanolupo.ndngame.libgdx.components.ProjectileComponent;
import com.stefanolupo.ndngame.libgdx.converters.ProjectileConverter;
import com.stefanolupo.ndngame.libgdx.systems.HasComponentMappers;
import com.stefanolupo.ndngame.names.projectiles.ProjectileName;
import com.stefanolupo.ndngame.protos.Projectile;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Singleton
public class LocalProjectileSystem extends IntervalSystem implements HasComponentMappers {

    private final ProjectilePublisher projectilePublisher;

    @Inject
    public LocalProjectileSystem(ProjectilePublisher projectilePublisher) {
        super(5/1000f);
        this.projectilePublisher = projectilePublisher;
    }

    @Override
    protected void updateInterval() {
        // TODO: Only if update / dead reckoning
        ImmutableArray<Entity> projectiles = getEngine().getEntitiesFor(Family.all(ProjectileComponent.class).get());

        Map<ProjectileName, Projectile> localProjectiles = StreamSupport.stream(projectiles.spliterator(), false)
                .filter(e -> !(PROJECTILE_MAPPER.get(e)).isRemote())
                .collect(Collectors.toMap(e -> PROJECTILE_MAPPER.get(e).getProjectileName(), ProjectileConverter::protoFromEntity));

        projectilePublisher.upsertBatch(localProjectiles);
    }
}
