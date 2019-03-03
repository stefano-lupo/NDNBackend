package com.stefanolupo.ndngame.libgdx.systems.remote;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.stefanolupo.ndngame.backend.publisher.ProjectilePublisher;
import com.stefanolupo.ndngame.backend.subscriber.ProjectileSubscriber;
import com.stefanolupo.ndngame.libgdx.components.ProjectileComponent;
import com.stefanolupo.ndngame.libgdx.converters.ProjectileConverter;
import com.stefanolupo.ndngame.libgdx.creators.ProjectileCreator;
import com.stefanolupo.ndngame.libgdx.systems.HasComponentMappers;
import com.stefanolupo.ndngame.names.projectiles.ProjectileName;
import com.stefanolupo.ndngame.protos.Projectile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Singleton
public class ProjectileUpdateSystem extends IntervalSystem implements HasComponentMappers {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectileUpdateSystem.class);

    private static final float PROJECTILE_UPDATE_INTERVAL_MS = 25f / 1000;

    private final ProjectileSubscriber projectileSubscriber;
    private final ProjectilePublisher projectilePublisher;
    private final ProjectileCreator projectileCreator;

    @Inject
    public ProjectileUpdateSystem(ProjectileSubscriber projectileSubscriber,
                                  ProjectilePublisher projectilePublisher,
                                  ProjectileCreator projectileCreator) {
        super(PROJECTILE_UPDATE_INTERVAL_MS);
        this.projectileSubscriber = projectileSubscriber;
        this.projectilePublisher = projectilePublisher;
        this.projectileCreator = projectileCreator;
    }

    @Override
    protected void updateInterval() {
        Map<ProjectileName, Projectile> updatedRemoteProjectiles = projectileSubscriber.getRemoteProjectilesWithUpdates();
        Map<ProjectileName, Projectile> localProjectiles = projectilePublisher.getLocalProjectiles();
        Map<ProjectileName, Entity> entitiesByProjectileName = getEntitiesByProjectileName();

        // Create missing remote blocks
        Set<ProjectileName> projectilesToCreate = Sets.difference(updatedRemoteProjectiles.keySet(), entitiesByProjectileName.keySet());

        if (!projectilesToCreate.isEmpty()) {
            LOG.debug("Creating {} projectiles", projectilesToCreate.size());
            LOG.debug("{}", projectilesToCreate.iterator().next().getId());
        }
        for (ProjectileName projectileNames : projectilesToCreate) {
            Projectile projectile = updatedRemoteProjectiles.get(projectileNames);
            projectileCreator.createRemoteProjectile(projectileNames, projectile);
        }

        // Destroy old blocks
//        Set<ProjectileName> projectilesToDestroy = new HashSet<>(Sets.difference(
//                entitiesByProjectileName.keySet(),
//                Sets.union(updatedRemoteProjectiles.keySet(), localProjectiles.keySet())));
//        projectilesToDestroy.removeAll(projectilesToCreate);
//        if (!projectilesToDestroy.isEmpty()) {
//            LOG.debug("Destroying {} projectiles", projectilesToDestroy.size());
//            LOG.debug("{}", projectilesToDestroy.iterator().next().getId());
//        }
//        for (ProjectileName projectileName : projectilesToDestroy) {
//            getEngine().removeEntity(entitiesByProjectileName.get(projectileName));
//            entitiesByProjectileName.remove(projectileName);
//        }

        // Reconcile updated remote blocks
        for (ProjectileName projectileName : entitiesByProjectileName.keySet()) {
            ProjectileComponent projectileComponent = PROJECTILE_MAPPER.get(entitiesByProjectileName.get(projectileName));
            if (!projectileComponent.isRemote()) {
                continue;
            }

            if (!updatedRemoteProjectiles.containsKey(projectileName)) {
                continue;
            }

            ProjectileConverter.reconcileRemoteProjectile(entitiesByProjectileName.get(projectileName), updatedRemoteProjectiles.get(projectileName));
        }
    }

    private Map<ProjectileName, Entity> getEntitiesByProjectileName() {
        ImmutableArray<Entity> projectileEntities = getEngine().getEntitiesFor(Family.all(ProjectileComponent.class).get());
        Map<ProjectileName, Entity> entityMap = new HashMap<>();
        for (Entity e : projectileEntities) {
            ProjectileComponent projectileComponent = PROJECTILE_MAPPER.get(e);
            entityMap.put(projectileComponent.getProjectileName(), e);
        }

        return entityMap;
    }
}
