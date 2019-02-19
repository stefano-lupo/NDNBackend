package com.stefanolupo.ndngame.libgdx.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Disposable;
import com.google.common.base.Preconditions;
import com.google.inject.Singleton;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class GameAssetManager implements Disposable {

    // Move these to an enum too
    private static final String BOING_SOUND = "sounds/boing.wav";
    private static final String PING_SOUND = "sounds/ping.wav";
    private static final String MUSIC = "music/music.mp3";

    private final Map<SpriteSheet, TextureAtlas> atlasMap = new HashMap<>(SpriteSheet.values().length);

    private final AssetManager assetManager;

    public GameAssetManager() {
        assetManager = new AssetManager();
    }

    public void loadAllAssets() {
        if (Gdx.files == null) {
            throw new RuntimeException("GDX.files is not initialized yet, can't load assets yet.");
        }

        queueSpriteSheets();
        queueAddSounds();
        queueAddMusic();
        finishLoading();
    }

    private void queueSpriteSheets() {
        Arrays.stream(SpriteSheet.values())
                .forEach(ss -> assetManager.load(ss.toAtlasName(), TextureAtlas.class));
    }

    private void queueAddSounds(){
        assetManager.load(BOING_SOUND, Sound.class);
        assetManager.load(PING_SOUND, Sound.class);
    }

    private void queueAddMusic() {
        assetManager.load(MUSIC, Music.class);
    }

    private void finishLoading() {
        assetManager.finishLoading();

        Arrays.stream(SpriteSheet.values()).forEach(ss ->
            atlasMap.put(ss, assetManager.get(ss.toAtlasName()))
        );
    }

    public Music getMusic() {
        return assetManager.get(MUSIC, Music.class);
    }


    public TextureAtlas getAtlas(SpriteSheet spriteSheet) {
        Preconditions.checkArgument(atlasMap.containsKey(spriteSheet),
                "No atlas loaded for sprite sheet %s", spriteSheet);

        return atlasMap.get(spriteSheet);
    }

    @Override
    public void dispose() {
        atlasMap.values().forEach(TextureAtlas::dispose);
        assetManager.dispose();
    }
}
