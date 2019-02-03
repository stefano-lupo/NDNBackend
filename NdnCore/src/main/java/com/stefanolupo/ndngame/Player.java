package com.stefanolupo.ndngame;

import com.stefanolupo.ndngame.protos.PlayerStatus;

import java.util.concurrent.ThreadLocalRandom;

public class Player {

    protected static final int DEFAULT_SPEED = 1;

    protected final String playerName;

    // TODO: This is all sorts of not thread safe right now
    protected PlayerStatus playerStatus;

    public Player(String playerName) {
        this(playerName, PlayerStatus.newBuilder()
                .setX(ThreadLocalRandom.current().nextInt(200, 300))
                .setY(ThreadLocalRandom.current().nextInt(200, 300))
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

    protected void move() {
        playerStatus = playerStatus.toBuilder()
                .setX(playerStatus.getX() + DEFAULT_SPEED * playerStatus.getVelX())
                .setY(playerStatus.getY() + DEFAULT_SPEED * playerStatus.getVelY())
                .build();
    }

    protected void move(int newVelX, int newVelY) {
        playerStatus = playerStatus.toBuilder()
                .setVelX(newVelX)
                .setVelY(newVelY)
                .setX(playerStatus.getX() + DEFAULT_SPEED * newVelX)
                .setY(playerStatus.getY() + DEFAULT_SPEED * newVelY)
                .build();
    }
}
