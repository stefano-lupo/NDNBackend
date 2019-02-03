package com.stefanolupo.ndngame.frontend;


import com.stefanolupo.ndngame.backend.Backend;
import com.stefanolupo.ndngame.backend.GameState;
import com.stefanolupo.ndngame.backend.entities.players.RemotePlayer;
import com.stefanolupo.ndngame.backend.events.Command;
import com.stefanolupo.ndngame.backend.setup.CommandLineHelper;
import com.stefanolupo.ndngame.protos.PlayerStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import processing.core.PApplet;

import java.util.List;

public class Game extends PApplet {

    public static final Logger LOG = LoggerFactory.getLogger(Game.class);

    private Backend backend;

    @Override
    public void settings() {

        Backend.Builder builder = new CommandLineHelper().getBackendBuilder(this.args);
        backend = builder.build();
        size(500, 500);
    }

    @Override
    public void setup() {
        super.setup();
        surface.setTitle(backend.getGameState().getLocalPlayer().getPlayerName());
    }

    @Override
    public void draw() {
        tickObjects();
        drawObjects();
    }

    private void tickObjects() {
        if (keyPressed) {
            Command command = Command.fromChar(key);
            LOG.trace("{}", command);
            if (command != Command.UNSUPPORTED) {
                backend.handleCommand(command);
            }
        } else {
            LOG.trace("no command");
            backend.handleNoCommand();
        }

        List<RemotePlayer> remotePlayers = backend.getGameState().getRemotePlayers();
        remotePlayers.forEach(RemotePlayer::tick);
    }

    private void drawObjects() {
        GameState gameState = backend.getGameState();

        background(0, 150, 150);
        for (RemotePlayer player : gameState.getRemotePlayers()) {
            PlayerStatus status = player.getPlayerStatus();
            fill(status.getHp());
            ellipse(status.getX(), status.getY(), 10, 10);
        }

        PlayerStatus status = gameState.getLocalPlayer().getPlayerStatus();
        fill(255,0,0);
        ellipse(status.getX(), status.getY(), 10, 10);
    }

    public static void main(String[] args) {
        PApplet.main("com.stefanolupo.ndngame.frontend.Game", args);
    }
}
