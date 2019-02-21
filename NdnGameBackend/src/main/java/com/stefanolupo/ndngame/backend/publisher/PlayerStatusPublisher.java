package com.stefanolupo.ndngame.backend.publisher;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.stefanolupo.ndngame.config.Config;
import com.stefanolupo.ndngame.names.PlayerStatusName;
import com.stefanolupo.ndngame.protos.PlayerStatus;
import net.named_data.jndn.util.Blob;

@Singleton
public class PlayerStatusPublisher {

    private final BasePublisher<PlayerStatusName> publisher;

    @Inject
    public PlayerStatusPublisher(Config config) {
        publisher = new BasePublisher<>(new PlayerStatusName(config.getGameId(), config.getPlayerName()), PlayerStatusName::new);
    }

    public void updateLocalPlayerStatus(PlayerStatus playerStatus) {
        publisher.updateLatestBlob(new Blob(playerStatus.toByteArray()));
    }

//    private static Name getSyncName(Config config) {
//        return new PlayerStatusName(config.getGameId(), config.getPlayerName()).getNameWithoutSequenceNumber();
//    }
}
