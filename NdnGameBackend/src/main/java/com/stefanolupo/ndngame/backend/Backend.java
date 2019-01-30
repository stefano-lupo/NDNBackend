package com.stefanolupo.ndngame.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.stefanolupo.ndngame.backend.players.LocalPlayer;
import com.stefanolupo.ndngame.config.Config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class Backend {

    private static final String CONFIG_FILE = "config.json";
    private static final ObjectMapper MAPPER = new ObjectMapper().registerModule(new Jdk8Module());
    private static final List<String> IGNORE_NAMES = Arrays.asList(
      "ndnbox"
//      "laptop"
    );

    private final GameState gameState;

    private Backend(String playerName, boolean automatePlayer, long gameId) {
        LocalPlayer localPlayer = new LocalPlayer(playerName);

        gameState = createGameState(localPlayer, automatePlayer, gameId);
    }

    public GameState getGameState() {
        return gameState;
    }

    private GameState createGameState(LocalPlayer localPlayer, boolean automatePlayer, long gameId) {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(CONFIG_FILE);
        Config config;
        try {
            config = MAPPER.readValue(is, Config.class);
        } catch (IOException e) {
            throw new RuntimeException("Unable to parse config file " + CONFIG_FILE, e);
        }

        GameState gameState = new GameState(localPlayer, automatePlayer, gameId);
//        config.getNodeConfigs().stream()
//                .filter(n -> !IGNORE_NAMES.contains(n.getName()) && !n.getName().equals(localPlayer.getPlayerName()))
//                .forEach(n -> gameState.registerNewPlayer(n.getName()));
        return gameState;
    }

    public static class BackendBuilder {

        private String playerName;
        private boolean automatePlayer = false;
        private long gameId = 0;

        public BackendBuilder playerName(String playerName) {
            this.playerName = playerName;
            return this;
        }

        public BackendBuilder automatePlayer(boolean automatePlayer) {
            this.automatePlayer = automatePlayer;
            return this;
        }

        public BackendBuilder gameId(long gameId) {
            this.gameId = gameId;
            return this;
        }

        public Backend build() {
            return new Backend(playerName, automatePlayer, gameId);
        }
    }
}
