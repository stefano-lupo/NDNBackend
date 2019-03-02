package com.stefanolupo.ndngame.backend.chronosynced;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.protobuf.InvalidProtocolBufferException;
import com.stefanolupo.ndngame.config.LocalConfig;
import com.stefanolupo.ndngame.names.DiscoveryName;
import com.stefanolupo.ndngame.protos.Player;
import com.stefanolupo.ndngame.protos.Players;
import net.named_data.jndn.Data;
import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;
import net.named_data.jndn.sync.ChronoSync2013;
import net.named_data.jndn.util.Blob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

@Singleton
public class DiscoveryManager extends ChronoSynced {

    private static final Logger LOG = LoggerFactory.getLogger(DiscoveryManager.class);

    private final Set<Player> players = new HashSet<>();
    private final Player localPlayer;
    private final Set<OnPlayersDiscovered> discoveryCallbacks;

    @Inject
    public DiscoveryManager(LocalConfig localConfig,
                            Set<OnPlayersDiscovered> discoveryCallbacks) {
        super(buildBroadcastPrefix(localConfig), buildDataPrefix(localConfig));
        this.localPlayer = Player.newBuilder()
                .setName(localConfig.getPlayerName())
                .build();
        this.discoveryCallbacks = discoveryCallbacks;
    }

    @Override
    public void onInitialized() {
        super.onInitialized();
        players.add(localPlayer);
        publishUpdate();
    }

    @Override
    protected Optional<Blob> localToBlob(Interest interest) {
        Players players = Players.newBuilder().addAllPlayers(this.players).build();
        return Optional.of(new Blob(players.toByteArray()));
    }

    @Override
    protected Collection<Interest> syncStatesToInterests(List<ChronoSync2013.SyncState> syncStates, boolean isRecovery) {
        return syncStates.stream()
                .map(DiscoveryName::new)
                .map(DiscoveryName::toInterest)
                .collect(Collectors.toList());
    }

    @Override
    public void onData(Interest interest, Data data) {
        try {
            Set<Player> players = new HashSet<>(Players.parseFrom(data.getContent().getImmutableArray()).getPlayersList());
            Set<Player> newPlayers = new HashSet<>(Sets.difference(players, this.players));
            this.players.addAll(players);

            discoveryCallbacks.forEach(callback -> callback.onPlayersDiscovered(newPlayers));
        } catch (InvalidProtocolBufferException e) {
            LOG.error("Unable to parse players list for {}", interest.toUri());
        }
    }

    private static Name buildBroadcastPrefix(LocalConfig localConfig) {
        return DiscoveryName.getBroadcastName(localConfig.getGameId());
    }

    private static Name buildDataPrefix(LocalConfig localConfig) {
        return new DiscoveryName(localConfig.getGameId(), localConfig.getPlayerName()).getAsPrefix();
    }
}
