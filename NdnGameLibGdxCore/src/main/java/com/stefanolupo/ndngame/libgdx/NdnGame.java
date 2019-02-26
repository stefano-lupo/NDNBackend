package com.stefanolupo.ndngame.libgdx;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.audio.Music;
import com.google.inject.Inject;
import com.stefanolupo.ndngame.config.LocalConfig;
import com.stefanolupo.ndngame.libgdx.assets.GameAssetManager;

public class NdnGame extends Game {


	private final LocalConfig localConfig;
	private final GameAssetManager gameAssetManager;
	private final MainScreen mainScreen;
	private Music music;

	@Inject
	public NdnGame(LocalConfig localConfig, GameAssetManager gameAssetManager, MainScreen mainScreen) {
		this.localConfig = localConfig;
		this.gameAssetManager = gameAssetManager;
		this.mainScreen = mainScreen;
	}

	@Override
	public void create () {
		gameAssetManager.loadAllAssets();
		music = gameAssetManager.getMusic();
//		music.play();

		// This must be updateMotionState after loading all the assets for the screen
		setScreen(mainScreen);
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		screen.dispose();
		gameAssetManager.dispose();
		music.dispose();
	}
}
