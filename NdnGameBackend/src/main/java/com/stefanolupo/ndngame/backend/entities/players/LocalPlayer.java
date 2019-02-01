package com.stefanolupo.ndngame.backend.entities.players;

import com.stefanolupo.ndngame.Player;
import com.stefanolupo.ndngame.backend.events.Command;

public class LocalPlayer extends Player {

    public LocalPlayer(String playerName) {
        super(playerName);
    }

    public void move(Command command) {
        int x = playerStatus.getX();
        int y = playerStatus.getY();

        switch (command) {
            case MOVE_UP:
                y -= DEFAULT_SPEED;
                break;
            case MOVE_DOWN:
                y += DEFAULT_SPEED;
                break;
            case MOVE_LEFT:
                x -= DEFAULT_SPEED;
                break;
            case MOVE_RIGHT:
                x += DEFAULT_SPEED;
                break;
        }

        setXAndY(x, y);
    }
}
