package com.stefanolupo.ndngame.backend;

import com.google.protobuf.InvalidProtocolBufferException;
import com.stefanolupo.ndngame.Player;
import com.stefanolupo.ndngame.backend.chronosynced.ChronoSyncedMap;
import com.stefanolupo.ndngame.backend.players.LocalPlayer;
import com.stefanolupo.ndngame.backend.players.RemotePlayer;
import com.stefanolupo.ndngame.names.PlayerStatusName;
import com.stefanolupo.ndngame.protos.PlayerStatus;
import net.named_data.jndn.Data;
import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;
import net.named_data.jndn.util.Blob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class GameState extends ChronoSyncedMap<PlayerStatusName, RemotePlayer> {

    private static final Logger LOG = LoggerFactory.getLogger(GameState.class);
    private static final String BROADCAST_PREFIX = "com/stefanolupo/ndngame/%d/status/broadcast";
    private static final Long LOCAL_PLAYER_PUBLISH_RATE_MS = 20L;

    private final LocalPlayer localPlayer;
    private final boolean automatePlayer;
    private final long gameId;

    public GameState(LocalPlayer localPlayer, boolean automatePlayer, long gameId) {
        super(new Name(String.format(BROADCAST_PREFIX, gameId)),
                new PlayerStatusName(gameId, localPlayer.getPlayerName()).getListenName());
        this.localPlayer = localPlayer;
        this.automatePlayer = automatePlayer;
        this.gameId = gameId;
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(this::printPlayerStatus, 0, 5, TimeUnit.SECONDS);
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(this::publishPlayerPosition, 1000, LOCAL_PLAYER_PUBLISH_RATE_MS, TimeUnit.MILLISECONDS);

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
    protected Blob localToBlob(Interest interest) {
        return new Blob(localPlayer.getPlayerStatus().toByteArray());
    }

    public LocalPlayer getLocalPlayer() {
        return localPlayer;
    }

    public List<RemotePlayer> getRemotePlayers() {
        return new ArrayList<>(getMap().values());
    }

    private void publishPlayerPosition() {
        publishUpdate();
    }

    private void printPlayerStatus() {
        System.out.println();
        getMap().values().forEach(p -> System.out.println(getPlayerPositionString(p)));
        System.out.println();
    }

    private String getPlayerPositionString(Player player) {
        return String.format("%s - x:%d, y:%d, hp:%d, mana:%d, score:%d",
                player.getPlayerName(),
                player.getPlayerStatus().getX(),
                player.getPlayerStatus().getY(),
                player.getPlayerStatus().getHp(),
                player.getPlayerStatus().getMana(),
                player.getPlayerStatus().getScore());
    }
}
