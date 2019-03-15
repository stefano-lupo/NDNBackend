package com.stefanolupo.ndngame.backend.subscriber;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.protobuf.InvalidProtocolBufferException;
import com.hubspot.liveconfig.value.Value;
import com.stefanolupo.ndngame.backend.LocalPlayerReference;
import com.stefanolupo.ndngame.backend.chronosynced.OnPlayersDiscovered;
import com.stefanolupo.ndngame.backend.filters.LinearInterestZoneFilter;
import com.stefanolupo.ndngame.backend.ndn.FaceManager;
import com.stefanolupo.ndngame.backend.subscriber.metrics.BaseSubscriberMetricsFactory;
import com.stefanolupo.ndngame.backend.subscriber.metrics.BaseSubscriberMetricsNames;
import com.stefanolupo.ndngame.config.LocalConfig;
import com.stefanolupo.ndngame.names.PlayerStatusName;
import com.stefanolupo.ndngame.protos.GameObject;
import com.stefanolupo.ndngame.protos.Player;
import com.stefanolupo.ndngame.protos.PlayerStatus;
import net.named_data.jndn.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Singleton
public class PlayerStatusSubscriber implements OnPlayersDiscovered {

    private static final Logger LOG = LoggerFactory.getLogger(PlayerStatusSubscriber.class);

    private final Map<PlayerStatusName, BaseSubscriber<PlayerStatus>> subscriberMap = new HashMap<>();
    private final LocalConfig localConfig;
    private final FaceManager faceManager;
    private final LocalPlayerReference localPlayerReference;
    private final LinearInterestZoneFilter linearInterestZoneFilter;
    private final BaseSubscriberMetricsFactory metricsFactory;
    private final Value<Long> maxWaitTime;
    private final Value<Boolean> useZoneFiltering;

    @Inject
    public PlayerStatusSubscriber(LocalConfig localConfig,
                                  FaceManager faceManager,
                                  LocalPlayerReference localPlayerReference,
                                  LinearInterestZoneFilter linearInterestZoneFilter,
                                  BaseSubscriberMetricsFactory metricsFactory,
                                  @Named("player.sub.inter.interest.max.wait.time.ms") Value<Long> maxWaitTime,
                                  @Named("linear.interest.zone.filter.enabled") Value<Boolean> useZoneFiltering) {
        this.localConfig = localConfig;
        this.faceManager = faceManager;
        this.localPlayerReference = localPlayerReference;
        this.linearInterestZoneFilter = linearInterestZoneFilter;
        this.metricsFactory = metricsFactory;
        this.maxWaitTime = maxWaitTime;
        this.useZoneFiltering = useZoneFiltering;
    }

    private void addSubscription(PlayerStatusName name) {
        LOG.info("Adding subscription for {}", name.getPlayerName().getName());
        BaseSubscriber<PlayerStatus> subscriber = new BaseSubscriber<>(
                faceManager,
                name,
                this::typeFromData,
                PlayerStatusName::new,
                this::sleepTimeFromPosition,
                metricsFactory.forNameAndType(name.getPlayerName(), BaseSubscriberMetricsNames.ObjectType.STATUS));
        subscriberMap.put(name, subscriber);
    }

    // TODO: This is probably going to bite me
    // TODO: The Maps PLayerStatusName never changes, neither does the engine
    // TODO: But the BaseSubscribers one will, so that is the one to check against!!
    // It kind of is okay since the map / engine are just using it to track the name!
    public long getLatestVersionForPlayer(PlayerStatusName playerStatusName) {
        return subscriberMap.get(playerStatusName).getLatestVersionSeen();
    }

    public PlayerStatus getLatestStatusForPlayer(PlayerStatusName playerStatusName) {
        return subscriberMap.get(playerStatusName).getEntity();
    }

    private PlayerStatus typeFromData(Data data) {
        try {
            return PlayerStatus.parseFrom(data.getContent().getImmutableArray());
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException("Couldn't parse data for " + data.getName().toUri(), e);
        }
    }

    private long sleepTimeFromPosition(PlayerStatus playerStatus) {
        if (!useZoneFiltering.get()) return 0;

        GameObject localPlayer = localPlayerReference.getPlayerStatus().getGameObject();
        GameObject remotePlayer = playerStatus.getGameObject();
        double weight = linearInterestZoneFilter.getSleepTimeFactor(
                localPlayer.getX(), localPlayer.getY(),
                remotePlayer.getX(), remotePlayer.getY());
        return Math.round(maxWaitTime.get() * weight);
    }

    @Override
    public void onPlayersDiscovered(Set<Player> players) {
        players.forEach(p -> this.addSubscription(new PlayerStatusName(localConfig.getGameId(), p.getName())));
    }
}
