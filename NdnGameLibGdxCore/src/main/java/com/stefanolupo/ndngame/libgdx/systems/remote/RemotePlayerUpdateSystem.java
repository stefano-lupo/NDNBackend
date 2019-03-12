package com.stefanolupo.ndngame.libgdx.systems.remote;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import com.google.inject.Inject;
import com.stefanolupo.ndngame.backend.annotations.BackendMetrics;
import com.stefanolupo.ndngame.backend.subscriber.PlayerStatusSubscriber;
import com.stefanolupo.ndngame.libgdx.components.RemotePlayerComponent;
import com.stefanolupo.ndngame.libgdx.converters.PlayerStatusConverter;
import com.stefanolupo.ndngame.libgdx.systems.HasComponentMappers;
import com.stefanolupo.ndngame.metrics.MetricNames;
import com.stefanolupo.ndngame.names.PlayerStatusName;
import com.stefanolupo.ndngame.protos.GameObject;
import com.stefanolupo.ndngame.protos.PlayerStatus;
import com.stefanolupo.ndngame.util.MathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
    private final MetricRegistry metrics;
    private final Map<PlayerStatusName, Histogram> playerStatusHistograms = new HashMap<>();

    @Inject
    public RemotePlayerUpdateSystem(PlayerStatusSubscriber playerStatusSubscriber,
                                    @BackendMetrics MetricRegistry metrics) {
        super(Family.all(RemotePlayerComponent.class).get());
        this.playerStatusSubscriber = playerStatusSubscriber;
        this.metrics = metrics;
//        runLogStats();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

        handleStatusUpdate(entity, deltaTime);
        numberOfRemoteUpdates++;
    }

    private void handleStatusUpdate(Entity entity,
                                    float deltaTime) {
        RemotePlayerComponent remotePlayerComponent = REMOTE_PLAYER_MAPPER.get(entity);

        PlayerStatusName playerStatusName = remotePlayerComponent.getPlayerStatusName();
        long latestVersionForPlayer = playerStatusSubscriber.getLatestVersionForPlayer(playerStatusName);

        if (latestVersionForPlayer <= remotePlayerComponent.getLatestVersionSeen()) {
            numberOfNonUpdates++;
            return;
        }

        PlayerStatus latestStatus = playerStatusSubscriber.getLatestStatusForPlayer(playerStatusName);
        capturePositionDeltaMetrics(playerStatusName, latestStatus, entity);
        PlayerStatusConverter.reconcileRemotePlayer(entity, latestStatus, latestVersionForPlayer, deltaTime);
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

    private void runLogStats() {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(
                this::logStats,
                0,
                20,
                TimeUnit.SECONDS
        );
    }

    private void capturePositionDeltaMetrics(PlayerStatusName name, PlayerStatus status, Entity entity) {
        GameObject engineObject = RENDER_MAPPER.get(entity).getGameObject();
        GameObject remoteObject = status.getGameObject();

        Histogram playerStatusHistogram = playerStatusHistograms.get(name);
        if (playerStatusHistogram == null) {
            playerStatusHistogram = metrics.histogram(MetricNames.playerStatusPositionDeltas(name));
            playerStatusHistograms.put(name, playerStatusHistogram);

            // Don't capture distance on first occurence as it will skew results
            return;
        }

        long distanceBetweenInHundreths = Math.round(MathUtils.distanceBetween(engineObject, remoteObject) * 100);
        playerStatusHistogram.update(distanceBetweenInHundreths);
    }

    private void logStats() {
        LOG.debug("{} remote updates, {} non updates = {}%",
                numberOfRemoteUpdates, numberOfNonUpdates,
                (numberOfRemoteUpdates) * 100 / (numberOfRemoteUpdates + numberOfNonUpdates + 0f));
    }
}
