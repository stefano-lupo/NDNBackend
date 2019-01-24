package com.stefanolupo.ndngame;

import com.stefanolupo.ndngame.protos.PlayerStatus;
import net.named_data.jndn.Name;

public class Player {

    protected final String playerName;

    protected PlayerStatus playerStatus;

    public Player(String playerName) {
        this(playerName, PlayerStatus.newBuilder().build());
    }

    public Player(String playerName, PlayerStatus playerStatus) {
        this.playerName = playerName;
        this.playerStatus = playerStatus;
    }

    public void update(PlayerStatus position) {
        this.playerStatus = position.toBuilder().build();
    }

    public Name getInterestName() {
        return new Name(Names.PLAYER_STATUS.getName(playerName));
    }

    public String getPlayerName() {
        return playerName;
    }

    public PlayerStatus getPlayerStatus() {
        return this.playerStatus;
    }
}
