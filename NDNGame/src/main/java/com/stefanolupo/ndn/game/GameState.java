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
        executorService.scheduleAtFixedRate(this::printPlayerStatus, 0, 3, TimeUnit.SECONDS);
    }

    public void registerNewPlayer(String playerName) {
        syncrhonizedGamePlayers.add(new SyncrhonizedGamePlayer(playerName));
    }

    private void printPlayerStatus() {
        System.out.println("\n\nTick " + tick++);
        syncrhonizedGamePlayers.forEach(p -> System.out.println(getPlayerPositionString(p.getGamePlayer())));
        System.out.println();
    }

    private String getPlayerPositionString(GamePlayer player) {
        return String.format("%s - x:%d, y:%d, hp:%d, mana:%d, score:%d",
                player.getPlayerName(),
                player.getPlayerStatus().getX(),
                player.getPlayerStatus().getY(),
                player.getPlayerStatus().getHp(),
                player.getPlayerStatus().getMana(),
                player.getPlayerStatus().getScore());
    }

}
