package com.stefanolupo.ndngame;

import com.stefanolupo.ndngame.protos.PlayerStatus;

public class Player {

    protected static final int DEFAULT_SPEED = 5;

    protected final String playerName;

    // TODO: This is all sorts of not thread safe right now
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

    protected void setXAndY(int x, int y) {
        playerStatus = playerStatus.toBuilder()
            .setX(x)
            .setY(y)
            .build();
    }
}
