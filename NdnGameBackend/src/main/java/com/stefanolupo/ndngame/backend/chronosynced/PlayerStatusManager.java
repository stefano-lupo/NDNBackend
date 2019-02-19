//package com.stefanolupo.ndngame.backend.chronosynced;
//
//import com.google.common.collect.Multimap;
//import com.google.common.collect.Multimaps;
//import com.google.inject.Inject;
//import com.google.inject.Singleton;
//import com.google.protobuf.InvalidProtocolBufferException;
//import com.stefanolupo.ndngame.backend.entities.LocalPlayer;
//import com.stefanolupo.ndngame.config.Config;
//import com.stefanolupo.ndngame.names.PlayerStatusName;
//import com.stefanolupo.ndngame.protos.PlayerStatus;
//import net.named_data.jndn.Data;
//import net.named_data.jndn.Interest;
//import net.named_data.jndn.Name;
//import net.named_data.jndn.sync.ChronoSync2013;
//import net.named_data.jndn.util.Blob;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.*;
//import java.util.concurrent.Executors;
//import java.util.concurrent.TimeUnit;
//import java.util.function.Consumer;
//import java.util.stream.Collectors;
//
//@Singleton
//public class PlayerStatusManager extends ChronoSyncedDataStructure {
//
//    private static final Logger LOG = LoggerFactory.getLogger(PlayerStatusManager.class);
//    private static final String BROADCAST_PREFIX = "/com/stefanolupo/ndngame/%d/status/broadcast";
//
//    private final Map<PlayerStatusName, PlayerStatus> remotePlayerMap = new HashMap<>();
//    private final Map<PlayerStatusName, Long> versionByPlayer = new HashMap<>();
//
//    private final LocalPlayer localPlayer;
//
//    // Gross hack temporary
//    private Consumer<PlayerStatusName> playerStatusDiscovery = null;
//
//    @Inject
//    public PlayerStatusManager(LocalPlayer localPlayer, Config config) {
//        super(new Name(String.format(BROADCAST_PREFIX, config.getGameId())),
//                localPlayer.getPlayerStatusName().getListenName());
//
//        this.localPlayer = localPlayer;
//        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(
//                this::publishPlayerStatusChange,
//                1000,
//                30,
//                TimeUnit.MILLISECONDS
//        );
//    }
//
//    @Override
//    public void onData(Interest interest, Data data) {
//        try {
//            PlayerStatusName name = new PlayerStatusName(interest);
//            PlayerStatus status = PlayerStatus.parseFrom(data.getContent().getImmutableArray());
//            if (!remotePlayerMap.containsKey(name)) {
//                LOG.info("First appearance of {}", name.getPlayerName());
//                if (playerStatusDiscovery != null) {
//                    playerStatusDiscovery.accept(name);
//                }
//
//            }
//
//            remotePlayerMap.put(name, status);
//            versionByPlayer.put(name, name.getSequenceNumber());
//
//        } catch (InvalidProtocolBufferException e) {
//            throw new RuntimeException("Unable to parse data received " + data.getName().toUri(), e);
//        }
//    }
//
//    @Override
//    protected Collection<Interest> syncStatesToInterests(List<ChronoSync2013.SyncState> syncStates, boolean isRecovery) {
//        // TODO: I'm not sure if get 1 sync state per user or whether we can get multiple sync states for the same user
//        List<PlayerStatusName> filteredSyncStates = syncStates.stream()
//                .map(PlayerStatusName::new)
//                .filter(psn -> !psn.getPlayerName().equals(localPlayer.getPlayerStatusName().getPlayerName()))
//                .collect(Collectors.toList());
//
//        if (filteredSyncStates.size() > remotePlayerMap.keySet().size() + 1) {
//            LOG.error("Got more sync states than remote players and me");
//        }
//        Multimap<String, PlayerStatusName> multimap = Multimaps.index(filteredSyncStates, PlayerStatusName::getPlayerName);
//
//
//        //TODO: Experiment with must be fresh and timestamps of updates
//        // When they start coming from other nodes
//        return multimap.asMap().entrySet().stream()
//                .map(e -> e.getValue().stream().max(Comparator.comparing(PlayerStatusName::getSequenceNumber)))
//                .filter(Optional::isPresent)
//                .map(opt -> opt.get().toInterest())
//                .collect(Collectors.toList());
//
//    }
//
//    @Override
//    protected Optional<Blob> localToBlob(Interest interest) {
//        return Optional.of(new Blob(localPlayer.getPlayerStatus().toByteArray()));
//    }
//
//    public void updateLocalPlayerStatus(PlayerStatus playerStatus) {
//        localPlayer.setPlayerStatus(playerStatus);
//    }
//
//    public void setPlayerStatusDiscovery(Consumer<PlayerStatusName> playerStatusDiscovery) {
//        this.playerStatusDiscovery = playerStatusDiscovery;
//    }
//
//    public PlayerStatus getLatestStatus(PlayerStatusName name) {
//        return remotePlayerMap.get(name);
//    }
//
//    public long getLatestVersionForPlayer(PlayerStatusName name) {
//        Long latestVersion = versionByPlayer.get(name);
//        return latestVersion != null ? latestVersion : -1;
//    }
//
//    private void publishPlayerStatusChange() {
//        publishUpdate();
//    }
//}
