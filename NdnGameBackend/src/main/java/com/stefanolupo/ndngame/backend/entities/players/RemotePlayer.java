package com.stefanolupo.ndngame.backend.entities.players;

import com.stefanolupo.ndngame.Player;
import com.stefanolupo.ndngame.protos.PlayerStatus;


public class RemotePlayer extends Player {

    public RemotePlayer(String playerName, PlayerStatus playerStatus) {
        super(playerName, playerStatus);
    }
}
