package com.stefanolupo.ndngame.libgdx.creators;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.stefanolupo.ndngame.libgdx.components.*;
import com.stefanolupo.ndngame.libgdx.components.enums.Type;
import com.stefanolupo.ndngame.protos.GameObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.stefanolupo.ndngame.libgdx.creators.PlayerCreator.PLAYER_HEIGHT;
import static com.stefanolupo.ndngame.libgdx.creators.PlayerCreator.PLAYER_WIDTH;

@Singleton
public class ProjectileCreator {

    public static final float PROJECTILE_RADIUS = .03f;
    public static final float PROJECTILE_VELOCITY = 6;

    private static final Logger LOG = LoggerFactory.getLogger(ProjectileCreator.class);

    private final BodyFactory bodyFactory;
    private final PooledEngine engine;

    @Inject
    public ProjectileCreator(BodyFactory bodyFactory,
                             PooledEngine engine) {
        this.bodyFactory = bodyFactory;
        this.engine = engine;
    }

    public void createProjectile(float x, float y, float targetX, float targetY) {
        Entity entity = engine.createEntity();

        double angle = Math.atan2(targetY - y, targetX - x);
        float velX = PROJECTILE_VELOCITY * (float) Math.cos(angle);
        float velY = PROJECTILE_VELOCITY * (float) Math.sin(angle);
        LOG.debug("Setting velocity: {}, {}", velX, velY);

        x += Math.signum(velX) * (PLAYER_WIDTH + PROJECTILE_RADIUS);
        y += Math.signum(velY) * (PLAYER_HEIGHT + PROJECTILE_RADIUS);

        Body body = bodyFactory.makeCirclePolyBody(x, y, PROJECTILE_RADIUS, BodyFactory.RUBBER, BodyDef.BodyType.DynamicBody, false);
        body.applyLinearImpulse(velX, velY, x, y, true);
        body.setUserData(entity);

        BodyComponent bodyComponent = engine.createComponent(BodyComponent.class);
        bodyComponent.setBody(body);
        entity.add(bodyComponent);

        TypeComponent typeComponent = engine.createComponent(TypeComponent.class);
        typeComponent.setType(Type.PROJECTILE);
        entity.add(typeComponent);

        // TODO: Texture


        RenderComponent renderComponent = engine.createComponent(RenderComponent.class);
        GameObject gameObject = GameObjectFactory.getBasicGameObjectBuilder(x, y, PROJECTILE_RADIUS)
                .setZ(1)
                .setVelX(velX)
                .setVelY(velY)
                .setIsFixedRotation(false)
                .build();

        renderComponent.setGameObject(gameObject);
        entity.add(renderComponent);

        ProjectileComponent projectileComponent = engine.createComponent(ProjectileComponent.class);
        // TODO: Populate
        entity.add(projectileComponent);

        CollisionComponent collisionComponent = engine.createComponent(CollisionComponent.class);
        entity.add(collisionComponent);

        engine.addEntity(entity);
    }
}
