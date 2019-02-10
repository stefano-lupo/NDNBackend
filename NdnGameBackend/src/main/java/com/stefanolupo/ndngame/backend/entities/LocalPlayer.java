package com.stefanolupo.ndngame.backend.entities;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.stefanolupo.ndngame.config.Config;
import com.stefanolupo.ndngame.names.PlayerStatusName;
import com.stefanolupo.ndngame.protos.PlayerStatus;

@Singleton
public class LocalPlayer {
    private final PlayerStatusName playerStatusName;
    private PlayerStatus playerStatus = PlayerStatus.newBuilder().build();

    @Inject
    public LocalPlayer(Config config) {
        playerStatusName = new PlayerStatusName(config.getGameId(), config.getPlayerName());
    }

    public void setPlayerStatus(PlayerStatus playerStatus) {
        this.playerStatus = PlayerStatus.newBuilder()
                .setX(playerStatus.getX())
                .setY(playerStatus.getY())
                .setVelX(playerStatus.getVelX())
                .setVelY(playerStatus.getVelY())
                .build();
    }

    public PlayerStatusName getPlayerStatusName() {
        return playerStatusName;
    }

    public PlayerStatus getPlayerStatus() {
        return playerStatus;
    }
}
