package com.stefanolupo.ndngame.backend.chronosynced;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.google.protobuf.InvalidProtocolBufferException;
import com.hubspot.liveconfig.resolver.Resolver;
import com.stefanolupo.ndngame.config.LocalConfig;
import com.stefanolupo.ndngame.names.ConfigName;
import com.stefanolupo.ndngame.protos.ConfigMap;
import net.named_data.jndn.Data;
import net.named_data.jndn.Interest;
import net.named_data.jndn.sync.ChronoSync2013;
import net.named_data.jndn.util.Blob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class ConfigManager extends ChronoSynced implements Resolver {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigManager.class);

    private final Map<String, String> configMap;
//    private final ScheduledExecutorService executor;

    public ConfigManager(LocalConfig localConfig, Properties initialProperties) {
        super(ConfigName.getBroadcastName(localConfig.getGameId()),
                new ConfigName(localConfig.getGameId(), localConfig.getPlayerName()).getAsPrefix());
        configMap = new HashMap<>();
        configMap.putAll(Maps.fromProperties(initialProperties));

        LOG.debug("Initialized with properties: {}", configMap);
        // update config every minute
//        executor = Executors.newScheduledThreadPool(1);
//        executor.scheduleWithFixedDelay(this::fetchMap, 5, 5, TimeUnit.SECONDS);
    }

    @Override
    protected java.util.Optional<Blob> localToBlob(Interest interest) {

        if (configMap.isEmpty()) {
            return java.util.Optional.empty();
        }

        ConfigMap configMap = ConfigMap.newBuilder()
                .putAllConfigByName(this.configMap)
                .build();

        return java.util.Optional.of(new Blob(configMap.toByteArray()));
    }

    @Override
    protected Collection<Interest> syncStatesToInterests(List<ChronoSync2013.SyncState> syncStates, boolean isRecovery) {
        return syncStates.stream()
                .map(ConfigName::new)
                .map(ConfigName::toInterest)
                .collect(Collectors.toList());
    }

    @Override
    public void onData(Interest interest, Data data) {
        try {
            ConfigMap updatedMap = ConfigMap.parseFrom(data.getContent().getImmutableArray());
            this.configMap.putAll(updatedMap.getConfigByNameMap());
        } catch (InvalidProtocolBufferException e) {
            LOG.error("Unable to parse ConfigMap: {}", e);
        }
    }

    @Override
    public Optional<String> get(String key) {
//        LOG.debug("Getting {} : {}", key, configMap.get(), get(key));
        return Optional.fromNullable(configMap.get(key));
    }

    @Override
    public Set<String> keySet() {
        return configMap.keySet();
    }
}
