package com.stefanolupo.ndngame.backend;

import com.google.protobuf.InvalidProtocolBufferException;
import com.stefanolupo.ndngame.Player;
import com.stefanolupo.ndngame.backend.players.LocalPlayer;
import com.stefanolupo.ndngame.backend.players.RemotePlayer;
import com.stefanolupo.ndngame.names.PlayerStatusName;
import com.stefanolupo.ndngame.protos.PlayerStatus;
import net.named_data.jndn.*;
import net.named_data.jndn.security.KeyChain;
import net.named_data.jndn.security.SecurityException;
import net.named_data.jndn.security.pib.PibImpl;
import net.named_data.jndn.sync.ChronoSync2013;
import net.named_data.jndn.util.Blob;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class GameState implements
        ChronoSync2013.OnInitialized,
        ChronoSync2013.OnReceivedSyncState,
        OnData,
        OnInterestCallback
{

    private static final long LOCAL_PLAYER_PUBLISH_RATE_MS = 50;
    private static final Name BROADCAST_PREFIX = new Name("/com/stefanolupo/ndngame/broadcast");

    private final LocalPlayer localPlayer;
    private final boolean automatePlayer;
    private final long gameId;

    private final Map<PlayerStatusName, RemotePlayer> remotePlayers;
    private final ChronoSync2013 chronoSync;
    private final Face face;
    private final KeyChain keyChain;
    private final Name certificateName;
    private final long session;
    private final PlayerStatusName localStatusName;

    private int tick = 0;

    public GameState(LocalPlayer localPlayer, boolean automatePlayer, long gameId) {
        try {
            this.localPlayer = localPlayer;
            this.automatePlayer = automatePlayer;
            this.gameId = gameId;

            // TODO: Abstract all of this
            keyChain = new KeyChain();
            certificateName = keyChain.getDefaultCertificateName();
            face = new Face();
            face.setCommandSigningInfo(keyChain, certificateName);
            session = System.currentTimeMillis();
            localStatusName = new PlayerStatusName(gameId, localPlayer.getPlayerName());

            chronoSync = new ChronoSync2013(
                    this,
                    this,
                    new Name(localStatusName.getFullName()),
                    BROADCAST_PREFIX,
                    session,
                    face,
                    keyChain,
                    certificateName,
                    1000.0,
                    this::registerPrefixFailure
            );

            remotePlayers = new HashMap<>();
            face.registerPrefix(localStatusName.getFullName(), this, this::registerPrefixFailure);

            Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(this::publishLocalPlayerPosition, 1550, LOCAL_PLAYER_PUBLISH_RATE_MS, TimeUnit.MILLISECONDS);
            Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(this::printPlayerStatus, 0, 5, TimeUnit.SECONDS);
            Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(this::pollFace, 0, 50, TimeUnit.MILLISECONDS);
        } catch (SecurityException | KeyChain.Error | PibImpl.Error | IOException e) {
            throw new RuntimeException("Could not initialize game state", e);
        }
    }


    public List<RemotePlayer> getRemotePlayers() {
        return new ArrayList<>(remotePlayers.values());
    }

    public LocalPlayer getLocalPlayer() {
        return this.localPlayer;
    }

    private void publishLocalPlayerPosition() {
        try {
            chronoSync.publishNextSequenceNo();
        } catch (IOException | SecurityException e) {
            throw new RuntimeException("Unable to publish local player sequence number", e);
        }
    }



    private void registerPrefixFailure(Name prefix) {
        throw new RuntimeException("Unable to register prefix " + prefix);
    }

    @Override
    public void onData(Interest interest, Data data) {
        PlayerStatusName name = new PlayerStatusName(interest);
        try {
            PlayerStatus status = PlayerStatus.parseFrom(data.getContent().getImmutableArray());
            RemotePlayer player = remotePlayers.get(name);
            if (player == null) {
                player = new RemotePlayer(name.getPlayerName(), status);
            } else {
                player.update(status);
            }
            remotePlayers.put(name, player);
        } catch (InvalidProtocolBufferException e) {
            //TODO: abstract this
            throw new RuntimeException(e);
        }

    }

    @Override
    public void onInterest(Name prefix, Interest interest, Face face, long interestFilterId, InterestFilter filter) {
        Data data = new Data(interest.getName()).setContent(new Blob(localPlayer.getPlayerStatus().toByteArray()));
        try {
            keyChain.sign(data, certificateName);
            face.send(data.wireEncode());
        } catch (Exception e) {
            throw new RuntimeException("Unable to send data to satisfy interest " + interest.toUri(), e);
        }
    }

    @Override
    public void onInitialized() {
        System.out.println("Initialized");
    }

    @Override
    public void onReceivedSyncState(List syncStates, boolean isRecovery) {
        ChronoSync2013.SyncState syncState = (ChronoSync2013.SyncState) syncStates.get(syncStates.size() - 1);
        Interest interest = new Interest(new Name(syncState.getDataPrefix()).append(String.valueOf(syncState.getSequenceNo())));
//        if (isRecovery) {
//            System.out.println("Skipping Recovery - " + syncStates.size());
//            return;
//        }

        if (syncStates.size() > 1) {
            System.out.println("Got more than one sync state!");
            long numRedundants = syncStates.stream()
                    .map(ss -> (ChronoSync2013.SyncState) ss)
                    .filter(ss -> ((ChronoSync2013.SyncState) ss).getDataPrefix().contains("desktoptwo"))
                    .count();
            System.out.println("Got " + numRedundants + " redundants");
        }
        try {
            face.expressInterest(interest, this);
//            System.out.println("Expressed interest " + interest.toUri());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void pollFace() {
        while (true) {
            try {
                face.processEvents();
                Thread.sleep(50);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void printPlayerStatus() {
        System.out.println("\n\nTick " + tick++);
        remotePlayers.values().forEach(p -> System.out.println(getPlayerPositionString(p)));
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

    public static void main(String[] args) throws Exception {
        GameState gameState = new GameState(new LocalPlayer("stefano"), false, 1);
    }
}
