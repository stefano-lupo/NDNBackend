package com.stefanolupo.ndngame.backend;

import com.stefanolupo.ndngame.protos.PlayerStatus;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class LocalPlayerReference {

    private PlayerStatus playerStatus;

    @Inject
    public LocalPlayerReference() {
        playerStatus = PlayerStatus.getDefaultInstance();
    }

    public void setPlayerStatus(PlayerStatus playerStatus) {
         this.playerStatus = playerStatus;
    }

    public PlayerStatus getPlayerStatus() {
        return playerStatus;
    }
}
