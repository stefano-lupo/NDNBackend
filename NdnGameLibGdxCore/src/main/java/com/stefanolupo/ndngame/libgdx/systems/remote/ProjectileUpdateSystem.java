package com.stefanolupo.ndngame.libgdx.systems.remote;

import com.badlogic.ashley.systems.IntervalSystem;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.stefanolupo.ndngame.backend.subscriber.ProjectileSubscriber;
import com.stefanolupo.ndngame.libgdx.creators.ProjectileCreator;
import com.stefanolupo.ndngame.libgdx.systems.HasComponentMappers;
import com.stefanolupo.ndngame.names.projectiles.ProjectileName;
import com.stefanolupo.ndngame.protos.Projectile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Singleton
public class ProjectileUpdateSystem extends IntervalSystem implements HasComponentMappers {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectileUpdateSystem.class);

    private static final float PROJECTILE_UPDATE_INTERVAL_MS = 25f / 1000;

    private final ProjectileSubscriber projectileSubscriber;
    private final ProjectileCreator projectileCreator;

    @Inject
    public ProjectileUpdateSystem(ProjectileSubscriber projectileSubscriber,
                                  ProjectileCreator projectileCreator) {
        super(PROJECTILE_UPDATE_INTERVAL_MS);
        this.projectileSubscriber = projectileSubscriber;
        this.projectileCreator = projectileCreator;
    }

    @Override
    protected void updateInterval() {
        Map<ProjectileName, Projectile> newProjectiles = projectileSubscriber.getNewProjectiles();
        newProjectiles.forEach(projectileCreator::createRemoteProjectile);
    }
}
