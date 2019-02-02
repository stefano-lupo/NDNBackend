package com.stefanolupo.ndngame.backend.entities.players;

import com.stefanolupo.ndngame.Player;
import com.stefanolupo.ndngame.backend.events.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalPlayer extends Player {

    private static final Logger LOG = LoggerFactory.getLogger(LocalPlayer.class);

    private final boolean automatePlayer;

    public LocalPlayer(String playerName) {
        super(playerName);
        this.automatePlayer = false;
    }

    public LocalPlayer(String playerName, boolean automatePlayer) {
        super(playerName);
        this.automatePlayer = automatePlayer;
    }

    public boolean move(Command command) {
        int oldVelX = playerStatus.getVelX();
        int oldVelY = playerStatus.getVelY();
        int newVelX = oldVelX;
        int newVelY= oldVelY;

        switch (command) {
            case MOVE_UP:
                newVelY = -1;
                break;
            case MOVE_DOWN:
                newVelY = 1;
                break;
            case MOVE_LEFT:
                newVelX = -1;
                break;
            case MOVE_RIGHT:
                newVelX = 1;
                break;
        }

        setXAndY(playerStatus.getX() + DEFAULT_SPEED * newVelX, playerStatus.getY() + DEFAULT_SPEED * newVelY);

        return oldVelX != newVelX || oldVelY != newVelY;
    }

    public boolean stop() {
        int oldVelX = playerStatus.getVelX();
        int oldVelY = playerStatus.getVelY();

//        setXAndY(0, 0);
        return oldVelX != 0 || oldVelY != 0;
    }
}
