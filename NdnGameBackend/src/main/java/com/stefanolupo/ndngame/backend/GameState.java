package com.stefanolupo.ndngame.backend;

import com.stefanolupo.ndngame.Player;
import com.stefanolupo.ndngame.backend.players.LocalPlayer;
import com.stefanolupo.ndngame.backend.players.RemotePlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class GameState {
    private final LocalPlayer localPlayer;
    private final boolean automatePlayer;
    private final List<RemotePlayer> remotePlayers;

    private int tick = 0;

    public GameState(LocalPlayer localPlayer, boolean automatePlayer) {
        this.localPlayer = localPlayer;
        this.automatePlayer = automatePlayer;

        remotePlayers = new ArrayList<>();
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(this::printPlayerStatus, 0, 5, TimeUnit.SECONDS);
    }

    public void registerNewPlayer(String playerName) {
        remotePlayers.add(new RemotePlayer(playerName));
    }

    public List<RemotePlayer> getRemotePlayers() {
        return Collections.unmodifiableList(remotePlayers);
    }

    public LocalPlayer getLocalPlayer() {
        return this.localPlayer;
    }

    private void printPlayerStatus() {
        System.out.println("\n\nTick " + tick++);
        remotePlayers.forEach(p -> System.out.println(getPlayerPositionString(p)));
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
