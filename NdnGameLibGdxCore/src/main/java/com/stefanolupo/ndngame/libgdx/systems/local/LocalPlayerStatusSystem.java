package com.stefanolupo.ndngame.libgdx.systems.local;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalSystem;
import com.badlogic.gdx.physics.box2d.Body;
import com.google.inject.Inject;
import com.stefanolupo.ndngame.backend.publisher.PlayerStatusPublisher;
import com.stefanolupo.ndngame.libgdx.components.LocalPlayerComponent;
import com.stefanolupo.ndngame.libgdx.systems.HasComponentMappers;
import com.stefanolupo.ndngame.protos.PlayerStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Pushes updates to Player Status from engine to PlayerStatusPublisher
 */
public class LocalPlayerStatusSystem
        extends IntervalSystem
        implements HasComponentMappers {

    private static final Logger LOG = LoggerFactory.getLogger(LocalPlayerStatusSystem.class);
    private static final Float UPDATE_INTERVAL_SEC = 20f / 1000;

    private final PlayerStatusPublisher playerStatusPublisher;

    @Inject
    public LocalPlayerStatusSystem(PlayerStatusPublisher playerStatusPublisher) {
        super(UPDATE_INTERVAL_SEC);
        this.playerStatusPublisher = playerStatusPublisher;
    }

    @Override
    protected void updateInterval() {
        Entity playerEntity = getEngine().getEntitiesFor(Family.all(LocalPlayerComponent.class).get()).get(0);
        Body body = BODY_MAPPER.get(playerEntity).getBody();
        PlayerStatus newPlayerStatus = PlayerStatus.newBuilder()
                .setX(body.getPosition().x)
                .setY(body.getPosition().y)
                .setVelX(body.getLinearVelocity().x)
                .setVelY(body.getLinearVelocity().y)
                .build();
        playerStatusPublisher.updateLocalPlayerStatus(newPlayerStatus);
    }

}
