package com.stefanolupo.ndngame.libgdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Disposable;

public class GameAssetManager implements Disposable {

    private static final String GAME_IMAGES_ATLAS = "img/game/skeleton.atlas";
    private static final String BOING_SOUND = "sounds/boing.wav";
    private static final String PING_SOUND = "sounds/ping.wav";
    private static final String MUSIC = "music/music.mp3";

    private final AssetManager assetManager;

    public GameAssetManager() {
        assetManager = new AssetManager();
    }

    public void loadAllAssets() {
        if (Gdx.files == null) {
            throw new RuntimeException("GDX.files is not initialized yet, can't load assets yet.");
        }

        queueAddImages();
        queueAddSounds();
        queueAddMusic();
        finishLoading();
    }

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

    public void finishLoading() {
        assetManager.finishLoading();
    }

    public Music getMusic() {
        return assetManager.get(MUSIC, Music.class);
    }

    public TextureAtlas getGameAtlas() {
        return assetManager.get(GAME_IMAGES_ATLAS, TextureAtlas.class);
    }

    @Override
    public void dispose() {
        assetManager.dispose();
    }
}
