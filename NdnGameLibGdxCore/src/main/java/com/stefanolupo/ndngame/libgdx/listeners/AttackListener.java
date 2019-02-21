package com.stefanolupo.ndngame.libgdx.listeners;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.utils.ImmutableArray;
import com.google.inject.Inject;
import com.stefanolupo.ndngame.backend.publisher.BlockPublisher;
import com.stefanolupo.ndngame.backend.subscriber.BlockSubscriber;
import com.stefanolupo.ndngame.libgdx.components.AttackComponent;
import com.stefanolupo.ndngame.libgdx.components.BlockComponent;
import com.stefanolupo.ndngame.libgdx.components.BodyComponent;
import com.stefanolupo.ndngame.libgdx.systems.HasComponentMappers;
import com.stefanolupo.ndngame.protos.Block;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AttackListener implements EntityListener, HasComponentMappers {

    public static final Family FAMILY = Family.all(AttackComponent.class).get();

    private static final Logger LOG = LoggerFactory.getLogger(AttackListener.class);

    private final PooledEngine engine;
    private final BlockSubscriber blockSubscriber;
    private final BlockPublisher blockPublisher;

    @Inject
    public AttackListener(PooledEngine engine,
                          BlockSubscriber blockSubscriber,
                          BlockPublisher blockPublisher) {
        this.engine = engine;
        this.blockSubscriber = blockSubscriber;
        this.blockPublisher = blockPublisher;
    }

    @Override
    public void entityAdded(Entity entity) {
        AttackComponent attackComponent = ATTACK_MAPPER.get(entity);
        LOG.info("Attack added: {}", attackComponent);

        // Handle attack, check if valid etc
        ImmutableArray<Entity> blockEntities = engine.getEntitiesFor(Family.all(BlockComponent.class).get());
        for (Entity blockEntity : blockEntities) {
            BlockComponent blockComponent = BLOCK_MAPPER.get(blockEntity);
            BodyComponent bodyComponent = BODY_MAPPER.get(blockEntity);
            if (intersectsWithBlock(attackComponent, bodyComponent)) {
                if (blockComponent.isRemote()) {
                    // TODO: subscriber update
                } else {
                    blockComponent.setHealth(blockComponent.getHealth() - 1);
                    Block block = blockPublisher.getLocalBlocksById().get(blockComponent.getId());
                    block = block.toBuilder()
                            .setHealth(blockComponent.getHealth())
                            .build();

//                    bodyComponent.getBody().setTransform(block.getX(), bodyComponent.getBody().getPosition().y, 0f);
                    blockPublisher.updateBlock(blockComponent.getId(), block);
                    LOG.info("Published block updat for {}", block.getId());
                }
            }
        }

        engine.removeEntity(entity);

    }

    private boolean intersectsWithBlock(AttackComponent attackComponent, BodyComponent bodyComponent) {
        return true;
    }

    @Override
    public void entityRemoved(Entity entity) {

    }
}
