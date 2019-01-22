package com.stefanolupo.ndn.game;

import com.stefanolupo.ndn.GamePlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class GameState {
    private final List<SyncrhonizedGamePlayer> syncrhonizedGamePlayers;
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private int tick = 0;

    public GameState() {
        syncrhonizedGamePlayers = new ArrayList<>();
        executorService.scheduleAtFixedRate(this::printPositions, 0, 3, TimeUnit.SECONDS);
        CompletableFuture.runAsync(() -> {
            try {
                new PositionResponder("desktop").processEvents();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void registerNewPlayer(String playerName) {
        syncrhonizedGamePlayers.add(new SyncrhonizedGamePlayer(playerName));
    }

    private void printPositions() {
        System.out.println("Tick " + tick++);
        syncrhonizedGamePlayers.forEach(p -> System.out.println(getPlayerPositionString(p.getGamePlayer())));
        System.out.println();
    }

    private String getPlayerPositionString(GamePlayer player) {
        return String.format("%s - x:%d, y:%d", player.getPlayerName(), player.getPosition().getX(), player.getPosition().getY());
    }

    public static void main(String[] args) {
        GameState gameState = new GameState();
        gameState.registerNewPlayer("desktop");
    }
}
