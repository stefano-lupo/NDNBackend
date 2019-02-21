package com.stefanolupo.ndngame.backend.publisher;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.stefanolupo.ndngame.config.Config;
import com.stefanolupo.ndngame.names.BlockInteractionName;
import com.stefanolupo.ndngame.names.BlockName;
import com.stefanolupo.ndngame.protos.Block;
import com.stefanolupo.ndngame.protos.Blocks;
import net.named_data.jndn.*;
import net.named_data.jndn.encoding.EncodingException;
import net.named_data.jndn.security.KeyChain;
import net.named_data.jndn.util.Blob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Singleton
public class BlockPublisher implements OnInterestCallback, OnRegisterFailed {

    private static final Logger LOG = LoggerFactory.getLogger(BlockPublisher.class);

    private final BasePublisher<BlockName> publisher;
    private final Map<String, Block> localBlocksById = new HashMap<>();

    @Inject
    public BlockPublisher(Config config) {
        publisher = new BasePublisher<>(new BlockName(config.getGameId(), config.getPlayerName()), BlockName::new);

        Face face = new Face();
        try {
            KeyChain keyChain = new KeyChain();
            Name cert =  keyChain.getDefaultCertificateName();
            face.setCommandSigningInfo(keyChain,cert);
            Name name = new BlockInteractionName(config.getGameId()).getListenPrefix();
            face.registerPrefix(name, this, this);
            Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(
                    () -> pollFace(face),
                    0,
                    30,
                    TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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

    @Override
    public void onInterest(Name prefix, Interest interest, Face face, long interestFilterId, InterestFilter filter) {
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

    @Override
    public void onRegisterFailed(Name prefix) {
        LOG.error("Failed to register prefix: {}", prefix);
    }

    private void pollFace(Face face) {
        try {
            face.processEvents();
        } catch (IOException | EncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
