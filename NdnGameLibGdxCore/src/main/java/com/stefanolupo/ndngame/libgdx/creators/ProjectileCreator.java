package com.stefanolupo.ndngame.libgdx.creators;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.physics.box2d.Body;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.stefanolupo.ndngame.backend.publisher.ProjectilePublisher;
import com.stefanolupo.ndngame.config.LocalConfig;
import com.stefanolupo.ndngame.libgdx.components.*;
import com.stefanolupo.ndngame.libgdx.components.enums.Type;
import com.stefanolupo.ndngame.names.projectiles.ProjectileName;
import com.stefanolupo.ndngame.protos.GameObject;
import com.stefanolupo.ndngame.protos.Projectile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

import static com.stefanolupo.ndngame.libgdx.creators.PlayerCreator.PLAYER_HEIGHT;
import static com.stefanolupo.ndngame.libgdx.creators.PlayerCreator.PLAYER_WIDTH;

@Singleton
public class ProjectileCreator {

    public static final float PROJECTILE_RADIUS = 0.05f;
    public static final float PROJECTILE_VELOCITY = 0.1f;

    private static final Logger LOG = LoggerFactory.getLogger(ProjectileCreator.class);

    private final LocalConfig localConfig;
    private final BodyFactory bodyFactory;
    private final PooledEngine engine;
    private final ProjectilePublisher projectilePublisher;

    @Inject
    public ProjectileCreator(LocalConfig localConfig,
                             BodyFactory bodyFactory,
                             PooledEngine engine,
                             ProjectilePublisher projectilePublisher) {
        this.localConfig = localConfig;
        this.bodyFactory = bodyFactory;
        this.engine = engine;
        this.projectilePublisher = projectilePublisher;
    }

    public void createLocalProjectile(float x, float y, float targetX, float targetY) {
        String id = UUID.randomUUID().toString();
        GameObject gameObject = buildGameObject(x, y, targetX, targetY);
        Projectile projectile = Projectile.newBuilder()
                .setId(id)
                .setGameObject(gameObject)
                .setDamage(1)
                .build();
        LOG.debug("Creating local projectile");
        ProjectileName projectileName = new ProjectileName(localConfig.getGameId(), localConfig.getPlayerName(), id);
        createProjectileEntity(projectileName, projectile, false);
        projectilePublisher.insertProjectile(projectileName, projectile);
    }

    public void createRemoteProjectile(ProjectileName projectileName, Projectile projectile) {
        LOG.debug("Creating remote projectile");
        createProjectileEntity(projectileName, projectile, true);
    }

    private void createProjectileEntity(ProjectileName projectileName, Projectile projectile, boolean isRemote) {
        GameObject gameObject = projectile.getGameObject();
        Entity entity = engine.createEntity();

        Body body = bodyFactory.makeCircleBody(
                gameObject.getX(), gameObject.getY(),
                gameObject.getWidth(),
                Material.PROJECTILE, false);


        LOG.debug("Applying linear impulse: {} {}", gameObject.getVelX(), gameObject.getVelY());
        body.applyLinearImpulse(gameObject.getVelX(), gameObject.getVelY(), gameObject.getX(), gameObject.getY(), true);

        body.setUserData(entity);

        BodyComponent bodyComponent = engine.createComponent(BodyComponent.class);
        bodyComponent.setBody(body);
        entity.add(bodyComponent);

        TypeComponent typeComponent = engine.createComponent(TypeComponent.class);
        typeComponent.setType(Type.PROJECTILE);
        entity.add(typeComponent);

        // TODO: Texture


        RenderComponent renderComponent = engine.createComponent(RenderComponent.class);
        renderComponent.setGameObject(gameObject);
        entity.add(renderComponent);

        ProjectileComponent projectileComponent = engine.createComponent(ProjectileComponent.class);
        projectileComponent.setProjectileName(projectileName);
        projectileComponent.setDamage(projectile.getDamage());
        projectileComponent.setRemote(isRemote);
        entity.add(projectileComponent);

        CollisionComponent collisionComponent = engine.createComponent(CollisionComponent.class);
        entity.add(collisionComponent);

        engine.addEntity(entity);
    }

    private GameObject buildGameObject(float x, float y, float targetX, float targetY) {
        double angle = Math.atan2(targetY - y, targetX - x);
        float velX = PROJECTILE_VELOCITY * (float) Math.cos(angle);
        float velY = PROJECTILE_VELOCITY * (float) Math.sin(angle);

        x += Math.signum(velX) * (2*PLAYER_WIDTH + PROJECTILE_RADIUS);
        y += Math.signum(velY) * (2*PLAYER_HEIGHT + PROJECTILE_RADIUS);

        return GameObjectFactory.getBasicGameObjectBuilder(x, y, PROJECTILE_RADIUS)
                .setZ(1)
                .setVelX(velX)
                .setVelY(velY)
                .setIsFixedRotation(false)
                .build();
    }
}
