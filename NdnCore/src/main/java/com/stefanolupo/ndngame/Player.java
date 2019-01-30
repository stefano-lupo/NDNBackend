package com.stefanolupo.ndngame;

import com.stefanolupo.ndngame.protos.PlayerStatus;

public class Player {

    protected final String playerName;

    protected PlayerStatus playerStatus;

    public Player(String playerName) {
        this(playerName, PlayerStatus.newBuilder().build());
    }

    public Player(String playerName, PlayerStatus playerStatus) {
        this.playerName = playerName;
        this.playerStatus = playerStatus;
    }

    public void update(PlayerStatus position) {
        this.playerStatus = position.toBuilder().build();
    }

    public String getPlayerName() {
        return playerName;
    }

    public PlayerStatus getPlayerStatus() {
        return this.playerStatus;
    }
}
