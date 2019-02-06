package com.stefanolupo.ndngame.libgdx;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class MyAssetManager {

    public static final String GAME_IMAGES_ATLAS = "img/game/game.atlas";
    public static final String BOING_SOUND = "sounds/boing.wav";
    public static final String PING_SOUND = "sounds/ping.wav";
    public static final String MUSIC = "music/music.mp3";

    public final AssetManager assetManager = new AssetManager();

    public void queueAddImages() {
        assetManager.load(GAME_IMAGES_ATLAS, TextureAtlas.class);
    }

    public void queueAddSounds(){
        assetManager.load(BOING_SOUND, Sound.class);
        assetManager.load(PING_SOUND, Sound.class);
    }

    public void queueAddMusic() {
        assetManager.load(MUSIC, Music.class);
    }
}
