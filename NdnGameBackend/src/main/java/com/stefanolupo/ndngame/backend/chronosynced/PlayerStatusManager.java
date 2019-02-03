package com.stefanolupo.ndngame.backend.chronosynced;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.protobuf.InvalidProtocolBufferException;
import com.stefanolupo.ndngame.backend.entities.players.LocalPlayer;
import com.stefanolupo.ndngame.backend.entities.players.RemotePlayer;
import com.stefanolupo.ndngame.config.Config;
import com.stefanolupo.ndngame.names.PlayerStatusName;
import com.stefanolupo.ndngame.protos.PlayerStatus;
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
public class PlayerStatusManager extends ChronoSyncedDataStructure {

    private static final Logger LOG = LoggerFactory.getLogger(PlayerStatusManager.class);
    private static final String BROADCAST_PREFIX = "/com/stefanolupo/ndngame/%d/status/broadcast";

    private final LocalPlayer localPlayer;
    private final Map<PlayerStatusName, RemotePlayer> remotePlayerMap = new HashMap<>();

    @Inject
    public PlayerStatusManager(LocalPlayer localPlayer, Config config) {
        super(new Name(String.format(BROADCAST_PREFIX, config.getGameId())),
                new PlayerStatusName(config.getGameId(), localPlayer.getPlayerName()).getListenName());
        this.localPlayer = localPlayer;
    }

    @Override
    public void onData(Interest interest, Data data) {
        try {
            PlayerStatusName name = new PlayerStatusName(interest);
            PlayerStatus status = PlayerStatus.parseFrom(data.getContent().getImmutableArray());
            if (remotePlayerMap.containsKey(name)) {
                remotePlayerMap.get(name).update(status);
            } else {
                LOG.info("First appearance of {}, creating..", name.getPlayerName());
                remotePlayerMap.put(name, new RemotePlayer(name.getPlayerName(), status));
            }
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException("Unable to parse data received " + data.getName().toUri(), e);
        }
    }

    @Override
    protected Collection<Interest> syncStatesToInterests(List<ChronoSync2013.SyncState> syncStates, boolean isRecovery) {
        if (syncStates.size() > 1) {
            //LOG.debug("Got more than 1 sync state");
        }

        // TODO: I'm not sure if get 1 sync state per user or whether we can get multiple sync states for the same user
        List<PlayerStatusName> filteredSyncStates = syncStates.stream()
                .map(PlayerStatusName::new)
                .filter(psn -> !psn.getPlayerName().equals(localPlayer.getPlayerName()))
                .collect(Collectors.toList());

        if (filteredSyncStates.size() > remotePlayerMap.keySet().size() + 1) {
            LOG.error("Got more sync states than remote players and me");
        }
        Multimap<String, PlayerStatusName> multimap = Multimaps.index(filteredSyncStates, PlayerStatusName::getPlayerName);

        return multimap.asMap().entrySet().stream()
                .map(e -> e.getValue().stream().max(Comparator.comparing(PlayerStatusName::getSequenceNumber)))
                .filter(Optional::isPresent)
                .map(opt -> opt.get().toInterest())
                .collect(Collectors.toList());

    }

    @Override
    protected Optional<Blob> localToBlob(Interest interest) {
        return Optional.of(new Blob(localPlayer.getPlayerStatus().toByteArray()));
    }

    public Collection<RemotePlayer> getRemotePlayers() {
        return Collections.unmodifiableCollection(remotePlayerMap.values());
    }

    public void publishPlayerStatusChange() {
        publishUpdate();
    }
}
