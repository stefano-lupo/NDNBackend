package com.stefanolupo.ndn;

import net.named_data.jndn.Name;

public enum Names {
    REGISTER("register"),
    PLAYER_STATUS("player-status");

    private final static String PREFIX = "/com/stefanolupo/ndngame";

    private final String value;

    Names(String value) {
        this.value = value;
    }

    public Name getName(String nodeName) {
        return new Name(String.format("%s/%s/%s", PREFIX, nodeName, value));
    }
}
