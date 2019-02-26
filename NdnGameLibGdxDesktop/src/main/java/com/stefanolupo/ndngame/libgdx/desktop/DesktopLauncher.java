package com.stefanolupo.ndngame.libgdx.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.stefanolupo.ndngame.backend.setup.CommandLineHelper;
import com.stefanolupo.ndngame.config.LocalConfig;
import com.stefanolupo.ndngame.libgdx.NdnGame;
import com.stefanolupo.ndngame.libgdx.guice.LibGdxGameModule;

public class DesktopLauncher {
	public static void main (String[] args) {
		LocalConfig localConfig = new CommandLineHelper().getConfig(args);
		Injector injector = Guice.createInjector(new LibGdxGameModule(localConfig));

		if (localConfig.isAutomated() && args == null) {
			launchHeadlessGame(injector, localConfig);
		} else {
			launchGame(injector, localConfig);
		}
	}

	private static void launchGame(Injector injector, LocalConfig localConfig) {
		NdnGame ndnGame = injector.getInstance(NdnGame.class);
		LwjglApplicationConfiguration lwjglConfig= new LwjglApplicationConfiguration();

		lwjglConfig.height = localConfig.getScreenHeight();
		lwjglConfig.width = localConfig.getScreenWidth();
		lwjglConfig.title = localConfig.getPlayerName();

		new LwjglApplication(ndnGame, lwjglConfig);
	}

	private static void launchHeadlessGame(Injector injector, LocalConfig localConfig) {

	}
}
