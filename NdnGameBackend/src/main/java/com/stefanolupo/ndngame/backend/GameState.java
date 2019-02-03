package com.stefanolupo.ndngame.backend;

import com.stefanolupo.ndngame.Player;
import com.stefanolupo.ndngame.backend.chronosynced.PlayerStatusManager;
import com.stefanolupo.ndngame.backend.entities.players.LocalPlayer;
import com.stefanolupo.ndngame.backend.entities.players.RemotePlayer;
import com.stefanolupo.ndngame.backend.events.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class GameState {

    private static final Logger LOG = LoggerFactory.getLogger(GameState.class);

    private final LocalPlayer localPlayer;
    private final boolean automatePlayer;
    private final long gameId;

    private final PlayerStatusManager playerStatusManager;

    public GameState(String playerName, boolean automatedPlayer, long gameId) {
        this.localPlayer = new LocalPlayer(playerName, automatedPlayer);
        this.automatePlayer = automatedPlayer;
        this.gameId = gameId;
        this.playerStatusManager = new PlayerStatusManager(localPlayer, gameId);

        if (automatedPlayer) {
            LOG.info("Automating player: {}", playerName);
        }
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(this::printPlayerStatus, 5, 5, TimeUnit.SECONDS);
    }

    public void moveLocalPlayer(Command command) {
        boolean hasUpdatedVel = localPlayer.move(command);

        if (hasUpdatedVel) {
            LOG.debug("Player moved and had updated vel");
            playerStatusManager.publishPlayerStatusChange();
        }
    }

    public void stopLocalPlayer() {
        boolean hasUpdatedVel = localPlayer.stop();

        if (hasUpdatedVel) {
            LOG.debug("Player stopped and had updated vel");
            playerStatusManager.publishPlayerStatusChange();
        }
    }

    public void interact(Command command) {

    }

    public LocalPlayer getLocalPlayer() {
        return localPlayer;
    }

    public List<RemotePlayer> getRemotePlayers() {
        return new ArrayList<>(playerStatusManager.getMap().values());
    }

    public boolean isAutomatedPlayer() {
        return automatePlayer;
    }

    private void printPlayerStatus() {
        System.out.println();
        LOG.info("{}", getPlayerPositionString(localPlayer));
        playerStatusManager.getMap().values().forEach(p -> LOG.info(getPlayerPositionString(p)));
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
