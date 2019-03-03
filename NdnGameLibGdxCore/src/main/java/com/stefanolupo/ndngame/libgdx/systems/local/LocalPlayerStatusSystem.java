package com.stefanolupo.ndngame.libgdx.systems.local;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalSystem;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.hubspot.liveconfig.value.Value;
import com.stefanolupo.ndngame.backend.annotations.LogScheduleExecutor;
import com.stefanolupo.ndngame.backend.publisher.PlayerStatusPublisher;
import com.stefanolupo.ndngame.config.LocalConfig;
import com.stefanolupo.ndngame.libgdx.components.LocalPlayerComponent;
import com.stefanolupo.ndngame.libgdx.converters.PlayerStatusConverter;
import com.stefanolupo.ndngame.libgdx.systems.HasComponentMappers;
import com.stefanolupo.ndngame.protos.GameObject;
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

    private long nullUpdates = 0;
    private long velUpdates = 0;
    private long deadReckoningUpdates = 0;
    private long deadReckoningNonUpdates = 0;

    private final PlayerStatusPublisher playerStatusPublisher;
    private final float ticksPerMs;
    private final Value<Boolean> useDeadReckoning;
    private final Value<Float> maxDeadReckoningError;

    @Inject
    public LocalPlayerStatusSystem(PlayerStatusPublisher playerStatusPublisher,
                                   LocalConfig localConfig,
                                   @LogScheduleExecutor ScheduledExecutorService executorService,
                                   @Named("local.player.status.use.dead.reckoning") Value<Boolean> useDeadReckoning,
                                   @Named("local.player.dead.reckoning.max.error") Value<Float> maxDeadReckoningError,
                                   @Named("local.player.status.update.interval.ms") Value<Float> updateInterval) {
        super(updateInterval.get() / 1000f);
        this.playerStatusPublisher = playerStatusPublisher;
        this.ticksPerMs = localConfig.getTargetFrameRate() / 1000f;
        this.useDeadReckoning = useDeadReckoning;
        this.maxDeadReckoningError = maxDeadReckoningError;
        executorService.scheduleAtFixedRate(this::logStats, 0, 10, TimeUnit.SECONDS);
    }

    @Override
    protected void updateInterval() {
        Entity playerEntity = getEngine().getEntitiesFor(Family.all(LocalPlayerComponent.class).get()).get(0);
        GameObject playerCurrentGameObject = RENDER_MAPPER.get(playerEntity).getGameObject();

        if (!useDeadReckoning.get()) {
            playerStatusPublisher.updateLocalPlayerStatus(PlayerStatusConverter.protoFromEntity(playerEntity));
            return;
        }

        List<PlayerStatusPublisher.PlayerStatusWithTime> list = playerStatusPublisher.getPlayerStatusesForOutstandingInterests();
        for (PlayerStatusPublisher.PlayerStatusWithTime withTime : list) {

            if (withTime == null) {
                updateLocalPlayer(playerEntity);
                nullUpdates++;
                return;
            }

            GameObject remoteVersion = withTime.playerStatus.getGameObject();
            if (remoteVersion.getVelX() != playerCurrentGameObject.getVelX() ||
                remoteVersion.getVelY() != playerCurrentGameObject.getVelY()) {
                updateLocalPlayer(playerEntity);
                velUpdates++;
                continue;
            }

            long delta = System.currentTimeMillis() - withTime.timeStamp;
            float ellapsedTicks = delta * ticksPerMs;
            float approxX = remoteVersion.getX() + ellapsedTicks*remoteVersion.getVelX();
            float approxY = remoteVersion.getY() + ellapsedTicks*remoteVersion.getVelY();
            double distanceBetween = distanceBetween(
                    playerCurrentGameObject.getX(), playerCurrentGameObject.getY(),
                    approxX, approxY);

            if (distanceBetween > maxDeadReckoningError.get()) {
                deadReckoningUpdates++;
                playerStatusPublisher.updateLocalPlayerStatus(PlayerStatusConverter.protoFromEntity(playerEntity));
            } else {
                deadReckoningNonUpdates++;
            }
        }
    }

    private void updateLocalPlayer(Entity playerEntity) {
        playerStatusPublisher.updateLocalPlayerStatus(PlayerStatusConverter.protoFromEntity(playerEntity));
    }

    private void logStats() {
        long total = nullUpdates + velUpdates + deadReckoningUpdates + deadReckoningNonUpdates;
        LOG.debug("Null: {}, vel: {}, deadReckoning: {}, deadReckoningSkip: {}, actionable: {}",
                nullUpdates, velUpdates, deadReckoningUpdates, deadReckoningNonUpdates,
                (total - deadReckoningNonUpdates + 0f) / total);
    }

    private static double distanceBetween(float x1, float y1, float x2, float y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y2 - y1, 2));
    }
}
