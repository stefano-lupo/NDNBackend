package com.stefanolupo.ndngame.libgdx;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.audio.Music;

public class NdnGame extends Game {
//	private static final Logger LOG = LoggerFactory.getLogger(NdnGame.class);
	public final MyAssetManager myAssetManager = new MyAssetManager();

	private MainScreen mainScreen;

	private Music music;


	public NdnGame() {

	}

	@Override
	public void create () {
		mainScreen = new MainScreen(this);
		setScreen(mainScreen);
		myAssetManager.queueAddMusic();
		myAssetManager.assetManager.finishLoading();
//		music = myAssetManager.assetManager.get("music/music.mp3", Music.class);
//		music.play();
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		screen.dispose();
		myAssetManager.assetManager.dispose();
		music.dispose();
	}
}
