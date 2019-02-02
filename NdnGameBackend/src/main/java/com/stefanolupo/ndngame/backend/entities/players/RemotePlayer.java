package com.stefanolupo.ndngame.backend.entities.players;

import com.stefanolupo.ndngame.Player;
import com.stefanolupo.ndngame.protos.PlayerStatus;


public class RemotePlayer extends Player {

    public RemotePlayer(String playerName, PlayerStatus playerStatus) {
        super(playerName, playerStatus);
    }

    public void tick() {
        int x = playerStatus.getX() + (DEFAULT_SPEED * playerStatus.getVelX());
        int y = playerStatus.getY() + (DEFAULT_SPEED * playerStatus.getVelY());
        setXAndY(x, y);
    }
}
