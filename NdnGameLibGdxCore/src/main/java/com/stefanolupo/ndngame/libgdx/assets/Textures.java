package com.stefanolupo.ndngame.libgdx.assets;

public enum Textures {
    GRASS("grass"),
    DIRT("dirt"),
    STONE("stone"),
    TNT("tnt");

    private String name;

    Textures(String name) {
        this.name = name;
    }

    public String getName() {
        return String.format("%s_00000", name);
    }
}
