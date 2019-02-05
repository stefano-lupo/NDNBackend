package com.stefanolupo.ndngame.libgdx.desktop;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.google.inject.Guice;
import com.stefanolupo.ndngame.backend.setup.CommandLineHelper;
import com.stefanolupo.ndngame.config.Config;
import com.stefanolupo.ndngame.libgdx.LibGdxGame;
import com.stefanolupo.ndngame.libgdx.guice.LibGdxGameModule;

public class DesktopLauncher {
	public static void main (String[] args) {
		Config config = new CommandLineHelper().getConfig(args);
		LibGdxGame game = Guice.createInjector(new LibGdxGameModule(config)).getInstance(LibGdxGame.class);
		LwjglApplicationConfiguration lwjglConfig= new LwjglApplicationConfiguration();
		new LwjglApplication(game, lwjglConfig);
	}
}
