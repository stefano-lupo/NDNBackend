package com.stefanolupo.ndngame.libgdx;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;

public class MyAssetManager {

    public final AssetManager assetManager = new AssetManager();

    public static final String playerImage = "img/player.png";
    public static final String enemyImage = "img/enemy.png";

    public static final String BOING_SOUND = "sounds/boing.wav";
    public static final String PING_SOUND = "sounds/ping.wav";

    public static final String music = "music/music.mp3";

    public void queAddImages() {
        assetManager.load(playerImage, Texture.class);
        assetManager.load(enemyImage, Texture.class);
    }

    public void queueAddSounds(){
        assetManager.load(BOING_SOUND, Sound.class);
        assetManager.load(PING_SOUND, Sound.class);
    }

    public void queueAddMusic() {
        assetManager.load(music, Music.class);
    }
}
