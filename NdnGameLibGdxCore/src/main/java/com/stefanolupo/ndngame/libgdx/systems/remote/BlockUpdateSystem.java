package com.stefanolupo.ndngame.libgdx.systems.remote;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IntervalSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.stefanolupo.ndngame.backend.publisher.BlockPublisher;
import com.stefanolupo.ndngame.backend.subscriber.BlockSubscriber;
import com.stefanolupo.ndngame.libgdx.EntityCreator;
import com.stefanolupo.ndngame.libgdx.components.BlockComponent;
import com.stefanolupo.ndngame.libgdx.converters.BlockConverter;
import com.stefanolupo.ndngame.libgdx.systems.HasComponentMappers;
import com.stefanolupo.ndngame.protos.Block;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * Updates / create remote blocks based on BlockSubscriber
 */
@Singleton
public class BlockUpdateSystem extends IntervalSystem implements HasComponentMappers {
    private static final Logger LOG = LoggerFactory.getLogger(BlockUpdateSystem.class);
    private static final float BLOCK_UPDATE_INTERVAL_SEC = 25f / 1000;

    private final BlockSubscriber blockSubscriber;
    private final BlockPublisher blockPublisher;
    private final EntityCreator entityCreator;
    private final PooledEngine engine;

    @Inject
    public BlockUpdateSystem(BlockSubscriber blockSubscriber,
                             BlockPublisher blockPublisher,
                             EntityCreator entityCreator,
                             PooledEngine engine) {
        super(BLOCK_UPDATE_INTERVAL_SEC);

        this.blockSubscriber = blockSubscriber;
        this.blockPublisher = blockPublisher;
        this.entityCreator = entityCreator;
        this.engine = engine;
    }

    @Override
    protected void updateInterval() {
        Map<String, Block> remoteBlocksById = blockSubscriber.getBlocksById();
        Map<String, Block> localBlocksById = blockPublisher.getLocalBlocksById();
        ImmutableArray<Entity> blockEntities = engine.getEntitiesFor(Family.all(BlockComponent.class).get());

        // Index blockEntities for later
        Map<String, Entity> entityMap = new HashMap<>();
        for (Entity e : blockEntities) {
            BlockComponent blockComponent = BLOCK_MAPPER.get(e);
            entityMap.put(blockComponent.getBlockName().getId(), e);
        }

        // Create missing remote blocks
        Set<String> blocksToCreate = Sets.difference(remoteBlocksById.keySet(), entityMap.keySet());
        for (String id : blocksToCreate) {
            Block block = remoteBlocksById.get(id);
            entityCreator.createRemoteBlock(block);
        }

        // Destroy old blocks
        Set<String> blocksToDestroy = Sets.difference(entityMap.keySet(), Sets.union(remoteBlocksById.keySet(), localBlocksById.keySet()));
        for (String id : blocksToDestroy) {
            engine.removeEntity(entityMap.get(id));
        }

        // Update existing blockEntities
        for (Entity blockEntity : blockEntities) {
            BlockComponent blockComponent = BLOCK_MAPPER.get(blockEntity);
            String id = blockComponent.getBlockName().getId();
            Block block = blockComponent.isRemote() ? remoteBlocksById.get(id) : localBlocksById.get(id);
            BlockConverter.reconcileRemoteBlock(blockEntity, block);
        }
    }
}
