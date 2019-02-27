package com.stefanolupo.ndngame.libgdx.listeners;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.physics.box2d.Body;
import com.google.inject.Inject;
import com.stefanolupo.ndngame.backend.publisher.BlockPublisher;
import com.stefanolupo.ndngame.backend.subscriber.BlockSubscriber;
import com.stefanolupo.ndngame.libgdx.components.*;
import com.stefanolupo.ndngame.libgdx.converters.BlockConverter;
import com.stefanolupo.ndngame.libgdx.systems.HasComponentMappers;
import com.stefanolupo.ndngame.names.blocks.BlockName;
import com.stefanolupo.ndngame.protos.Attack;
import com.stefanolupo.ndngame.protos.GameObject;
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
        handleAttackedBlock(entity, attackComponent);
        handleAttackedPlayer(entity, attackComponent);
    }

    private void handleAttackedBlock(Entity entity, AttackComponent attackComponent) {

        ImmutableArray<Entity> blockEntities = engine.getEntitiesFor(Family.all(BlockComponent.class).get());

        for (Entity blockEntity : blockEntities) {
            BlockComponent blockComponent = BLOCK_MAPPER.get(blockEntity);
            BodyComponent bodyComponent = BODY_MAPPER.get(blockEntity);
            RenderComponent renderComponent = RENDER_MAPPER.get(blockEntity);

            if (intersectsWithBlock(attackComponent, bodyComponent, renderComponent)) {
                BlockName blockName = blockComponent.getBlockName();
                if (blockComponent.isRemote()) {
                    LOG.debug("Interacting with block {}", blockName.getId());
                    blockSubscriber.interactWithBlock(blockComponent.getBlockName());
                } else {
                    // Perform engine updates
                    LOG.debug("Interacting with local block: {}", blockName.getId());
                    blockComponent.setHealth(blockComponent.getHealth() + 1);
                    blockPublisher.upsertBlock(blockName, BlockConverter.protoFromEntity(blockEntity));
                }

                break;
            }
        }

        engine.removeEntity(entity);
    }

    private void handleAttackedPlayer(Entity entity, AttackComponent attackComponent) {
        ImmutableArray<Entity> remotePlayers = engine.getEntitiesFor(Family.all(RemotePlayerComponent.class).get());

        for (Entity remoteEntity : remotePlayers) {
            RemotePlayerComponent remotePlayerComponent = REMOTE_PLAYER_MAPPER.get(entity);

        }
    }

    private boolean intersectsWithBlock(AttackComponent attackComponent,
                                        BodyComponent bodyComponent,
                                        RenderComponent renderComponent) {
        Body body = bodyComponent.getBody();
        Attack attack = attackComponent.getAttack();
        double distBetweenCenters = Math.sqrt(
                Math.pow(body.getPosition().x - attack.getX(), 2) +
                Math.pow(body.getPosition().y - attack.getY(), 2)
        );

        GameObject gameObject = renderComponent.getGameObject();
        float bodyRadius = Math.max(gameObject.getWidth(), gameObject.getHeight()) / 2;
        if (distBetweenCenters <= bodyRadius + attack.getRadius()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void entityRemoved(Entity entity) {

    }
}
