package com.stefanolupo.ndngame.libgdx.components;

import com.badlogic.ashley.core.Component;
import com.stefanolupo.ndngame.names.PlayerStatusName;

public class RemotePlayerComponent implements Component {

    private PlayerStatusName playerStatusName;
    private long latestVersionSeen = 0;

    public PlayerStatusName getPlayerStatusName() {
        return playerStatusName;
    }

    public void setPlayerStatusName(PlayerStatusName playerStatusName) {
        this.playerStatusName = playerStatusName;
    }

    public long getLatestVersionSeen() {
        return latestVersionSeen;
    }

    public void setLatestVersionSeen(long latestVersionSeen) {
        this.latestVersionSeen = latestVersionSeen;
    }
}
