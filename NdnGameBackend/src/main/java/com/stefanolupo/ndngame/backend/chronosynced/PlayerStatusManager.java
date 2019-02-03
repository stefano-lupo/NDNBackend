package com.stefanolupo.ndngame.backend.chronosynced;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.protobuf.InvalidProtocolBufferException;
import com.stefanolupo.ndngame.backend.entities.players.LocalPlayer;
import com.stefanolupo.ndngame.backend.entities.players.RemotePlayer;
import com.stefanolupo.ndngame.names.PlayerStatusName;
import com.stefanolupo.ndngame.protos.PlayerStatus;
import net.named_data.jndn.Data;
import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;
import net.named_data.jndn.sync.ChronoSync2013;
import net.named_data.jndn.util.Blob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PlayerStatusManager extends ChronoSyncedMap<PlayerStatusName, RemotePlayer> {

    private static final Logger LOG = LoggerFactory.getLogger(PlayerStatusManager.class);
    private static final String BROADCAST_PREFIX = "/com/stefanolupo/ndngame/%d/status/broadcast";

    private final LocalPlayer localPlayer;
    private final long gameId;

    public PlayerStatusManager(LocalPlayer localPlayer,
                               long gameId) {
        super(new Name(String.format(BROADCAST_PREFIX, gameId)),
                new PlayerStatusName(gameId, localPlayer.getPlayerName()).getListenName());
        this.localPlayer = localPlayer;
        this.gameId = gameId;
    }

    @Override
    protected PlayerStatusName interestToKey(Interest interest) {
        return new PlayerStatusName(interest);
    }

    @Override
    protected RemotePlayer dataToVal(Data data, PlayerStatusName key, RemotePlayer oldVal) {
        try {
            PlayerStatus status = PlayerStatus.parseFrom(data.getContent().getImmutableArray());
            if (oldVal != null) {
                oldVal.update(status);
                return oldVal;
            } else {
                LOG.info("First appearance of {}, creating..", key.getPlayerName());
                return new RemotePlayer(key.getPlayerName(), status);
            }
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException("Unable to parse data received " + data.getName().toUri(), e);
        }
    }

    @Override
    protected Collection<Interest> syncStatesToInterests(List<ChronoSync2013.SyncState> syncStates, boolean isRecovery) {
        if (syncStates.size() > 1) {
            LOG.debug("Got more than 1 sync state");
        }

        // TODO: I'm not sure if get 1 sync state per user or whether we can get multiple sync states for the same user
        List<PlayerStatusName> filteredSyncStates = syncStates.stream()
                .map(PlayerStatusName::new)
                .filter(psn -> !psn.getPlayerName().equals(localPlayer.getPlayerName()))
                .collect(Collectors.toList());

        if (filteredSyncStates.size() > getMap().keySet().size() + 1) {
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

    public void publishPlayerStatusChange() {
        publishUpdate();
    }
}
