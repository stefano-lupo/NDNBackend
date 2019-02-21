package com.stefanolupo.ndngame.backend.subscriber;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.protobuf.InvalidProtocolBufferException;
import com.stefanolupo.ndngame.backend.chronosynced.OnPlayersDiscovered;
import com.stefanolupo.ndngame.config.Config;
import com.stefanolupo.ndngame.names.BlockInteractionName;
import com.stefanolupo.ndngame.names.BlockName;
import com.stefanolupo.ndngame.protos.Block;
import com.stefanolupo.ndngame.protos.Blocks;
import com.stefanolupo.ndngame.protos.Player;
import net.named_data.jndn.Data;
import net.named_data.jndn.Face;
import net.named_data.jndn.Interest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

@Singleton
public class BlockSubscriber implements OnPlayersDiscovered {

    private static final Logger LOG = LoggerFactory.getLogger(BlockSubscriber.class);

    private final List<BaseSubscriber<Map<String, Block>>> subscribersList = new ArrayList<>();
    private final Config config;

    @Inject
    public BlockSubscriber(Config config) {
        this.config = config;
    }

    public void addSubscription(BlockName blockName) {
        LOG.info("Adding subscription for {}", blockName);
        BaseSubscriber<Map<String, Block>> subscriber =
                new BaseSubscriber<>(
                        blockName,
                        this::typeFromData,
                        BlockName::new
                );
        subscribersList.add(subscriber);
    }

    public Map<String, Block> getBlocksById() {
        Map<String, Block> map = new HashMap<>();

        for (BaseSubscriber<Map<String, Block>> subscriber : subscribersList) {
            // Can be null before first remote receipt of entity
            if (subscriber.getEntity() == null) {
                continue;
            }
            for (String id : subscriber.getEntity().keySet()) {
                Block block = subscriber.getEntity().get(id);
                map.put(id, block);
            }
        }

        return map;
    }


    public void interactWithBlock(String blockId) {
        for (BaseSubscriber<Map<String, Block>> subscriber : subscribersList) {
            if (subscriber.getEntity().containsKey(blockId)) {
                BlockInteractionName name = new BlockInteractionName(config.getGameId(), blockId);
                Face face = new Face();
                Interest interest = name.toInterest();
                LOG.info("Interacting with block: {}", interest.toUri());
                try {
                    face.expressInterest(interest, (i, d) -> LOG.debug("Got data"));
                } catch (IOException e) {
                    LOG.error("Unable to express interest when interacting with block: {}", name.toInterest().toUri());
                }
            }
        }
    }

    private Map<String, Block> typeFromData(Data data) {
        try {
            Blocks remoteBlocks = Blocks.parseFrom(data.getContent().getImmutableArray());
            return new HashMap<>(remoteBlocks.getBlocksByIdMap());
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException("Unable to parse Block for %s" + data.getName().toUri(), e);
        }
    }

    @Override
    public void onPlayersDiscovered(Set<Player> players) {
        players.forEach(p -> this.addSubscription(new BlockName(config.getGameId(), p.getName())));
    }
}
