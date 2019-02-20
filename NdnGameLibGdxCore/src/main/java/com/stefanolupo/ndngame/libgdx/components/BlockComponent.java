package com.stefanolupo.ndngame.libgdx.components;

import com.badlogic.ashley.core.Component;

public class BlockComponent implements Component {
    private String id;
    private long versionNumber = 0;
    private boolean isRemote = false;
    private int health;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isRemote() {
        return isRemote;
    }

    public void setRemote(boolean remote) {
        isRemote = remote;
    }

    public long getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(long versionNumber) {
        this.versionNumber = versionNumber;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }
}
