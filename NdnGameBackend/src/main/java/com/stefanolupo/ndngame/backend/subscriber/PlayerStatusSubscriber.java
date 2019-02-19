package com.stefanolupo.ndngame.backend.subscriber;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.protobuf.InvalidProtocolBufferException;
import com.stefanolupo.ndngame.backend.chronosynced.OnPlayersDiscovered;
import com.stefanolupo.ndngame.config.Config;
import com.stefanolupo.ndngame.names.PlayerStatusName;
import com.stefanolupo.ndngame.protos.Player;
import com.stefanolupo.ndngame.protos.PlayerStatus;
import net.named_data.jndn.Data;
import net.named_data.jndn.Interest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Singleton
public class PlayerStatusSubscriber implements OnPlayersDiscovered {

    private static final Logger LOG = LoggerFactory.getLogger(PlayerStatusSubscriber.class);

    private final Map<PlayerStatusName, BaseSubscriber<PlayerStatus>> subscriberMap = new HashMap<>();
    private final long gameId;

    @Inject
    public PlayerStatusSubscriber(Config config) {
        gameId = config.getGameId();
    }

    public void addSubscription(PlayerStatusName name) {
        // TODO: Factory
        LOG.info("Adding subscription for {}", name);
        BaseSubscriber<PlayerStatus> subscriber = new BaseSubscriber<>(name.getNameWithSequenceNumber(), this::typeFromData);
        subscriberMap.put(name, subscriber);
    }

    public long getLatestVersionForPlayer(PlayerStatusName playerStatusName) {
        return subscriberMap.get(playerStatusName).getLatestVersionSeen();
    }

    public PlayerStatus getLatestStatusForPlayer(PlayerStatusName playerStatusName) {
        return subscriberMap.get(playerStatusName).getEntity();
    }

    private PlayerStatus typeFromData(Interest interest, Data data) {
        try {
            return PlayerStatus.parseFrom(data.getContent().getImmutableArray());
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException("Couldn't parse data for " + interest.toUri(), e);
        }
    }

    @Override
    public void onPlayersDiscovered(Set<Player> players) {
        players.forEach(p -> this.addSubscription(new PlayerStatusName(gameId, p.getName())));
    }
}
