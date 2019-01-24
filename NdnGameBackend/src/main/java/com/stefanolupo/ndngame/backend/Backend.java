package com.stefanolupo.ndngame.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.stefanolupo.ndngame.backend.players.LocalPlayer;
import com.stefanolupo.ndngame.config.Config;
import com.stefanolupo.ndngame.exceptions.NdnException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class Backend {

    private static final String CONFIG_FILE = "config.json";
    private static final ObjectMapper MAPPER = new ObjectMapper().registerModule(new Jdk8Module());
    private static final List<String> IGNORE_NAMES = Arrays.asList(
      "ndnbox",
      "laptop"
    );

    private final GameState gameState;
    private final StatusResponder playerStatusResponder;

    private Backend(String playerName, boolean automatePlayer) {
        LocalPlayer localPlayer = new LocalPlayer(playerName);
        try {
            playerStatusResponder = new StatusResponder(localPlayer);
        } catch (NdnException ne) {
            throw new RuntimeException("Unable to construct PlayerStatusResponder", ne);
        }

        gameState = createGameState(localPlayer, automatePlayer);
    }


    public void launch() {
        playerStatusResponder.launch();
    }

    public GameState getGameState() {
        return gameState;
    }

    private GameState createGameState(LocalPlayer localPlayer, boolean automatePlayer) {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(CONFIG_FILE);
        Config config;
        try {
            config = MAPPER.readValue(is, Config.class);
        } catch (IOException e) {
            throw new RuntimeException("Unable to parse config file " + CONFIG_FILE, e);
        }

        GameState gameState = new GameState(localPlayer, automatePlayer);
        config.getNodeConfigs().stream()
                .filter(n -> !IGNORE_NAMES.contains(n.getName()) && !n.getName().equals(localPlayer.getPlayerName()))
                .forEach(n -> gameState.registerNewPlayer(n.getName()));
        return gameState;
    }

    public static class BackendBuilder {

        private String playerName;
        private boolean automatePlayer = false;

        public BackendBuilder playerName(String playerName) {
            this.playerName = playerName;
            return this;
        }

        public BackendBuilder automatePlayer(boolean automatePlayer) {
            this.automatePlayer = automatePlayer;
            return this;
        }

        public Backend build() {
            return new Backend(playerName, automatePlayer);
        }
    }
}
