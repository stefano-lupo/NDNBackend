package com.stefanolupo.ndngame.backend;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.stefanolupo.ndngame.backend.chronosynced.PlayerStatusManager;
import com.stefanolupo.ndngame.backend.guice.AutomatedBackendModule;
import com.stefanolupo.ndngame.protos.PlayerStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AutomatedBackend {
    private static final Logger LOG = LoggerFactory.getLogger(AutomatedBackend.class);

    private final PlayerStatusManager playerStatusManager;
    private PlayerStatus playerStatus = PlayerStatus.newBuilder()
            .setX(5)
            .setY(9.5f)
            .build();

    float velX = 0.25f;

    @Inject
    public AutomatedBackend(PlayerStatusManager playerStatusManager) {
        this.playerStatusManager = playerStatusManager;
        playerStatusManager.setPlayerStatusDiscovery(psn -> LOG.info("Discovered new player {}", psn));
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(
                this::movePlayer, 1000, 100, TimeUnit.MILLISECONDS);
    }

    private void movePlayer() {

//        LOG.debug("Desktop: {}", playerStatusManager.getLatestStatus(new PlayerStatusName(0, "desktop")));

        float currentX = playerStatus.getX();

        if (currentX > 18 || currentX < 2) {
            LOG.debug("turning");
            velX *= -1;
        }

        float nextX = currentX + velX;
//        LOG.debug("Next x: {}", nextX);

        playerStatus = playerStatus.toBuilder()
                .setVelX(velX)
                .setX(nextX)
                .build();
        playerStatusManager.updateLocalPlayerStatus(playerStatus);
    }


    public static void main(String[] args) {
        Guice.createInjector(new AutomatedBackendModule(args)).getInstance(AutomatedBackend.class);
    }

}
