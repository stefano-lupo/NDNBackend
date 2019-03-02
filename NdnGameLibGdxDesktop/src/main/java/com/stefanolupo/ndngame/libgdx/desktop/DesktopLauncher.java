package com.stefanolupo.ndngame.libgdx.desktop;

import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.stefanolupo.ndngame.backend.setup.CommandLineHelper;
import com.stefanolupo.ndngame.config.LocalConfig;
import com.stefanolupo.ndngame.libgdx.NdnGame;
import com.stefanolupo.ndngame.libgdx.guice.LibGdxGameModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DesktopLauncher {

	private static final Logger LOG = LoggerFactory.getLogger(DesktopLauncher.class);

	public static void main (String[] args) {
		LocalConfig localConfig = new CommandLineHelper().getConfig(args);
		Injector injector = Guice.createInjector(new LibGdxGameModule(localConfig));

		if (localConfig.isHeadless()) {
			launchHeadlessGame(injector, localConfig);
		} else {
			launchGame(injector, localConfig);
		}
	}

	private static void launchGame(Injector injector, LocalConfig localConfig) {
		LOG.info("Launching Lwjgl Game");
		NdnGame ndnGame = injector.getInstance(NdnGame.class);
		LwjglApplicationConfiguration lwjglConfig= new LwjglApplicationConfiguration();

		lwjglConfig.height = localConfig.getScreenHeight();
		lwjglConfig.width = localConfig.getScreenWidth();
		lwjglConfig.title = localConfig.getPlayerName();

		new LwjglApplication(ndnGame, lwjglConfig);
	}

	private static void launchHeadlessGame(Injector injector, LocalConfig localConfig) {
		LOG.info("Launching headless game");
//		Gdx.gl = mock(GL20.class);
		NdnGame headlessGame = injector.getInstance(NdnGame.class);
		new HeadlessApplication(headlessGame);
	}
}
