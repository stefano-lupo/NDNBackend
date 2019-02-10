package com.stefanolupo.ndngame.libgdx.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.physics.box2d.Body;
import com.google.inject.Inject;
import com.stefanolupo.ndngame.backend.chronosynced.PlayerStatusManager;
import com.stefanolupo.ndngame.libgdx.components.MotionStateComponent;
import com.stefanolupo.ndngame.libgdx.components.RemotePlayerComponent;
import com.stefanolupo.ndngame.names.PlayerStatusName;
import com.stefanolupo.ndngame.protos.PlayerStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemotePlayerUpdateSystem
        extends IteratingSystem
        implements HasComponentMappers {

    private static final Logger LOG = LoggerFactory.getLogger(RemotePlayerUpdateSystem.class);

    private static long numberOfRemoteUpdates = 0;
    private static long numberOfNonUpdates = 0;

    private final PlayerStatusManager playerStatusManager;

    @Inject
    public RemotePlayerUpdateSystem(PlayerStatusManager playerStatusManager) {
        super(Family.all(RemotePlayerComponent.class).get());
        this.playerStatusManager = playerStatusManager;
//        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(this::logStats, 10, 5, TimeUnit.SECONDS);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Body body = BODY_MAPPER.get(entity).getBody();
        MotionStateComponent motionStateComponent = MOTION_STATE_MAPPER.get(entity);
        RemotePlayerComponent remotePlayerComponent = REMOTE_PLAYER_MAPPER.get(entity);

        PlayerStatusName playerStatusName = remotePlayerComponent.getPlayerStatusName();

        long latestVersionForPlayer = playerStatusManager.getLatestVersionForPlayer(playerStatusName);

        if (latestVersionForPlayer <= remotePlayerComponent.getLatestVersionSeen()) {
            numberOfNonUpdates++;
            return;
        }

        PlayerStatus latestStatus = playerStatusManager.getLatestStatus(playerStatusName);

        // Update the motion state component for this entity according to latest status
        motionStateComponent.updateState(latestStatus.getVelX(), latestStatus.getVelY(), deltaTime);

        // Rectify discrepancy in calculated vs actual position since last status update
        body.setTransform(latestStatus.getX(), latestStatus.getY(), body.getAngle());
        remotePlayerComponent.setLatestVersionSeen(latestVersionForPlayer);

        numberOfRemoteUpdates++;
    }

    private void logStats() {
        LOG.debug("{} remote updates, {} non updates = {}%",
                numberOfRemoteUpdates, numberOfNonUpdates, (numberOfRemoteUpdates) * 100 / (numberOfRemoteUpdates + numberOfNonUpdates + 0f));
    }
}
