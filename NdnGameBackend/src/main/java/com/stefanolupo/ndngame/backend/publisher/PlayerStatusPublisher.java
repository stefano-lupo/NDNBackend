package com.stefanolupo.ndngame.backend.publisher;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.hubspot.liveconfig.value.Value;
import com.stefanolupo.ndngame.backend.LocalPlayerReference;
import com.stefanolupo.ndngame.backend.annotations.BackendMetrics;
import com.stefanolupo.ndngame.backend.ndn.BasePublisherFactory;
import com.stefanolupo.ndngame.config.LocalConfig;
import com.stefanolupo.ndngame.metrics.MetricNames;
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

    private final Histogram playerStatusPacketSizeHist;
    private final BasePublisher publisher;
    private final LocalPlayerReference localPlayerReference;

    /**
     * Keep a cache of the local player's recent status by their sequence number
     * This is used by the dead reckoning system to approximate the local player's position on remote machines
     */
    private final Cache<Long, PlayerStatusWithTime> playerStatusBySequenceNumber = CacheBuilder.newBuilder()
            .maximumSize(50)
            .concurrencyLevel(1)
            .build();


    @Inject
    public PlayerStatusPublisher(LocalConfig localConfig,
                                 BasePublisherFactory factory,
                                 LocalPlayerReference localPlayerReference,
                                 @BackendMetrics MetricRegistry metrics,
                                 @Named("player.status.publisher.freshness.period.ms") Value<Double> freshnessPeriod) {
        this.localPlayerReference = localPlayerReference;
        PlayerStatusName playerStatusName = new PlayerStatusName(localConfig.getGameId(), localConfig.getPlayerName());
        publisher = factory.create(playerStatusName.getListenName(), PlayerStatusName::new, freshnessPeriod);
        playerStatusPacketSizeHist = metrics.histogram(MetricNames.packetSizeHistogram(MetricNames.PacketSizeType.STATUS));
    }

    public void updateLocalPlayerStatus(PlayerStatus playerStatus) {
        playerStatusPacketSizeHist.update(playerStatus.getSerializedSize());
        long nextSequenceNumber = publisher.updateLatestBlob(new Blob(playerStatus.toByteArray()));
        localPlayerReference.setPlayerStatus(playerStatus);
        playerStatusBySequenceNumber.put(nextSequenceNumber, new PlayerStatusWithTime(playerStatus, System.currentTimeMillis()));
    }

    /**
     * For all of the publisher's outstanding interests, link their last seen sequence numbers to
     * the relevant player status. This is used by the dead reckoning system to determine when to publish an update
     * for the local players position
     */
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

        PlayerStatusWithTime(PlayerStatus playerStatus, long timeStamp) {
            this.playerStatus = playerStatus;
            this.timeStamp = timeStamp;
        }
    }
}
