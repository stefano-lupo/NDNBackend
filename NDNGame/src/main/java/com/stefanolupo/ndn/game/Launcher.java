package com.stefanolupo.ndn.game;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stefanolupo.ndn.Config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Launcher {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final List<String> IGNORE_NAMES = Arrays.asList(
      "ndnbox",
      "laptop"
    );

    public static final String CONFIG_FILE = "config.json";

    public static void main(String[] args) {
        if (args.length < 1) {
            throw new IllegalArgumentException("Must supply a name for this node");
        }

        String nodeName = args[0];

        CompletableFuture.runAsync(() -> {
            try {
                new StatusResponder(nodeName).processEvents();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(CONFIG_FILE);
        Config config;
        try {
            config = MAPPER.readValue(is, Config.class);
            GameState gameState = new GameState();
            config.nodes.stream()
                .filter(n -> !IGNORE_NAMES.contains(n.name))
                .forEach(n -> gameState.registerNewPlayer(n.name));
        } catch (IOException e) {
            System.err.println("Could not load config file" + CONFIG_FILE + ", exitng..");
            e.printStackTrace();
        }
    }
}
