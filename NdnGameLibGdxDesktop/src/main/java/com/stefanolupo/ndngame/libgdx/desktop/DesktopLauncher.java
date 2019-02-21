package com.stefanolupo.ndngame.libgdx.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.stefanolupo.ndngame.backend.setup.CommandLineHelper;
import com.stefanolupo.ndngame.config.Config;
import com.stefanolupo.ndngame.libgdx.NdnGame;
import com.stefanolupo.ndngame.libgdx.guice.LibGdxGameModule;

public class DesktopLauncher {
	public static void main (String[] args) {
		Config config = new CommandLineHelper().getConfig(args);
		Injector injector = Guice.createInjector(new LibGdxGameModule(config));

		if (config.isAutomated() && args == null) {
			launchHeadlessGame(injector, config);
		} else {
			launchGame(injector, config);
		}
	}

	private static void launchGame(Injector injector, Config config) {
		NdnGame ndnGame = injector.getInstance(NdnGame.class);
		LwjglApplicationConfiguration lwjglConfig= new LwjglApplicationConfiguration();

		lwjglConfig.height = config.getScreenHeight();
		lwjglConfig.width = config.getScreenWidth();
		lwjglConfig.title = config.getPlayerName();

		new LwjglApplication(ndnGame, lwjglConfig);
	}

	private static void launchHeadlessGame(Injector injector, Config config) {

	}
}
