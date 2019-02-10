package com.stefanolupo.ndngame.libgdx.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.physics.box2d.Body;
import com.google.inject.Inject;
import com.stefanolupo.ndngame.backend.chronosynced.PlayerStatusManager;
import com.stefanolupo.ndngame.libgdx.components.LocalPlayerComponent;
import com.stefanolupo.ndngame.protos.PlayerStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class LocalPlayerStatusSystem
        extends IteratingSystem
        implements HasComponentMappers {

    private static final Logger LOG = LoggerFactory.getLogger(LocalPlayerStatusSystem.class);

    private Entity entity;
    private final PlayerStatusManager playerStatusManager;

    @Inject
    public LocalPlayerStatusSystem(PlayerStatusManager playerStatusManager) {
        super(Family.all(LocalPlayerComponent.class).get());
        this.playerStatusManager = playerStatusManager;
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(this::logPosition, 5, 5, TimeUnit.SECONDS);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Body body = BODY_MAPPER.get(entity).getBody();
        PlayerStatus newPlayerStatus = PlayerStatus.newBuilder()
                .setX(body.getPosition().x)
                .setY(body.getPosition().y)
                .setVelX(body.getLinearVelocity().x)
                .setVelY(body.getLinearVelocity().y)
                .build();
        playerStatusManager.updateLocalPlayerStatus(newPlayerStatus);
        this.entity = entity;
    }

    private void logPosition() {
        Body b = BODY_MAPPER.get(entity).getBody();
        LOG.debug("Local: x: {}, y: {}, velX: {}, velY: {}", b.getPosition().x, b.getPosition().y, b.getLinearVelocity().x, b.getLinearVelocity().y);
    }
}
