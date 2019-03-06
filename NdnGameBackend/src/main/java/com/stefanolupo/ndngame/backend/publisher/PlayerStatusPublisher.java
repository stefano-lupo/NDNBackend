package com.stefanolupo.ndngame.backend.publisher;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.hubspot.liveconfig.value.Value;
import com.stefanolupo.ndngame.backend.LocalPlayerReference;
import com.stefanolupo.ndngame.backend.ndn.BasePublisherFactory;
import com.stefanolupo.ndngame.config.LocalConfig;
import com.stefanolupo.ndngame.names.PlayerStatusName;
import com.stefanolupo.ndngame.protos.PlayerStatus;
import net.named_data.jndn.util.Blob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class PlayerStatusPublisher {
    private static final Logger LOG = LoggerFactory.getLogger(PlayerStatusPublisher.class);

    private final BasePublisher publisher;
    private final LocalPlayerReference localPlayerReference;
    private final Cache<Long, PlayerStatusWithTime> playerStatusBySequenceNumber = CacheBuilder.newBuilder()
            .maximumSize(50)
            .concurrencyLevel(1)
            .build();


    @Inject
    public PlayerStatusPublisher(LocalConfig localConfig,
                                 BasePublisherFactory factory,
                                 LocalPlayerReference localPlayerReference,
                                 @Named("player.status.publisher.freshness.period.ms") Value<Double> freshnessPeriod) {
        this.localPlayerReference = localPlayerReference;
        PlayerStatusName playerStatusName = new PlayerStatusName(localConfig.getGameId(), localConfig.getPlayerName());
        publisher = factory.create(playerStatusName.getListenName(), PlayerStatusName::new, freshnessPeriod);
    }

    public void updateLocalPlayerStatus(PlayerStatus playerStatus) {
        long nextSequenceNumber = publisher.updateLatestBlob(new Blob(playerStatus.toByteArray()));
        localPlayerReference.setPlayerStatus(playerStatus);
        playerStatusBySequenceNumber.put(nextSequenceNumber, new PlayerStatusWithTime(playerStatus, System.currentTimeMillis()));
    }

    public List<PlayerStatusWithTime> getPlayerStatusesForOutstandingInterests() {
        return publisher.getOutstandingInterests().stream()
                .map(k -> {
                    PlayerStatusName name = (PlayerStatusName) k;
                    return playerStatusBySequenceNumber.getIfPresent(name.getLatestSequenceNumberSeen());
                })
                .collect(Collectors.toList());
    }

    public class PlayerStatusWithTime {
        public final PlayerStatus playerStatus;
        public final long timeStamp;

        public PlayerStatusWithTime(PlayerStatus playerStatus, long timeStamp) {
            this.playerStatus = playerStatus;
            this.timeStamp = timeStamp;
        }
    }
}
