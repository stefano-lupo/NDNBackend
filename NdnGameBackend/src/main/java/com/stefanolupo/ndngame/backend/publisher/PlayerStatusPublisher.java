package com.stefanolupo.ndngame.backend.publisher;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.stefanolupo.ndngame.backend.ndn.BasePublisherFactory;
import com.stefanolupo.ndngame.config.LocalConfig;
import com.stefanolupo.ndngame.names.PlayerStatusName;
import com.stefanolupo.ndngame.protos.PlayerStatus;
import net.named_data.jndn.util.Blob;

@Singleton
public class PlayerStatusPublisher {

    private final BasePublisher publisher;

    @Inject
    public PlayerStatusPublisher(LocalConfig localConfig,
                                 BasePublisherFactory factory) {
        PlayerStatusName playerStatusName = new PlayerStatusName(localConfig.getGameId(), localConfig.getPlayerName());
        publisher = factory.create(playerStatusName.getListenName(), PlayerStatusName::new);
    }

    public void updateLocalPlayerStatus(PlayerStatus playerStatus) {
        publisher.updateLatestBlob(new Blob(playerStatus.toByteArray()));
    }
}
