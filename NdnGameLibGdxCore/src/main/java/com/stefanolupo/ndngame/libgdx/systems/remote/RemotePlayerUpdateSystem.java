package com.stefanolupo.ndngame.libgdx.systems.remote;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.physics.box2d.Body;
import com.google.inject.Inject;
import com.stefanolupo.ndngame.backend.subscriber.PlayerStatusSubscriber;
import com.stefanolupo.ndngame.libgdx.components.RemotePlayerComponent;
import com.stefanolupo.ndngame.libgdx.components.StateComponent;
import com.stefanolupo.ndngame.libgdx.systems.HasComponentMappers;
import com.stefanolupo.ndngame.names.PlayerStatusName;
import com.stefanolupo.ndngame.protos.PlayerStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles updates from remote players
 * Note if this class gets out of hand it would make a lot of sense to split
 * out into RemotePlayerMovementSystem, RemotePlayerAttackSystem etc
 */
public class RemotePlayerUpdateSystem
        extends IteratingSystem
        implements HasComponentMappers {

    private static final Logger LOG = LoggerFactory.getLogger(RemotePlayerUpdateSystem.class);

    private static long numberOfRemoteUpdates = 0;
    private static long numberOfNonUpdates = 0;

    private final PlayerStatusSubscriber playerStatusSubscriber;
//    private final AttackManager attackManager;
    private final PooledEngine pooledEngine;

    @Inject
    public RemotePlayerUpdateSystem(PlayerStatusSubscriber playerStatusSubscriber,
//                                    AttackManager attackManager,
                                    PooledEngine pooledEngine) {
        super(Family.all(RemotePlayerComponent.class).get());
        this.playerStatusSubscriber = playerStatusSubscriber;
//        this.attackManager = attackManager;
        this.pooledEngine = pooledEngine;

//        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(this::logStats, 10, 5, TimeUnit.SECONDS);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Body body = BODY_MAPPER.get(entity).getBody();
        StateComponent stateComponent = STATE_MAPPER.get(entity);
        RemotePlayerComponent remotePlayerComponent = REMOTE_PLAYER_MAPPER.get(entity);

        handleStatusUpdate(remotePlayerComponent, stateComponent, body, deltaTime);
        handleAttackUpdate(remotePlayerComponent);

        numberOfRemoteUpdates++;
    }

    private void handleStatusUpdate(RemotePlayerComponent remotePlayerComponent,
                                    StateComponent stateComponent,
                                    Body body,
                                    float deltaTime) {
        PlayerStatusName playerStatusName = remotePlayerComponent.getPlayerStatusName();

        long latestVersionForPlayer = playerStatusSubscriber.getLatestVersionForPlayer(playerStatusName);

        if (latestVersionForPlayer <= remotePlayerComponent.getLatestVersionSeen()) {
            numberOfNonUpdates++;
            return;
        }

        PlayerStatus latestStatus = playerStatusSubscriber.getLatestStatusForPlayer(playerStatusName);


        // Update the motion state component for this entity according to latest status
        stateComponent.updateMotionState(latestStatus.getVelX(), latestStatus.getVelY(), deltaTime);

        // Rectify discrepancy in calculated vs actual position since last status update
        body.setTransform(latestStatus.getX(), latestStatus.getY(), body.getAngle());
        remotePlayerComponent.setLatestVersionSeen(latestVersionForPlayer);
    }

    private void handleAttackUpdate(RemotePlayerComponent remotePlayerComponent) {
//        List<Attack> unprocessedAttacks = attackManager.getUnprocessedAttacks(remotePlayerComponent.getAttackName());
//
//        if (unprocessedAttacks.size() == 0) {
//            return;
//        }
//
//        // TODO: Should probably run all these through the backend but meh
//        if (unprocessedAttacks.size() > 1) {
//            LOG.error("Had more than one unprocessed attack for {}", remotePlayerComponent);
//        }
//
//        Attack attack = unprocessedAttacks.get(0);
//        Entity entity = pooledEngine.createEntity();
//        AttackComponent attackComponent = pooledEngine.createComponent(AttackComponent.class);
//        attackComponent.setAttackName(remotePlayerComponent.getAttackName());
//        attackComponent.setAttack(attack);
//        entity.add(attackComponent);
    }

    private void logStats() {
        LOG.debug("{} remote updates, {} non updates = {}%",
                numberOfRemoteUpdates, numberOfNonUpdates, (numberOfRemoteUpdates) * 100 / (numberOfRemoteUpdates + numberOfNonUpdates + 0f));
    }
}
