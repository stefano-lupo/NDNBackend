package com.stefanolupo.ndngame.libgdx.converters;

import com.badlogic.ashley.core.Entity;
import com.stefanolupo.ndngame.libgdx.components.RemotePlayerComponent;
import com.stefanolupo.ndngame.libgdx.components.StateComponent;
import com.stefanolupo.ndngame.libgdx.components.StatusComponent;
import com.stefanolupo.ndngame.libgdx.systems.HasComponentMappers;
import com.stefanolupo.ndngame.protos.GameObject;
import com.stefanolupo.ndngame.protos.PlayerStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Maps to and from PlayerStatusFace Proto and Game engine entity
 */
public class PlayerStatusConverter implements HasComponentMappers {

    private static final Logger LOG = LoggerFactory.getLogger(PlayerStatusConverter.class);

    /**
     * Builds Block based on current state of entity
     */
    public static PlayerStatus protoFromEntity(Entity entity) {
        StatusComponent statusComponent = STATUS_MAPPER.get(entity);
        return PlayerStatus.newBuilder()
                .setGameObject(GameObjectConverter.protoFromEntity(entity))
                .setStatus(statusComponent.getStatus())
                .build();
    }

    /**
     * Update the entity's state based on the remote playerStatus
     * The PhysicsSystem updates the render component based on the Body state
     * so only need to update the body
     */
    public static void reconcileRemotePlayer(Entity entity,
                                             PlayerStatus playerStatus,
                                             long latestVersionForPlayer,
                                             float deltaTime) {
        GameObjectConverter.reconcileGameObject(entity, playerStatus.getGameObject());

        RemotePlayerComponent remotePlayerComponent = REMOTE_PLAYER_MAPPER.get(entity);
        StateComponent stateComponent = STATE_MAPPER.get(entity);
        StatusComponent statusComponent = STATUS_MAPPER.get(entity);

        statusComponent.setStatus(playerStatus.getStatus());

        GameObject gameObject = playerStatus.getGameObject();

        // Update the motion state component for this entity according to latest status
        stateComponent.updateMotionState(gameObject.getVelX(), gameObject.getVelY(), deltaTime);
        remotePlayerComponent.setLatestVersionSeen(latestVersionForPlayer);
    }
}
