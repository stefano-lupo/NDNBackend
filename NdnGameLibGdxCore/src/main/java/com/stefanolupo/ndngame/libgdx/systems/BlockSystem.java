package com.stefanolupo.ndngame.libgdx.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IntervalSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.physics.box2d.Body;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.stefanolupo.ndngame.backend.publisher.BlockPublisher;
import com.stefanolupo.ndngame.backend.subscriber.BlockSubscriber;
import com.stefanolupo.ndngame.libgdx.EntityCreator;
import com.stefanolupo.ndngame.libgdx.assets.GameAssetManager;
import com.stefanolupo.ndngame.libgdx.assets.Textures;
import com.stefanolupo.ndngame.libgdx.components.AttackComponent;
import com.stefanolupo.ndngame.libgdx.components.BlockComponent;
import com.stefanolupo.ndngame.libgdx.components.TextureComponent;
import com.stefanolupo.ndngame.protos.Block;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Singleton
public class BlockSystem extends IntervalSystem implements HasComponentMappers {
    private static final float BLOCK_UPDATE_INTERVAL_SEC = 25f / 1000;
    private static final Logger LOG = LoggerFactory.getLogger(BlockSystem.class);

    private final BlockSubscriber blockSubscriber;
    private final BlockPublisher blockPublisher;
    private final EntityCreator entityCreator;
    private final PooledEngine engine;
    private final GameAssetManager gameAssetManager;

    @Inject
    public BlockSystem(BlockSubscriber blockSubscriber,
                       BlockPublisher blockPublisher,
                       EntityCreator entityCreator,
                       PooledEngine engine,
                       GameAssetManager gameAssetManager) {
        super(BLOCK_UPDATE_INTERVAL_SEC);

        this.blockSubscriber = blockSubscriber;
        this.blockPublisher = blockPublisher;
        this.entityCreator = entityCreator;
        this.engine = engine;
        this.gameAssetManager = gameAssetManager;
    }

//    @Override
//    public void addedToEngine(Engine engine) {
//        engine.getEntitiesFor(Family.all(BlockComponent.class).get());
//    }

    @Override
    protected void updateInterval() {
        Map<String, Block> remoteBlocksById = blockSubscriber.getBlocksById();
        Map<String, Block> localBlocksById = blockPublisher.getLocalBlocksById();
        ImmutableArray<Entity> blockEntities = engine.getEntitiesFor(Family.all(BlockComponent.class).get());
        ImmutableArray<Entity> attackEntities = engine.getEntitiesFor(Family.all(AttackComponent.class).get());

        // Index blockEntities for later
        Map<String, Entity> entityMap = new HashMap<>();
        for (Entity e : blockEntities) {
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

        createMissingRemoteBlocks(remoteBlocksById, entityMap);
        Set<String> blocksToDestroy = Sets.difference(entityMap.keySet(), Sets.union(remoteBlocksById.keySet(), localBlocksById.keySet()));
        for (String id : blocksToDestroy) {
            engine.removeEntity(entityMap.get(id));
        }

        // Update existing blockEntities
        for (Entity blockEntity : blockEntities) {
            BlockComponent blockComponent = BLOCK_MAPPER.get(blockEntity);

            String id = blockComponent.getId();
            Block block = blockComponent.isRemote() ? remoteBlocksById.get(id) : localBlocksById.get(id);
            if (block == null) {
                // Hopefully catch this on the next one
                LOG.error("Got null block for {}", blockEntity);
                continue;
            }

            blockComponent.setHealth(block.getHealth());
            Body body = BODY_MAPPER.get(blockEntity).getBody();
            Block protoBlock;
            if (blockComponent.isRemote()) {
                protoBlock = remoteBlocksById.get(id);
            } else {
                protoBlock = localBlocksById.get(id);
            }

            // Just temp to test block update
            body.setTransform(protoBlock.getX(), protoBlock.getY(), 0);
            int health = protoBlock.getHealth();
            health = Math.max(0, Math.min(health, Textures.values().length) - 1);

            TextureComponent textureComponent = TEXTURE_MAPPER.get(blockEntity);
            textureComponent.setRegion(gameAssetManager.getTexture(Textures.values()[health]));
        }



    }

    private void createMissingRemoteBlocks(Map<String, Block> remoteBlocksById, Map<String, Entity> entityMap) {
        Set<String> blocksToCreate = Sets.difference(remoteBlocksById.keySet(), entityMap.keySet());
        // Create missing remote blocks
        for (String id : blocksToCreate) {
            LOG.debug("Had {} blocks to create", blocksToCreate.size());
            Block block = remoteBlocksById.get(id);
            entityCreator.createRemoteBlock(block);
        }
    }

}
