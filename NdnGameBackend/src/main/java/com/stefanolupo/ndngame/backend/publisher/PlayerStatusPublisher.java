package com.stefanolupo.ndngame.backend.publisher;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.stefanolupo.ndngame.config.Config;
import com.stefanolupo.ndngame.protos.PlayerStatus;
import net.named_data.jndn.Name;

@Singleton
public class PlayerStatusPublisher extends BasePublisher<PlayerStatus> {

    private static final String STATUS_SYNC_PREFIX = "/com/stefanolupo/ndngame/%d/%s/status";

    @Inject
    public PlayerStatusPublisher(Config config) {
        super(getSyncName(config), PlayerStatus.newBuilder().build());
    }

    @Override
    protected byte[] entityToByteArray(PlayerStatus entity) {
        return entity.toByteArray();
    }

    private static Name getSyncName(Config config) {
        return new Name(String.format(STATUS_SYNC_PREFIX, config.getGameId(), config.getPlayerName()));
    }
}
