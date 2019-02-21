package com.stefanolupo.ndngame.libgdx.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.google.inject.Guice;
import com.stefanolupo.ndngame.backend.setup.CommandLineHelper;
import com.stefanolupo.ndngame.config.Config;
import com.stefanolupo.ndngame.libgdx.NdnGame;
import com.stefanolupo.ndngame.libgdx.guice.LibGdxGameModule;

public class DesktopLauncher {
	public static void main (String[] args) {
		Config config = new CommandLineHelper().getConfig(args);
		NdnGame ndnGame = Guice.createInjector(new LibGdxGameModule(config)).getInstance(NdnGame.class);
		LwjglApplicationConfiguration lwjglConfig= new LwjglApplicationConfiguration();
		lwjglConfig.height = 800;
		lwjglConfig.width = 800;
		lwjglConfig.title = config.getPlayerName();

		new LwjglApplication(ndnGame, lwjglConfig);
	}
}
