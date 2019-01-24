package com.stefanolupo.ndn;

import net.named_data.jndn.Name;

public class GamePlayer {

    private final String playerName;
    private NDNGameProtos.PlayerStatus playerStatus;

    public GamePlayer(String playerName) {
        this(playerName, NDNGameProtos.PlayerStatus.newBuilder().setX(0).setY(0).build());
    }

    public GamePlayer(String playerName, NDNGameProtos.PlayerStatus playerStatus) {
        this.playerName = playerName;
        this.playerStatus = playerStatus;
    }

    public void update(NDNGameProtos.PlayerStatus position) {
        this.playerStatus = position.toBuilder().build();
    }

    public Name getInterestName() {
        return new Name(Names.PLAYER_STATUS.getName(playerName));
    }

    public String getPlayerName() {
        return playerName;
    }

    public NDNGameProtos.PlayerStatus getPlayerStatus() {
        return this.playerStatus;
    }
}
