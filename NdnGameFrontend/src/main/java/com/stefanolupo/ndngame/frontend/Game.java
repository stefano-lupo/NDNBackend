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
import processing.event.KeyEvent;

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
        surface.setTitle(this.args[1]);
    }

    @Override
    public void draw() {
        GameState gameState = backend.getGameState();

        background(0, 150, 150);
        for (RemotePlayer player : gameState.getRemotePlayers()) {
            PlayerStatus status = player.getPlayerStatus();
            fill(status.getHp());
            ellipse(status.getX(), status.getY(), 50, 50);
        }

        PlayerStatus status = gameState.getLocalPlayer().getPlayerStatus();
        fill(255,0,0);
        ellipse(status.getX(), status.getY(), 50, 50);
    }

    @Override
    public void keyPressed(KeyEvent event) {
        super.keyPressed(event);

        Command command = Command.fromChar(event.getKey());
        LOG.trace("{}", command);
        if (command != Command.UNSUPPORTED) {
            backend.handleCommand(command);
        }
    }

    public static void main(String[] args) {
        PApplet.main("com.stefanolupo.ndngame.frontend.Game", args);
    }
}
