package com.stefanolupo.ndngame.libgdx.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.physics.box2d.Body;
import com.google.inject.Inject;
import com.stefanolupo.ndngame.backend.publisher.PlayerStatusPublisher;
import com.stefanolupo.ndngame.libgdx.components.LocalPlayerComponent;
import com.stefanolupo.ndngame.protos.PlayerStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalPlayerStatusSystem
        extends IteratingSystem
        implements HasComponentMappers {

    private static final Logger LOG = LoggerFactory.getLogger(LocalPlayerStatusSystem.class);

//    private final PlayerStatusManager playerStatusManager;
    private final PlayerStatusPublisher playerStatusPublisher;

    @Inject
    public LocalPlayerStatusSystem(/*PlayerStatusManager playerStatusManager,*/
                                   PlayerStatusPublisher playerStatusPublisher) {
        super(Family.all(LocalPlayerComponent.class).get());
//        this.playerStatusManager = playerStatusManager;
        this.playerStatusPublisher = playerStatusPublisher;
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
//        playerStatusManager.updateLocalPlayerStatus(newPlayerStatus);
        playerStatusPublisher.updateEntity(newPlayerStatus);
    }
}
