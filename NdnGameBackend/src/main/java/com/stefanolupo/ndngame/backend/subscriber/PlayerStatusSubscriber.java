package com.stefanolupo.ndngame.backend.subscriber;

import com.google.inject.Inject;
import com.google.protobuf.InvalidProtocolBufferException;
import com.stefanolupo.ndngame.names.PlayerStatusName;
import com.stefanolupo.ndngame.protos.PlayerStatus;
import net.named_data.jndn.Data;
import net.named_data.jndn.Interest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class PlayerStatusSubscriber {

    private static final Logger LOG = LoggerFactory.getLogger(PlayerStatusSubscriber.class);

    private final Map<PlayerStatusName, BaseSubscriber<PlayerStatus>> subscriberMap = new HashMap<>();

    @Inject
    public PlayerStatusSubscriber() {
    }

    public void addSubscription(PlayerStatusName name) {
        // TODO: Factory
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
}
