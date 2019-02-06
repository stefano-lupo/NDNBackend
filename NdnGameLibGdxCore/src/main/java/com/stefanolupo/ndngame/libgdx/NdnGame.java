package com.stefanolupo.ndngame.libgdx;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.stefanolupo.ndngame.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NdnGame extends Game {
//	private static final Logger LOG = LoggerFactory.getLogger(NdnGame.class);
	public final MyAssetManager myAssetManager = new MyAssetManager();

	private MainScreen mainScreen;

	private Music music;

	private SpriteBatch batch;

	public NdnGame() {

	}

	@Override
	public void create () {
		batch = new SpriteBatch();
		mainScreen = new MainScreen(this);
		setScreen(mainScreen);
		myAssetManager.queueAddMusic();
		myAssetManager.assetManager.finishLoading();
		music = myAssetManager.assetManager.get("music/music.mp3", Music.class);
		music.play();
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		screen.dispose();
		myAssetManager.assetManager.dispose();
		music.dispose();
	}
}
