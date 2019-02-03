package com.stefanolupo.ndngame.backend.entities.players;

import com.google.inject.Inject;
import com.stefanolupo.ndngame.Player;
import com.stefanolupo.ndngame.backend.events.Command;
import com.stefanolupo.ndngame.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalPlayer extends Player {

    private static final Logger LOG = LoggerFactory.getLogger(LocalPlayer.class);

    private final boolean isAutomated;

    @Inject
    public LocalPlayer(Config config) {
        super(config.getPlayerName());
        this.isAutomated = config.getIsAutomated();
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

        move(newVelX, newVelY);

        return oldVelX != newVelX || oldVelY != newVelY;
    }

    public boolean stop() {
        int oldVelX = playerStatus.getVelX();
        int oldVelY = playerStatus.getVelY();

        move(0, 0);
        return oldVelX != 0 || oldVelY != 0;
    }

    public boolean isAutomated() {
        return isAutomated;
    }
}
