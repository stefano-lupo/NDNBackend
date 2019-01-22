package com.stefanolupo.ndn;

import net.named_data.jndn.Name;

public class GamePlayer {

    private final String playerName;
    private NDNGameProtos.Position position;

    public GamePlayer(String playerName) {
        this(playerName, NDNGameProtos.Position.newBuilder().setX(0).setY(0).build());
    }

    public GamePlayer(String playerName, NDNGameProtos.Position position) {
        this.playerName = playerName;
        this.position = position;
    }

    public void update(NDNGameProtos.Position position) {
        this.position = this.position.toBuilder()
                .setX(position.getX())
                .setY(position.getY())
                .build();
    }

    public Name getInterestName() {
        return new Name(String.format("%s/%s/ping", Names.PLAYER_POSITION.getName(), playerName));
    }

    public String getPlayerName() {
        return playerName;
    }

    public NDNGameProtos.Position getPosition() {
        return this.position;
    }
}
