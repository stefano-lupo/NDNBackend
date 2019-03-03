package com.stefanolupo.ndngame.libgdx.converters;

import com.badlogic.ashley.core.Entity;
import com.stefanolupo.ndngame.libgdx.components.BlockComponent;
import com.stefanolupo.ndngame.libgdx.systems.HasComponentMappers;
import com.stefanolupo.ndngame.protos.Block;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Maps to and from BlockProto and Game engine entitys
 */
public class BlockConverter implements HasComponentMappers {

    private static final Logger LOG = LoggerFactory.getLogger(BlockConverter.class);

    /**
     * Builds Block based on current state of entity
     */
    public static Block protoFromEntity(Entity entity) {

        BlockComponent blockComponent = BLOCK_MAPPER.get(entity);

        return Block.newBuilder()
                .setId(blockComponent.getBlockName().getId())
                .setGameObject(GameObjectConverter.protoFromEntity(entity))
                .setHealth(blockComponent.getHealth())
                .build();
    }

    /**
     * Update the entitys state based on the remote block
     * The PhysicsSystem updates the render component based on the Body state
     * so only need to update the body
     */
    public static void reconcileRemoteBlock(Entity entity, Block block) {
        BlockComponent blockComponent = BLOCK_MAPPER.get(entity);
        GameObjectConverter.reconcileGameObject(entity, block.getGameObject());
        blockComponent.setHealth(block.getHealth());
    }
}
