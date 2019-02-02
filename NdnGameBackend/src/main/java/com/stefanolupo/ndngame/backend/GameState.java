package com.stefanolupo.ndngame.backend;

import com.stefanolupo.ndngame.Player;
import com.stefanolupo.ndngame.backend.chronosynced.PlayerStatusManager;
import com.stefanolupo.ndngame.backend.entities.players.LocalPlayer;
import com.stefanolupo.ndngame.backend.entities.players.RemotePlayer;
import com.stefanolupo.ndngame.backend.events.Command;
import com.stefanolupo.ndngame.backend.events.CommandType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class GameState {

    private static final Logger LOG = LoggerFactory.getLogger(GameState.class);

    private final LocalPlayer localPlayer;
    private final boolean automatePlayer;
    private final long gameId;

    private final PlayerStatusManager playerStatusManager;

    public GameState(String playerName, boolean automatePlayer, long gameId) {
        this.localPlayer = new LocalPlayer(playerName, automatePlayer);
        this.automatePlayer = automatePlayer;
        this.gameId = gameId;
        this.playerStatusManager = new PlayerStatusManager(localPlayer, gameId);

        if (automatePlayer) {
            LOG.info("Automating player: {}", playerName);
            Executors.newSingleThreadScheduledExecutor()
                    .scheduleAtFixedRate(() -> moveLocalPlayer(getRandomMoveCommand()), 5000, 150, TimeUnit.MILLISECONDS);
        }
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(this::printPlayerStatus, 0, 5, TimeUnit.SECONDS);
    }

    public LocalPlayer getLocalPlayer() {
        return localPlayer;
    }

    public List<RemotePlayer> getRemotePlayers() {
        return new ArrayList<>(playerStatusManager.getMap().values());
    }

    public void moveLocalPlayer(Command command) {
        boolean hasUpdatedVel = localPlayer.move(command);

        if (hasUpdatedVel) {
            playerStatusManager.publishPlayerStatusChange();
        }
    }

    public void stopLocalPlayer() {
        boolean hasUpdatedVel = localPlayer.stop();

        if (hasUpdatedVel) {
            playerStatusManager.publishPlayerStatusChange();
        }
    }

    public void interact(Command command) {

    }

    private void printPlayerStatus() {
        System.out.println();
        playerStatusManager.getMap().values().forEach(p -> System.out.println(getPlayerPositionString(p)));
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

    private Command getRandomMoveCommand() {
        List<Command> moveCommands = Command.getCommandsOfType(CommandType.MOVE);
        return moveCommands.get(ThreadLocalRandom.current().nextInt(moveCommands.size()));
    }
}
