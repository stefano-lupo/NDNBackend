package com.stefanolupo.ndngame.backend.chronosynced;

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

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class PlayerStatusManager extends ChronoSyncedMap<PlayerStatusName, RemotePlayer> {

    private static final Logger LOG = LoggerFactory.getLogger(PlayerStatusManager.class);
    private static final String BROADCAST_PREFIX = "com/stefanolupo/ndngame/%d/status/broadcast";
    private static final Long LOCAL_PLAYER_PUBLISH_RATE_MS = 30L;

    private int numRecoveries = 0;
    private int numSyncs = 0;

    private final LocalPlayer localPlayer;
    private final long gameId;

    public PlayerStatusManager(LocalPlayer localPlayer,
                               long gameId) {
        super(new Name(String.format(BROADCAST_PREFIX, gameId)),
                new PlayerStatusName(gameId, localPlayer.getPlayerName()).getListenName());
        this.localPlayer = localPlayer;
        this.gameId = gameId;
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(this::publishPlayerPosition, 5000, LOCAL_PLAYER_PUBLISH_RATE_MS, TimeUnit.MILLISECONDS);
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> LOG.info("Recoveries: {}", (numRecoveries + 0.0) / numSyncs), 2, 4, TimeUnit.SECONDS);
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
                return new RemotePlayer(key.getPlayerName(), status);
            }
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException("Unable to parse data received " + data.getName().toUri(), e);
        }
    }

    @Override
    protected Optional<Interest> syncStatesToMaybeInterest(List<ChronoSync2013.SyncState> syncStates, boolean isRecovery) {
        numSyncs ++;
        if (isRecovery) {
            numRecoveries++;
            return Optional.empty();
        }

        ChronoSync2013.SyncState state = syncStates.get(syncStates.size() - 1);
        PlayerStatusName statusName = new PlayerStatusName(state);
        return Optional.of(new Interest(statusName.getExpressInterestName()));
    }

    @Override
    protected Optional<Blob> localToBlob(Interest interest) {
        return Optional.of(new Blob(localPlayer.getPlayerStatus().toByteArray()));
    }

    private void publishPlayerPosition() {
        publishUpdate();
    }
}
