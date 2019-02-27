package com.stefanolupo.ndngame.backend.publisher;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.hubspot.liveconfig.value.Value;
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
                                 BasePublisherFactory factory,
                                 @Named("player.status.publisher.freshness.period.ms") Value<Double> freshnessPeriod) {
        PlayerStatusName playerStatusName = new PlayerStatusName(localConfig.getGameId(), localConfig.getPlayerName());
        publisher = factory.create(playerStatusName.getListenName(), PlayerStatusName::new, freshnessPeriod);
    }

    public void updateLocalPlayerStatus(PlayerStatus playerStatus) {
        publisher.updateLatestBlob(new Blob(playerStatus.toByteArray()));
    }
}
