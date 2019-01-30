package com.stefanolupo.ndngame.frontend;


import com.stefanolupo.ndngame.backend.Backend;
import com.stefanolupo.ndngame.backend.CommandLineHelper;
import com.stefanolupo.ndngame.backend.GameState;
import com.stefanolupo.ndngame.backend.players.LocalPlayer;
import com.stefanolupo.ndngame.backend.players.RemotePlayer;
import com.stefanolupo.ndngame.protos.PlayerStatus;
import processing.core.PApplet;

public class Game extends PApplet {

    private Backend backend;

    @Override
    public void settings() {
        Backend.BackendBuilder builder = new CommandLineHelper().getBackendBuilder(this.args);
        backend = builder.build();
        size(500, 500);
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

        // Pretend this happens elsewhere..
        LocalPlayer localPlayer = gameState.getLocalPlayer();
        localPlayer.update(localPlayer.getPlayerStatus().toBuilder()
            .setX(mouseX)
            .setY(mouseY)
            .build());

        PlayerStatus status = gameState.getLocalPlayer().getPlayerStatus();
        fill(255,0,0);
        ellipse(status.getX(), status.getY(), 50, 50);
    }


    public static void main(String[] args) {
        PApplet.main("com.stefanolupo.ndngame.frontend.Game", args);
    }
}
