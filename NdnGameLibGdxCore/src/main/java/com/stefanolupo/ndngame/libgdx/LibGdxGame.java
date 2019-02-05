package com.stefanolupo.ndngame.libgdx;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.google.inject.Inject;
import com.stefanolupo.ndngame.config.Config;

public class LibGdxGame extends Game {
	private SpriteBatch batch;
	private MainScreen mainScreen;

	@Inject
	public LibGdxGame(Config config) {
		System.out.println(config);
	}

	@Override
	public void create () {
		batch = new SpriteBatch();
		mainScreen = new MainScreen(this);
		setScreen(mainScreen);
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		screen.dispose();
	}
}
