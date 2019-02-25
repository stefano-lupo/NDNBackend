package com.stefanolupo.ndngame.backend.subscriber;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.protobuf.InvalidProtocolBufferException;
import com.stefanolupo.ndngame.backend.LocalPlayerReference;
import com.stefanolupo.ndngame.backend.chronosynced.OnPlayersDiscovered;
import com.stefanolupo.ndngame.backend.filters.LinearInterestZoneFilter;
import com.stefanolupo.ndngame.backend.ndn.FaceManager;
import com.stefanolupo.ndngame.config.Config;
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
    private static final long MAX_TIME_BETWEEN_INTERESTS_MS = 2000;

    private final Map<PlayerStatusName, BaseSubscriber<PlayerStatus>> subscriberMap = new HashMap<>();
    private final Config config;
    private final FaceManager faceManager;
    private final LocalPlayerReference localPlayerReference;

    @Inject
    public PlayerStatusSubscriber(Config config,
                                  FaceManager faceManager,
                                  LocalPlayerReference localPlayerReference) {
        this.config = config;
        this.faceManager = faceManager;
        this.localPlayerReference = localPlayerReference;
    }

    public void addSubscription(PlayerStatusName name) {
        LOG.info("Adding subscription for {}", name.getPlayerName());
        BaseSubscriber<PlayerStatus> subscriber = new BaseSubscriber<>(
                faceManager,
                name,
                this::typeFromData,
                PlayerStatusName::new,
                this::sleepTimeFromPosition);
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
        GameObject localPlayer = localPlayerReference.getPlayerStatus().getGameObject();
        GameObject remotePlayer = playerStatus.getGameObject();
        double weight = LinearInterestZoneFilter.getSleepTimeFactor(
                localPlayer.getX(), localPlayer.getY(),
                remotePlayer.getX(), remotePlayer.getY());
        long sleepTime = Math.round(MAX_TIME_BETWEEN_INTERESTS_MS * weight);
//        LOG.debug("Sleeping for {}", sleepTime);
        return sleepTime;
    }

    @Override
    public void onPlayersDiscovered(Set<Player> players) {
        players.forEach(p -> this.addSubscription(new PlayerStatusName(config.getGameId(), p.getName())));
    }
}
