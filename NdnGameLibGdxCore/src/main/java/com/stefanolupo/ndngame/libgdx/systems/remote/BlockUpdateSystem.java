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
import com.stefanolupo.ndngame.libgdx.components.BlockComponent;
import com.stefanolupo.ndngame.libgdx.converters.BlockConverter;
import com.stefanolupo.ndngame.libgdx.creators.BlockCreator;
import com.stefanolupo.ndngame.libgdx.systems.HasComponentMappers;
import com.stefanolupo.ndngame.names.blocks.BlockName;
import com.stefanolupo.ndngame.protos.Block;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Updates / create remote blocks based on BlockSubscriber
 */
@Singleton
public class BlockUpdateSystem extends IntervalSystem implements HasComponentMappers {
    private static final Logger LOG = LoggerFactory.getLogger(BlockUpdateSystem.class);
    private static final float BLOCK_UPDATE_INTERVAL_MS = 25f / 1000;

    private final BlockSubscriber blockSubscriber;
    private final BlockPublisher blockPublisher;
    private final BlockCreator blockCreator;
    private final PooledEngine engine;

    @Inject
    public BlockUpdateSystem(BlockSubscriber blockSubscriber,
                             BlockPublisher blockPublisher,
                             BlockCreator blockCreator,
                             PooledEngine engine) {
        super(BLOCK_UPDATE_INTERVAL_MS);

        this.blockSubscriber = blockSubscriber;
        this.blockPublisher = blockPublisher;
        this.blockCreator = blockCreator;
        this.engine = engine;
    }

    @Override
    protected void updateInterval() {
        Map<BlockName, Block> remoteBlocks = blockSubscriber.getRemoteBlocks();
        Map<BlockName, Block> localBlocks = blockPublisher.getLocalBlocks();
        Map<BlockName, Entity> entitiesByBlockName = getEntitiesByBlockName();

        // Create missing remote blocks
        Set<BlockName> blocksToCreate = Sets.difference(remoteBlocks.keySet(), entitiesByBlockName.keySet());
        for (BlockName blocksName : blocksToCreate) {
            Block block = remoteBlocks.get(blocksName);
            blockCreator.createRemoteBlock(blocksName, block);
        }

        // Destroy old blocks from the engine
        Set<BlockName> blocksToDestroy = Sets.difference(entitiesByBlockName.keySet(), Sets.union(remoteBlocks.keySet(), localBlocks.keySet()));
        for (BlockName blockName : blocksToDestroy) {
            engine.removeEntity(entitiesByBlockName.get(blockName));
        }

        entitiesByBlockName.keySet().removeAll(blocksToDestroy);

        // Reconcile updated remote blocks
        for (BlockName blocksName : entitiesByBlockName.keySet()) {
            BlockComponent blockComponent = BLOCK_MAPPER.get(entitiesByBlockName.get(blocksName));
            if (!blockComponent.isRemote()) {
                continue;
            }

            BlockConverter.reconcileRemoteBlock(entitiesByBlockName.get(blocksName), remoteBlocks.get(blocksName));
        }
    }

    private Map<BlockName, Entity> getEntitiesByBlockName() {
        ImmutableArray<Entity> blockEntities = engine.getEntitiesFor(Family.all(BlockComponent.class).get());
        Map<BlockName, Entity> entityMap = new ConcurrentHashMap<>();
        for (Entity e : blockEntities) {
            BlockComponent blockComponent = BLOCK_MAPPER.get(e);
            entityMap.put(blockComponent.getBlockName(), e);
        }

        return entityMap;
    }
}
