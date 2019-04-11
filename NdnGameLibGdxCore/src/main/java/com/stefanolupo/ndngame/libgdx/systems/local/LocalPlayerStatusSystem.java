package com.stefanolupo.ndngame.libgdx.systems.local;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalSystem;
import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.hubspot.liveconfig.value.Value;
import com.stefanolupo.ndngame.backend.annotations.BackendMetrics;
import com.stefanolupo.ndngame.backend.annotations.LogScheduleExecutor;
import com.stefanolupo.ndngame.backend.publisher.PlayerStatusPublisher;
import com.stefanolupo.ndngame.config.LocalConfig;
import com.stefanolupo.ndngame.libgdx.components.LocalPlayerComponent;
import com.stefanolupo.ndngame.libgdx.converters.PlayerStatusConverter;
import com.stefanolupo.ndngame.libgdx.systems.HasComponentMappers;
import com.stefanolupo.ndngame.metrics.MetricNames;
import com.stefanolupo.ndngame.protos.GameObject;
import com.stefanolupo.ndngame.util.MathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Pushes updates to Player Status from engine to PlayerStatusPublisher
 * Also uses Dead Reckoning to limit player updates
 */
public class LocalPlayerStatusSystem
        extends IntervalSystem
        implements HasComponentMappers {

    private static final Logger LOG = LoggerFactory.getLogger(LocalPlayerStatusSystem.class);

    private final Counter nullCounter;
    private final Counter velCounter;
    private final Counter thresholdCounter;
    private final Counter skipCounter;

    private final PlayerStatusPublisher playerStatusPublisher;
    private final float ticksPerMs;
    private final Value<Boolean> useDeadReckoning;
    private final Value<Float> maxDeadReckoningError;

    @Inject
    public LocalPlayerStatusSystem(PlayerStatusPublisher playerStatusPublisher,
                                   LocalConfig localConfig,
                                   @BackendMetrics MetricRegistry metrics,
                                   @LogScheduleExecutor ScheduledExecutorService executorService,
                                   @Named("local.player.status.use.dead.reckoning") Value<Boolean> useDeadReckoning,
                                   @Named("local.player.dead.reckoning.max.error") Value<Float> maxDeadReckoningError,
                                   @Named("local.player.status.updates.per.sec") Value<Float> updatesPerSecond) {
        super(1 / updatesPerSecond.get());
        this.playerStatusPublisher = playerStatusPublisher;
        this.ticksPerMs = localConfig.getTargetFrameRate() / 1000f;
        this.useDeadReckoning = useDeadReckoning;
        this.maxDeadReckoningError = maxDeadReckoningError;

        this.nullCounter = metrics.counter(MetricNames.deadReckoningCounter(MetricNames.DeadReckoningCounters.NULL));
        this.velCounter = metrics.counter(MetricNames.deadReckoningCounter(MetricNames.DeadReckoningCounters.VELOCITY));
        this.thresholdCounter = metrics.counter(MetricNames.deadReckoningCounter(MetricNames.DeadReckoningCounters.THRESHOLD));
        this.skipCounter = metrics.counter(MetricNames.deadReckoningCounter(MetricNames.DeadReckoningCounters.SKIP));

        if (useDeadReckoning.get()) {
            executorService.scheduleAtFixedRate(this::logStats, 0, 10, TimeUnit.SECONDS);
        }
    }

    @Override
    protected void updateInterval() {
        Entity playerEntity = getEngine().getEntitiesFor(Family.all(LocalPlayerComponent.class).get()).get(0);

        if (!useDeadReckoning.get()) {
            playerStatusPublisher.updateLocalPlayerStatus(PlayerStatusConverter.protoFromEntity(playerEntity));
            return;
        }

        deadReckoningUpdate(playerEntity);
    }

    /**
     * Determine whether or not to publish a player update based on the deadreckoned position of the
     * local player on subscribers' machines
     *
     *  This currently just produces a player update for everyone to consume or doesn't produce one at all
     *  Possible enhancement is to do it on a per player basis
     */
    private void deadReckoningUpdate(Entity playerEntity) {
        GameObject playerCurrentGameObject = RENDER_MAPPER.get(playerEntity).getGameObject();
        List<PlayerStatusPublisher.PlayerStatusWithTime> statusForCurrentSeqNoOfInterest =
                playerStatusPublisher.getPlayerStatusesForOutstandingInterests();
        for (PlayerStatusPublisher.PlayerStatusWithTime statusWithTime : statusForCurrentSeqNoOfInterest) {

            // If we don't have this sequence number version cached any more
            // Always produce an update
            // TODO: This means a laggy client requesting old SN which are no longer cached
            // will cause everyone to get more updates
            if (statusWithTime == null) {
                updateLocalPlayer(playerEntity);
                nullCounter.inc();
                return;
            }

            // On changing velocity just push out an update
            // Currently NOT lerping velocities, so this is basically a direction change
            GameObject remoteVersion = statusWithTime.playerStatus.getGameObject();
            if (remoteVersion.getVelX() != playerCurrentGameObject.getVelX() ||
                    remoteVersion.getVelY() != playerCurrentGameObject.getVelY()) {
                updateLocalPlayer(playerEntity);
                velCounter.inc();
                return;
            }

            // Finally, compute the approx dead reckoned position and publish update if its over the threshold
            double distanceBetween = computeDeadReckoningDistance(statusWithTime, playerCurrentGameObject);
            if (distanceBetween > maxDeadReckoningError.get()) {
                thresholdCounter.inc();
                playerStatusPublisher.updateLocalPlayerStatus(PlayerStatusConverter.protoFromEntity(playerEntity));
            } else {
                skipCounter.inc();
            }
        }
    }

    private double computeDeadReckoningDistance(PlayerStatusPublisher.PlayerStatusWithTime statusWithTime,
                                                GameObject playerCurrentGameObject) {
        GameObject remoteVersion = statusWithTime.playerStatus.getGameObject();
        long delta = System.currentTimeMillis() - statusWithTime.timeStamp;
        float ellapsedTicks = delta * ticksPerMs;
        float approxX = remoteVersion.getX() + ellapsedTicks*remoteVersion.getVelX();
        float approxY = remoteVersion.getY() + ellapsedTicks*remoteVersion.getVelY();
        return MathUtils.distanceBetween(
                playerCurrentGameObject.getX(), playerCurrentGameObject.getY(),
                approxX, approxY);
    }

    private void updateLocalPlayer(Entity playerEntity) {
        playerStatusPublisher.updateLocalPlayerStatus(PlayerStatusConverter.protoFromEntity(playerEntity));
    }

    private void logStats() {
        long nullCount = nullCounter.getCount();
        long velCount = velCounter.getCount();
        long thresholdCount = thresholdCounter.getCount();
        long skipCount = skipCounter.getCount();
        long total = nullCount + velCount + thresholdCount + skipCount;
        LOG.debug("Null: {}, vel: {}, deadReckoning: {}, deadReckoningSkip: {}, actionable: {}",
                nullCount, velCount, thresholdCount, skipCount,
                (total - skipCount + 0f) / total);
    }


}
