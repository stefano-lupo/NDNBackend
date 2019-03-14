package com.stefanolupo.ndngame.libgdx.creators;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.physics.box2d.Body;
import com.google.inject.Inject;
import com.stefanolupo.ndngame.backend.publisher.BlockPublisher;
import com.stefanolupo.ndngame.config.LocalConfig;
import com.stefanolupo.ndngame.libgdx.assets.GameAssetManager;
import com.stefanolupo.ndngame.libgdx.assets.Textures;
import com.stefanolupo.ndngame.libgdx.components.*;
import com.stefanolupo.ndngame.libgdx.components.enums.Type;
import com.stefanolupo.ndngame.names.blocks.BlockName;
import com.stefanolupo.ndngame.protos.Block;
import com.stefanolupo.ndngame.protos.GameObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class BlockCreator {

    private static final Logger LOG = LoggerFactory.getLogger(BlockCreator.class);

    private static final float BLOCK_WIDTH = 1.5f;
    private static final float BLOCK_HEIGHT = 1.5f;
    private static final int BLOCK_HEALTH = 3;

    private final LocalConfig localConfig;
    private final PooledEngine engine;
    private final BodyFactory bodyFactory;
    private final GameAssetManager gameAssetManager;

    private final BlockPublisher blockPublisher;

    @Inject
    public BlockCreator(LocalConfig localConfig,
                        PooledEngine engine,
                        BodyFactory bodyFactory,
                        GameAssetManager gameAssetManager,

                        //Backend Connections
                        BlockPublisher blockPublisher) {

        this.localConfig = localConfig;
        this.engine = engine;
        this.bodyFactory = bodyFactory;
        this.gameAssetManager = gameAssetManager;

        // Backend Connections
        this.blockPublisher = blockPublisher;
    }

    public void createLocalBlock(float x, float y) {
        String id = UUID.randomUUID().toString();
        GameObject gameObject = GameObjectFactory.buildBasicGameObject(x, y, BLOCK_WIDTH, BLOCK_HEIGHT);
        Block block = Block.newBuilder()
                .setId(id)
                .setGameObject(gameObject)
                .setHealth(BLOCK_HEALTH)
                .build();
        BlockName blockName = new BlockName(localConfig.getGameId(), localConfig.getPlayerName(), id);
        Entity entity = createBlockEntity(blockName, block, false);
        blockPublisher.upsertBlock(blockName, block);
        engine.addEntity(entity);
    }

    public void createRemoteBlock(BlockName blockName, Block block) {
        Entity entity = createBlockEntity(blockName, block, true);
        engine.addEntity(entity);
    }

    private Entity createBlockEntity(BlockName blockName, Block block, boolean isRemote) {
        GameObject gameObject = block.getGameObject();
        LOG.debug("Creating a block at {}, {}, isRemote: {}", gameObject.getX(), gameObject.getY(), isRemote);

        Entity entity = engine.createEntity();
        Body body = bodyFactory.makeBoxBody(gameObject, Material.BLOCK);
        body.setUserData(entity);

        BodyComponent bodyComponent = engine.createComponent(BodyComponent.class);
        bodyComponent.setBody(body);
        entity.add(bodyComponent);

        TypeComponent type = engine.createComponent(TypeComponent.class);
        type.setType(Type.PLACABLE_OBJECT);
        entity.add(type);


        // TODO: Write proper loader
        TextureComponent textureComponent = engine.createComponent(TextureComponent.class);
        textureComponent.setRegion(gameAssetManager.getTexture(Textures.TNT));
        entity.add(textureComponent);

        RenderComponent renderComponent = engine.createComponent(RenderComponent.class);
        renderComponent.setGameObject(gameObject);
        entity.add(renderComponent);

        BlockComponent blockComponent = engine.createComponent(BlockComponent.class);
        blockComponent.setBlockName(blockName);
        blockComponent.setRemote(isRemote);
        blockComponent.setHealth(block.getHealth());
        entity.add(blockComponent);

        return entity;
    }
}
