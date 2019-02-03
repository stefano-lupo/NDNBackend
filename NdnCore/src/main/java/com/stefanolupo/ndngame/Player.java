package com.stefanolupo.ndngame;

import com.stefanolupo.ndngame.protos.PlayerStatus;

import java.util.concurrent.ThreadLocalRandom;

public class Player {

    protected static final int DEFAULT_SPEED = 5;

    protected final String playerName;

    // TODO: This is all sorts of not thread safe right now
    protected PlayerStatus playerStatus;

    public Player(String playerName) {
        this(playerName, PlayerStatus.newBuilder()
                .setX(ThreadLocalRandom.current().nextInt(0, 150))
                .setY(ThreadLocalRandom.current().nextInt(0, 150))
                .build());
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

    protected void updateVelocities(int velX, int velY) {
        playerStatus = playerStatus.toBuilder()
            .setVelX(velX)
            .setVelY(velY)
            .setX(playerStatus.getX() + DEFAULT_SPEED * velX)
            .setY(playerStatus.getY() + DEFAULT_SPEED * velY)
            .build();
    }
}
