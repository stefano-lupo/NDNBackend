package com.stefanolupo.ndngame.libgdx.systems.core;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.stefanolupo.ndngame.backend.publisher.BlockPublisher;
import com.stefanolupo.ndngame.backend.publisher.PlayerStatusPublisher;
import com.stefanolupo.ndngame.backend.publisher.ProjectilePublisher;
import com.stefanolupo.ndngame.backend.subscriber.ProjectileSubscriber;
import com.stefanolupo.ndngame.libgdx.components.*;
import com.stefanolupo.ndngame.libgdx.components.enums.Type;
import com.stefanolupo.ndngame.libgdx.converters.BlockConverter;
import com.stefanolupo.ndngame.libgdx.systems.HasComponentMappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class ProjectileCollisionSystem
        extends IteratingSystem
        implements HasComponentMappers {
    
    private static final Logger LOG = LoggerFactory.getLogger(ProjectileCollisionSystem.class);

    private final ProjectileSubscriber projectileSubscriber;
    private final PlayerStatusPublisher playerStatusPublisher;
    private final ProjectilePublisher projectilePublisher;
    private final BlockPublisher blockPublisher;

    @Inject
    public ProjectileCollisionSystem(ProjectileSubscriber projectileSubscriber,
                                     PlayerStatusPublisher playerStatusPublisher,
                                     ProjectilePublisher projectilePublisher,
                                     BlockPublisher blockPublisher) {
        super(Family.all(CollisionComponent.class, ProjectileComponent.class).get());
        this.projectileSubscriber = projectileSubscriber;
        this.projectilePublisher = projectilePublisher;
        this.playerStatusPublisher = playerStatusPublisher;
        this.blockPublisher = blockPublisher;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        CollisionComponent collisionComponent = COLLISION_MAPPER.get(entity);
        handleMaybeCollision(entity, collisionComponent);

        // Consume collision
        collisionComponent.setCollidedWith(null);
    }

    private void handleMaybeCollision(Entity entity, CollisionComponent collisionComponent) {
        Entity collidedWithEntity = collisionComponent.getCollidedWith();
        if (collidedWithEntity == null) {
            return;
        }

        Type myType = entity.getComponent(TypeComponent.class).getType();
        TypeComponent collidedWithTypeComponent = collidedWithEntity.getComponent(TypeComponent.class);

        if (collidedWithTypeComponent == null) {
            LOG.error("Type component was null for collision with {}", myType);
            return;
        }

        Type collidedWithType = collidedWithTypeComponent.getType();


        if (collidedWithType == null || myType == null) {
            LOG.error("Null type in collision between {} and {}", myType, collidedWithTypeComponent);
            return;
        }

        ProjectileComponent projectileComponent = PROJECTILE_MAPPER.get(entity);

        // Handle local bullet hitting static object
        if (collidedWithType == Type.BOUNDARY || collidedWithType == Type.SCENERY) {
            getEngine().removeEntity(entity);
            return;
        }

        if (collidedWithType == Type.PLACABLE_OBJECT) {
            LOG.debug("Projectile collided with block");
            if (projectileComponent.isRemote()) {
                BlockComponent blockComponent = BLOCK_MAPPER.get(collidedWithEntity);
                if (!blockComponent.isRemote()) {
                    LOG.debug("Remote projectile hit my block");
                    int previousHealth = blockComponent.getHealth();
                    if (previousHealth > 2) {
                        blockComponent.setHealth(previousHealth - 1);
                        blockPublisher.upsertBlock(blockComponent.getBlockName(), BlockConverter.protoFromEntity(collidedWithEntity));
                    } else {
                        getEngine().removeEntity(collidedWithEntity);
                        blockPublisher.removeBlock(blockComponent.getBlockName());
                    }
                }
            } else {
                LOG.debug("Local projectile collided with a block, ignoring, will be picked up by remote if it was remote block");
            }

            getEngine().removeEntity(entity);
            return;
        }

        // If collided with local player, consume bullet and decrease health
        if (collidedWithType == Type.PLAYER && projectileComponent.isRemote()) {

            LOG.debug("Bullet hit me, notifying owner and decreasing my health");
            getEngine().removeEntity(entity);

            projectileSubscriber.interactWithProjectile(projectileComponent.getProjectileName());
            StatusComponent statusComponent = STATUS_MAPPER.get(collidedWithEntity);
            statusComponent.setStatus(statusComponent.getStatus().toBuilder()
                    .setHealth(statusComponent.getStatus().getHealth() - 1)
                    .build());
        }
    }
}
