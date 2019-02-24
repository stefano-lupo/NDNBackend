package com.stefanolupo.ndngame.backend.publisher;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.stefanolupo.ndngame.backend.ndn.BasePublisherFactory;
import com.stefanolupo.ndngame.backend.ndn.FaceManager;
import com.stefanolupo.ndngame.config.Config;
import com.stefanolupo.ndngame.names.blocks.BlockName;
import com.stefanolupo.ndngame.names.blocks.BlocksSyncName;
import com.stefanolupo.ndngame.protos.Block;
import com.stefanolupo.ndngame.protos.Blocks;
import net.named_data.jndn.Face;
import net.named_data.jndn.Interest;
import net.named_data.jndn.InterestFilter;
import net.named_data.jndn.Name;
import net.named_data.jndn.util.Blob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@Singleton
public class BlockPublisher {

    private static final Logger LOG = LoggerFactory.getLogger(BlockPublisher.class);

    private final Map<BlockName, Block> localBlocksByName = new HashMap<>();
    private final BasePublisher publisher;

    @Inject
    public BlockPublisher(Config config,
                          BasePublisherFactory factory,
                          FaceManager faceManager) {
        BlocksSyncName blockSyncName = new BlocksSyncName(config.getGameId(), config.getPlayerName());
        publisher = factory.create(blockSyncName.getAsPrefix(), BlocksSyncName::new);

        BlockName blockName = new BlockName(config.getGameId(), config.getPlayerName());
        faceManager.registerBasicPrefix(blockName.getAsPrefix(), this::onInteractionInterest);
    }

    public void upsertBlock(BlockName blockName, Block block) {
        localBlocksByName.put(blockName, block);
        updateBlob();
    }

    public void upsertBatch(Map<BlockName, Block> blockUpdates) {
        localBlocksByName.putAll(blockUpdates);
        updateBlob();
    }

    public void removeBlock(BlockName blockName) {
        localBlocksByName.remove(blockName);
        updateBlob();
    }

    public Map<BlockName, Block> getLocalBlocks() {
        return localBlocksByName;
    }

    private void updateBlob() {
        Blocks blocks = Blocks.newBuilder().addAllBlocks(localBlocksByName.values()).build();
        publisher.updateLatestBlob(new Blob(blocks.toByteArray()));
    }

    private void onInteractionInterest(Name prefix, Interest interest, Face face, long interestFilterId, InterestFilter filter) {
        BlockName blockInteractionName = new BlockName(interest);
        Block block = localBlocksByName.get(blockInteractionName);
        if (block != null) {
            Block updatedBlock = block.toBuilder()
                    .setHealth(block.getHealth() - 1)
                    .build();
            upsertBlock(blockInteractionName, updatedBlock);
            LOG.debug("Updated block: {}", updatedBlock.getId());
        }
    }
}
