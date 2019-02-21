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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Singleton
public class PlayerStatusSubscriber implements OnPlayersDiscovered {

    private static final Logger LOG = LoggerFactory.getLogger(PlayerStatusSubscriber.class);

    private final Map<PlayerStatusName, BaseSubscriber<PlayerStatus>> subscriberMap = new HashMap<>();
    private final Config config;

    @Inject
    public PlayerStatusSubscriber(Config config) {
        this.config = config;
    }

    public void addSubscription(PlayerStatusName name) {
        // TODO: Factory
        LOG.info("Adding subscription for {}", name.getPlayerName());
        BaseSubscriber<PlayerStatus> subscriber = new BaseSubscriber<>(
                name,
                this::typeFromData,
                PlayerStatusName::new);
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
//
//    private Long sequenceNumberExtractor(Interest interest, Data data) {
//        return new PlayerStatusName(interest).getSequenceNumber();
//    }

    @Override
    public void onPlayersDiscovered(Set<Player> players) {
        players.forEach(p -> this.addSubscription(new PlayerStatusName(config.getGameId(), p.getName())));
    }
}
