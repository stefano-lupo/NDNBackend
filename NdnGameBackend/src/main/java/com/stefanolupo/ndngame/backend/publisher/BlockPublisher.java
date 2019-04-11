package com.stefanolupo.ndngame.backend.publisher;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.hubspot.liveconfig.value.Value;
import com.stefanolupo.ndngame.backend.annotations.BackendMetrics;
import com.stefanolupo.ndngame.backend.ndn.BasePublisherFactory;
import com.stefanolupo.ndngame.backend.ndn.FaceManager;
import com.stefanolupo.ndngame.config.LocalConfig;
import com.stefanolupo.ndngame.metrics.MetricNames;
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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Singleton
public class BlockPublisher {

    private static final Logger LOG = LoggerFactory.getLogger(BlockPublisher.class);

    private final ConcurrentMap<BlockName, Block> localBlocksByName = new ConcurrentHashMap<>();
    private final BasePublisher publisher;
    private final Histogram blockPacketSizeHist;

    @Inject
    public BlockPublisher(LocalConfig localConfig,
                          BasePublisherFactory factory,
                          FaceManager faceManager,
                          @BackendMetrics MetricRegistry metrics,
                          @Named("block.publisher.freshness.period.ms") Value<Double> freshnessPeriod) {
        BlocksSyncName blockSyncName = new BlocksSyncName(localConfig.getGameId(), localConfig.getPlayerName());
        publisher = factory.create(blockSyncName.getAsPrefix(), BlocksSyncName::new, freshnessPeriod);

        BlockName blockName = new BlockName(localConfig.getGameId(), localConfig.getPlayerName());
        faceManager.registerBasicPrefix(blockName.getAsPrefix(), this::onInteractionInterest);
        blockPacketSizeHist = metrics.histogram(MetricNames.packetSizeHistogram(MetricNames.PacketSizeType.BLOCK));
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

    public ConcurrentMap<BlockName, Block> getLocalBlocks() {
        return localBlocksByName;
    }

    private void updateBlob() {
        Blocks blocks = Blocks.newBuilder().addAllBlocks(localBlocksByName.values()).build();
        blockPacketSizeHist.update(blocks.getSerializedSize());
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
