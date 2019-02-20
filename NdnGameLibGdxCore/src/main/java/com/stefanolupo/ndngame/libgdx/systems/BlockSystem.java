package com.stefanolupo.ndngame.libgdx.systems;

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
import com.stefanolupo.ndngame.protos.Block;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Singleton
public class BlockSystem extends IntervalSystem implements HasComponentMappers {
    private static final float BLOCK_UPDATE_INTERVAL_SEC = 5f / 1000;
    private static final Logger LOG = LoggerFactory.getLogger(BlockSystem.class);

    private final BlockSubscriber blockSubscriber;
    private final BlockPublisher blockPublisher;
    private final EntityCreator entityCreator;
    private final PooledEngine engine;

    @Inject
    public BlockSystem(BlockSubscriber blockSubscriber,
                       BlockPublisher blockPublisher,
                       EntityCreator entityCreator,
                       PooledEngine engine) {
        super(BLOCK_UPDATE_INTERVAL_SEC);

        this.blockSubscriber = blockSubscriber;
        this.blockPublisher = blockPublisher;
        this.entityCreator = entityCreator;
        this.engine = engine;
    }

//    @Override
//    public void addedToEngine(Engine engine) {
//        engine.getEntitiesFor(Family.all(BlockComponent.class).get());
//    }

    @Override
    protected void updateInterval() {
        Map<String, Block> remoteBlocksById = blockSubscriber.getBlocksById();
        Map<String, Block> localBlocksById = blockPublisher.getLocalBlocksById();
        ImmutableArray<Entity> entities = engine.getEntitiesFor(Family.all(BlockComponent.class).get());

        // Index entities for later
        Map<String, Entity> entityMap = new HashMap<>();
        for (Entity e : entities) {
            BlockComponent blockComponent = BLOCK_MAPPER.get(e);
            entityMap.put(blockComponent.getId(), e);
        }

        // Just a check
        Set<String> overlappingBlockIds = Sets.intersection(
                remoteBlocksById.keySet(),
                localBlocksById.keySet());
        if (!overlappingBlockIds.isEmpty()) {
            LOG.error("Found overlapping remote / local block IDs: {}", overlappingBlockIds);
        }

        // Remote blocks are currently created in EntityCreator
        Set<String> blocksToCreate = Sets.difference(remoteBlocksById.keySet(), entityMap.keySet());
        Set<String> blocksToDestroy = Sets.difference(entityMap.keySet(), Sets.union(remoteBlocksById.keySet(), localBlocksById.keySet()));


        // Update existing entities
        for (Entity e : entities) {
            BlockComponent blockComponent = BLOCK_MAPPER.get(e);

            String id = blockComponent.getId();
            Block block = blockComponent.isRemote() ? remoteBlocksById.get(id) : localBlocksById.get(id);
            if (block == null) {
                // Hopefully catch this on the next one
                LOG.error("Got null block for {}", e);
                continue;
            }
            blockComponent.setHealth(block.getHealth());
        }

        // Create missing remote blocks
        for (String id : blocksToCreate) {
            Block block = remoteBlocksById.get(id);
            entityCreator.createRemoteBlock(block);
        }

        // Delete old blocks
        for (String id : blocksToDestroy) {
            engine.removeEntity(entityMap.get(id));
        }
    }

}
