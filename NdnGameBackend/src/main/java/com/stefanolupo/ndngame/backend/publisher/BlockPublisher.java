package com.stefanolupo.ndngame.backend.publisher;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.stefanolupo.ndngame.config.Config;
import com.stefanolupo.ndngame.names.BlockName;
import com.stefanolupo.ndngame.protos.Block;
import com.stefanolupo.ndngame.protos.Blocks;
import net.named_data.jndn.util.Blob;

import java.util.HashMap;
import java.util.Map;

@Singleton
public class BlockPublisher {

    private final BasePublisher<BlockName> publisher;
    private final Map<String, Block> localBlocksById = new HashMap<>();

    @Inject
    public BlockPublisher(Config config) {
        publisher = new BasePublisher<>(new BlockName(config.getGameId(), config.getPlayerName()), BlockName::new);
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
}
