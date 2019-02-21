package com.stefanolupo.ndngame.libgdx.assets;

public enum SpriteSheet {
    PLAYER("dude"),
    ENEMY("sara");

    private static final String SPRITES_DIR = "./sprites";

    private String directory;

    SpriteSheet(String directory) {
        this.directory = directory;
    }

    public String toJsonName() {
        return String.format("%s/%s/%s.json", SPRITES_DIR, directory, directory);
    }

    public String toAtlasName() {
        return String.format("%s/%s/%s.atlas", SPRITES_DIR, directory, directory);
    }
}
