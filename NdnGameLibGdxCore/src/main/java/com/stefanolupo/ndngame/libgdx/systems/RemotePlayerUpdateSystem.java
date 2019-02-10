package com.stefanolupo.ndngame.libgdx.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.physics.box2d.Body;
import com.google.inject.Inject;
import com.stefanolupo.ndngame.backend.chronosynced.PlayerStatusManager;
import com.stefanolupo.ndngame.libgdx.components.RemotePlayerComponent;
import com.stefanolupo.ndngame.names.PlayerStatusName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemotePlayerUpdateSystem
        extends IteratingSystem
        implements HasComponentMappers {

    private static final Logger LOG = LoggerFactory.getLogger(RemotePlayerUpdateSystem.class);

    private final PlayerStatusManager playerStatusManager;

    @Inject
    public RemotePlayerUpdateSystem(PlayerStatusManager playerStatusManager) {
        super(Family.all(RemotePlayerComponent.class).get());
        this.playerStatusManager = playerStatusManager;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Body body = BODY_MAPPER.get(entity).getBody();
        RemotePlayerComponent remotePlayerComponent = REMOTE_PLAYER_MAPPER.get(entity);
        PlayerStatusName playerStatusName = remotePlayerComponent.getPlayerStatusName();
        long latestVersion = playerStatusManager.getLatestVersionNumber(playerStatusName);
        if (latestVersion > remotePlayerComponent.getLatestVersionSeen()) {
            // Resolve differences
            LOG.debug("Need to resolve differences for {}", playerStatusName.getPlayerName());
//            RemotePlayer remotePlayer = playerStatusManager.getLatestVersionOfRemotePlayer(playerStatusName);
            remotePlayerComponent.setLatestVersionSeen(latestVersion);
        }


    }
}
