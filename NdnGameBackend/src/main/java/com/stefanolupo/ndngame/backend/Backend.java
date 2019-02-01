package com.stefanolupo.ndngame.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.stefanolupo.ndngame.backend.entities.players.LocalPlayer;
import com.stefanolupo.ndngame.backend.events.Command;
import com.stefanolupo.ndngame.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

public class Backend {

    private static final Logger LOG = LoggerFactory.getLogger(Backend.class);
    private static final String CONFIG_FILE = "config.json";
    private static final ObjectMapper MAPPER = new ObjectMapper().registerModule(new Jdk8Module());

    private final GameState gameState;

    private Backend(String playerName, boolean automatePlayer, long gameId) {
        LocalPlayer localPlayer = new LocalPlayer(playerName);
        gameState = createGameState(localPlayer, automatePlayer, gameId);
    }

    public GameState getGameState() {
        return gameState;
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
                LOG.error("Got unexpected command: ");
        }
    }

    private GameState createGameState(LocalPlayer localPlayer, boolean automatePlayer, long gameId) {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(CONFIG_FILE);
        Config config;
        try {
            config = MAPPER.readValue(is, Config.class);
        } catch (IOException e) {
            throw new RuntimeException("Unable to parse config file " + CONFIG_FILE, e);
        }


        return new GameState(localPlayer, automatePlayer, gameId);
    }

    public static class Builder {

        private String playerName;
        private boolean automatePlayer = false;
        private long gameId = 0;

        public Builder playerName(String playerName) {
            this.playerName = playerName;
            return this;
        }

        public Builder automatePlayer(boolean automatePlayer) {
            this.automatePlayer = automatePlayer;
            return this;
        }

        public Builder gameId(long gameId) {
            this.gameId = gameId;
            return this;
        }

        public Backend build() {
            return new Backend(playerName, automatePlayer, gameId);
        }
    }
}
