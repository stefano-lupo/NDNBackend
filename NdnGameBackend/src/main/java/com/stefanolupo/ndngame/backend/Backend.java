package com.stefanolupo.ndngame.backend;

import com.google.inject.Inject;
import com.stefanolupo.ndngame.backend.events.Command;
import com.stefanolupo.ndngame.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Backend {

    private static final Logger LOG = LoggerFactory.getLogger(Backend.class);

    private final Config config;
    private final GameState gameState;

    @Inject
    private Backend(Config config, GameState gameState) {
        this.config = config;
        this.gameState = gameState;
    }

    public void handleCommand(Command command) {
        switch (command.getCommandType()) {
            case MOVE:
                gameState.moveLocalPlayer(command);
                break;
            case INTERACT:
                gameState.interact(command);
                break;
            default:
                LOG.error("Got unexpected command: {}", command);
        }
    }

    public void handleNoCommand() {
        gameState.stopLocalPlayer();
    }

    public GameState getGameState() {
        return gameState;
    }

    public int getGameWidth() {
        return config.getWidth();
    }

    public int getGameHeight() {
        return config.getHeight();
    }

//    private String randomString() {
//
//        int leftLimit = 97; // letter 'a'
//        int rightLimit = 122; // letter 'z'
//        int targetStringLength = 5;
//        Random random = new Random();
//        StringBuilder buffer = new StringBuilder(targetStringLength);
//        for (int i = 0; i < targetStringLength; i++) {
//            int randomLimitedInt = leftLimit + (int)
//                    (random.nextFloat() * (rightLimit - leftLimit + 1));
//            buffer.append((char) randomLimitedInt);
//        }
//        String generatedString = buffer.toString();
//
//        return generatedString;
//    }
}
