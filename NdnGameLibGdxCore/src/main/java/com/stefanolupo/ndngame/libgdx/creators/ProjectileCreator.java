package com.stefanolupo.ndngame.libgdx.creators;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.physics.box2d.Body;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.stefanolupo.ndngame.backend.publisher.ProjectilePublisher;
import com.stefanolupo.ndngame.config.LocalConfig;
import com.stefanolupo.ndngame.libgdx.components.CollisionComponent;
import com.stefanolupo.ndngame.libgdx.components.ProjectileComponent;
import com.stefanolupo.ndngame.libgdx.components.RenderComponent;
import com.stefanolupo.ndngame.libgdx.components.TypeComponent;
import com.stefanolupo.ndngame.libgdx.components.enums.Type;
import com.stefanolupo.ndngame.names.projectiles.ProjectileName;
import com.stefanolupo.ndngame.protos.GameObject;
import com.stefanolupo.ndngame.protos.Projectile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import static com.stefanolupo.ndngame.libgdx.creators.PlayerCreator.PLAYER_HEIGHT;
import static com.stefanolupo.ndngame.libgdx.creators.PlayerCreator.PLAYER_WIDTH;

@Singleton
public class ProjectileCreator {

    public static final float PROJECTILE_RADIUS = 0.15f;
    public static final float PROJECTILE_VELOCITY = 0.2f;

    private static final Logger LOG = LoggerFactory.getLogger(ProjectileCreator.class);

    private final LocalConfig localConfig;
    private final BodyFactory bodyFactory;
    private final PooledEngine engine;
    private final ProjectilePublisher projectilePublisher;
    private final EntityManager entityManager;

    @Inject
    public ProjectileCreator(LocalConfig localConfig,
                             BodyFactory bodyFactory,
                             PooledEngine engine,
                             ProjectilePublisher projectilePublisher,
                             EntityManager entityManager) {
        this.localConfig = localConfig;
        this.bodyFactory = bodyFactory;
        this.engine = engine;
        this.projectilePublisher = projectilePublisher;
        this.entityManager = entityManager;
    }

    public void createLocalProjectile(float x, float y, float targetX, float targetY) {
        String id = UUID.randomUUID().toString();
        GameObject gameObject = buildGameObject(x, y, targetX, targetY);
        Projectile projectile = Projectile.newBuilder()
                .setId(id)
                .setGameObject(gameObject)
                .setDamage(1)
                .build();
//        LOG.debug("Creating local projectile");
        ProjectileName projectileName = new ProjectileName(localConfig.getGameId(), localConfig.getPlayerName(), id);
        createProjectileEntity(projectileName, projectile, false);
        projectilePublisher.insertProjectile(projectileName, projectile);
    }

    public void createLocalProjectileExplosion(float x, float y) {
        createLocalProjectile(x, y, x+1, y);
        createLocalProjectile(x, y, x-1, y);
        createLocalProjectile(x, y, x, y+1);
        createLocalProjectile(x, y, x, y-1);
    }

    public void createRemoteProjectile(ProjectileName projectileName, Projectile projectile) {
//        LOG.debug("Creating remote projectile");
        createProjectileEntity(projectileName, projectile, true);
    }

    private void createProjectileEntity(ProjectileName projectileName, Projectile projectile, boolean isRemote) {
        GameObject gameObject = projectile.getGameObject();

        Set<Component> components = new HashSet<>();

        BodyCreationRequest bodyCreationRequest = bodyFactory.circleBody(
                gameObject.getX(), gameObject.getY(),
                gameObject.getWidth(),
                Material.PROJECTILE, false);

        Consumer<Body> bodyCreationCallback = b ->
            b.applyLinearImpulse(gameObject.getVelX(), gameObject.getVelY(), gameObject.getX(), gameObject.getY(), true);

        TypeComponent typeComponent = engine.createComponent(TypeComponent.class);
        typeComponent.setType(Type.PROJECTILE);
        components.add(typeComponent);

        // TODO: Texture

        RenderComponent renderComponent = engine.createComponent(RenderComponent.class);
        renderComponent.setGameObject(gameObject);
        components.add(renderComponent);

        ProjectileComponent projectileComponent = engine.createComponent(ProjectileComponent.class);
        projectileComponent.setProjectileName(projectileName);
        projectileComponent.setDamage(projectile.getDamage());
        projectileComponent.setRemote(isRemote);
        components.add(projectileComponent);

        CollisionComponent collisionComponent = engine.createComponent(CollisionComponent.class);
        components.add(collisionComponent);

        entityManager.addEntityCreationRequest(new EntityCreationRequest(components, bodyCreationRequest, bodyCreationCallback));
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
