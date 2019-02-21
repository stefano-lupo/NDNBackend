package com.stefanolupo.ndngame.backend.publisher;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.stefanolupo.ndngame.backend.ndn.BasePublisherFactory;
import com.stefanolupo.ndngame.backend.ndn.FaceManager;
import com.stefanolupo.ndngame.config.Config;
import com.stefanolupo.ndngame.names.BlockInteractionName;
import com.stefanolupo.ndngame.names.BlockName;
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

    private final BasePublisher publisher;
    private final Face interactionFace;
    private final Map<String, Block> localBlocksById = new HashMap<>();

    @Inject
    public BlockPublisher(Config config,
                          BasePublisherFactory factory,
                          FaceManager faceManager) {
        publisher = factory.create(new BlockName(config.getGameId(), config.getPlayerName()), BlockName::new);
        BlockInteractionName blockInteractionName = new BlockInteractionName(config.getGameId());
        interactionFace = faceManager.getBasicFace(blockInteractionName.getListenPrefix(), this::onInteractionInterest);
    }

    public void updateBlock(String blockId, Block block) {
        localBlocksById.put(blockId, block);
        updateBlob();
    }

    public void removeBlock(String blockId) {
        localBlocksById.remove(blockId);
        updateBlob();
    }

    public void addBlock(Block block) {
        localBlocksById.put(block.getId(), block);
        updateBlob();
    }

    public Map<String, Block> getLocalBlocksById() {
        return localBlocksById;
    }

    private void updateBlob() {
        Blocks blocks = Blocks.newBuilder().putAllBlocksById(localBlocksById).build();
        publisher.updateLatestBlob(new Blob(blocks.toByteArray()));
    }

    public void onInteractionInterest(Name prefix, Interest interest, Face face, long interestFilterId, InterestFilter filter) {
        BlockInteractionName blockInteractionName = new BlockInteractionName(interest);
        Block block = localBlocksById.get(blockInteractionName.getBlockId());
        if (block != null) {
            Block updatedBlock = block.toBuilder()
                    .setHealth(block.getHealth() - 1)
                    .build();
            updateBlock(blockInteractionName.getBlockId(), updatedBlock);
            LOG.info("Updated block: {}", updatedBlock);
        }

    }

}
