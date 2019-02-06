package com.stefanolupo.ndngame.libgdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;

public class MyAssetManager {

    public final AssetManager assetManager = new AssetManager();

    public final String playerImage = "img/player.png";
    public final String enemyImage = "img/enemy.png";

    public final String boingSound = "sounds/boing.wav";
    public final String pingSound = "sounds/ping.wav";

    public final String music = "music/music.mp3";

    public void queAddImages() {
        assetManager.load(playerImage, Texture.class);
        assetManager.load(enemyImage, Texture.class);
    }

    public void queueAddSounds(){
        assetManager.load(boingSound, Sound.class);
        assetManager.load(pingSound, Sound.class);
    }

    public void queueAddMusic() {
        assetManager.load(music, Music.class);
    }
}
