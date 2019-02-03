package com.stefanolupo.ndngame.frontend;


import com.google.inject.Guice;
import com.google.inject.Inject;
import com.stefanolupo.ndngame.backend.Backend;
import com.stefanolupo.ndngame.backend.GameState;
import com.stefanolupo.ndngame.backend.entities.Bullet;
import com.stefanolupo.ndngame.backend.entities.players.RemotePlayer;
import com.stefanolupo.ndngame.backend.events.Command;
import com.stefanolupo.ndngame.backend.setup.CommandLineHelper;
import com.stefanolupo.ndngame.config.Config;
import com.stefanolupo.ndngame.frontend.guice.NdnGameModule;
import com.stefanolupo.ndngame.protos.BulletStatus;
import com.stefanolupo.ndngame.protos.PlayerStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import processing.core.PApplet;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Game extends PApplet {

    private static final Logger LOG = LoggerFactory.getLogger(Game.class);

    private Backend backend;
    private long frameCount = 0;

    private final List<Command> circleCommands = Arrays.asList(Command.MOVE_RIGHT, Command.MOVE_DOWN, Command.MOVE_LEFT, Command.MOVE_UP);
    private Command automatedCommand = getNextCommand();

    @Inject
    public Game(Backend backend) {
        this.backend = backend;
    }

    @Override
    public void settings() {
        size(backend.getGameWidth(), backend.getGameHeight());
    }

    @Override
    public void setup() {
        super.setup();
        surface.setTitle(backend.getGameState().getLocalPlayer().getPlayerName());
    }

    @Override
    public void draw() {
        handleCommands();
        tickRemotes();
        drawObjects();
    }

    private void handleCommands() {
        frameCount = (frameCount + 1) % 100;
        // Quick hack for automation
        if (backend.getGameState().getLocalPlayer().isAutomated()) {
            if (frameCount == 0) {
                automatedCommand = getNextCommand();
            }
            backend.handleCommand(automatedCommand);
            return;
        }

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
    }

    private void tickRemotes() {
        GameState gameState = backend.getGameState();
        List<RemotePlayer> remotePlayers = gameState.getRemotePlayers();
        remotePlayers.forEach(RemotePlayer::tick);

        gameState.getAllBullets().forEach(Bullet::tick);
    }

    private void drawObjects() {
        GameState gameState = backend.getGameState();

        // Draw remote players
        background(0, 150, 150);
        for (RemotePlayer player : gameState.getRemotePlayers()) {
            PlayerStatus status = player.getPlayerStatus();
            fill(status.getHp());
            ellipse(status.getX(), status.getY(), 50, 50);
        }

        // Draw local player
        PlayerStatus localPlayerStatus = gameState.getLocalPlayer().getPlayerStatus();
        fill(255,0,0);
        ellipse(localPlayerStatus.getX(), localPlayerStatus.getY(), 50, 50);

        // Draw remote bullets
        for (Bullet bullet : gameState.getRemoteBullets()) {
            BulletStatus status = bullet.getBulletStatus();
            fill(0, 255, 0);
            ellipse(status.getX(), status.getY(), 10, 10);
        }

        // Draw local bullets
        for (Bullet bullet : gameState.getLocalBullets()) {
            BulletStatus status = bullet.getBulletStatus();
            fill(0, 0, 255);
            ellipse(status.getX(), status.getY(), 10, 10);
        }
    }

    private Command getNextCommand() {
        Collections.rotate(circleCommands, 1);
        return circleCommands.get(0);
    }

    public static void main(String[] args) {
        Config config = new CommandLineHelper().getConfig(args);
        Guice.createInjector(new NdnGameModule(config)).getInstance(Game.class).runSketch();
    }
}
