package com.stefanolupo.ndngame.libgdx;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.stefanolupo.ndngame.backend.chronosynced.OnPlayersDiscovered;
import com.stefanolupo.ndngame.backend.publisher.BlockPublisher;
import com.stefanolupo.ndngame.backend.subscriber.BlockSubscriber;
import com.stefanolupo.ndngame.config.Config;
import com.stefanolupo.ndngame.libgdx.assets.GameAssetManager;
import com.stefanolupo.ndngame.libgdx.assets.SpriteSheet;
import com.stefanolupo.ndngame.libgdx.assets.SpriteSheetLoader;
import com.stefanolupo.ndngame.libgdx.assets.Textures;
import com.stefanolupo.ndngame.libgdx.components.*;
import com.stefanolupo.ndngame.libgdx.components.enums.Type;
import com.stefanolupo.ndngame.names.AttackName;
import com.stefanolupo.ndngame.names.BlockName;
import com.stefanolupo.ndngame.names.PlayerStatusName;
import com.stefanolupo.ndngame.protos.Block;
import com.stefanolupo.ndngame.protos.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Singleton
public class EntityCreator implements OnPlayersDiscovered {

    private static final Logger LOG = LoggerFactory.getLogger(EntityCreator.class);

    public static final float WORLD_WIDTH = 50;
    public static final float WORLD_HEIGHT = 50;

    public static final float PLAYER_WIDTH = 1;
    public static final float PLAYER_HEIGHT = 1.5f;
    public static final float PLAYER_SCALE_X = 1;
    public static final float PLAYER_SCALE_Y = 1;

    public static final float BLOCK_WIDTH = 2;
    public static final float BLOCK_HEIGHT = 2;


    private final Config config;
    private final PooledEngine engine;
    private final SpriteSheetLoader spriteSheetLoader;
    private final BodyFactory bodyFactory;
    private final GameAssetManager gameAssetManager;

    // Backend Connections
    private final BlockPublisher blockPublisher;
    private final BlockSubscriber blockSubscriber;

    @Inject
    public EntityCreator(Config config,
                         PooledEngine engine,
                         SpriteSheetLoader spriteSheetLoader,
                         BodyFactory bodyFactory,
                         GameAssetManager gameAssetManager,

                         //Backend Connections
                         BlockPublisher blockPublisher,
                         BlockSubscriber blockSubscriber) {

        this.config = config;
        this.engine = engine;
        this.spriteSheetLoader = spriteSheetLoader;
        this.bodyFactory = bodyFactory;
        this.gameAssetManager = gameAssetManager;

        // Backend Connections
        this.blockPublisher = blockPublisher;
        this.blockSubscriber = blockSubscriber;
    }

    @Override
    public void onPlayersDiscovered(Set<Player> players) {
        players.forEach(this::createRemotePlayer);
    }

    public void createInitialWorld() {
        createWorldBoundary();
        createLocalPlayer();
    }

    public void createLocalBlock(float x, float y) {
        Block block = Block.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setX(x)
                .setY(y)
                .setWidth(BLOCK_WIDTH)
                .setHeight(BLOCK_HEIGHT)
                .setHealth(5)
                .build();
        BlockName blockName = new BlockName(config.getGameId(), config.getPlayerName());
        Entity entity = createBlockEntity(block, blockName, false);
        blockPublisher.addBlock(block);
        engine.addEntity(entity);
    }

    public void createRemoteBlock(Block block) {
        Entity entity = createBlockEntity(block, true);
        engine.addEntity(entity);
    }

    private Entity createBlockEntity(Block block, BlockName blockName, boolean isRemote) {
        LOG.debug("Creating a block at {}, {}, isRemote: {}", block.getX(), block.getY(), isRemote);

        Entity entity = engine.createEntity();

        Body body = bodyFactory.makeBoxPolyBody(block.getX(), block.getY(), block.getWidth(), block.getHeight(), BodyFactory.STONE, BodyDef.BodyType.StaticBody);
        body.setUserData(entity);

        BodyComponent bodyComponent = engine.createComponent(BodyComponent.class);
        bodyComponent.setBody(body);
        entity.add(bodyComponent);

        TypeComponent type = engine.createComponent(TypeComponent.class);
        type.setType(Type.SCENERY);
        entity.add(type);


        // TODO: Write proper loader
        TextureComponent textureComponent = engine.createComponent(TextureComponent.class);
        textureComponent.setRegion(gameAssetManager.getTexture(Textures.TNT));
        entity.add(textureComponent);

        RenderComponent renderComponent = engine.createComponent(RenderComponent.class);
        renderComponent.setWidth(BLOCK_WIDTH);
        renderComponent.setHeight(BLOCK_HEIGHT);
        entity.add(renderComponent);

        BlockComponent blockComponent = engine.createComponent(BlockComponent.class);
        blockComponent.setBlockName(blockName);
        blockComponent.setRemote(isRemote);
        blockComponent.setHealth(block.getHealth());
        entity.add(blockComponent);

        return entity;
    }

    public void createLocalPlayer() {

        Entity entity = engine.createEntity();
        LocalPlayerComponent player = engine.createComponent(LocalPlayerComponent.class);
        entity.add(player);

        entity.add(spriteSheetLoader.buildAnimationComponent(SpriteSheet.PLAYER));

        TypeComponent type = engine.createComponent(TypeComponent.class);
        type.setType(Type.PLAYER);
        entity.add(type);

        createPlayer(entity);
    }

    public void createRemotePlayer(Player player) {
        LOG.debug("Creating remote player: {}", player);
        PlayerStatusName playerStatusName = new PlayerStatusName(config.getGameId(), player.getName());
        Entity entity = engine.createEntity();
        RemotePlayerComponent remotePlayerComponent = engine.createComponent(RemotePlayerComponent.class);
        remotePlayerComponent.setPlayerStatusName(playerStatusName);
        remotePlayerComponent.setAttackName(new AttackName(config.getGameId(), playerStatusName.getPlayerName()));
        entity.add(remotePlayerComponent);

        entity.add(spriteSheetLoader.buildAnimationComponent(SpriteSheet.ENEMY));

        TypeComponent type = engine.createComponent(TypeComponent.class);
        type.setType(Type.REMOTE_PLAYER);
        entity.add(type);


        createPlayer(entity);
    }

    private void createPlayer(Entity entity) {
        float x = ThreadLocalRandom.current().nextInt(20, (int) WORLD_WIDTH - 20);
        float y = ThreadLocalRandom.current().nextInt(20, (int) WORLD_HEIGHT - 20);

        Body body = bodyFactory.makeBoxPolyBody(x, y, PLAYER_WIDTH, PLAYER_HEIGHT, BodyFactory.STONE, BodyDef.BodyType.DynamicBody, true);
        body.setUserData(entity);

        BodyComponent bodyComponent = engine.createComponent(BodyComponent.class);
        bodyComponent.setBody(body);
        entity.add(bodyComponent);

        RenderComponent renderComponent = engine.createComponent(RenderComponent.class);
        renderComponent.setWidth(PLAYER_WIDTH);
        renderComponent.setHeight(PLAYER_HEIGHT);
        renderComponent.setScale(PLAYER_SCALE_X, PLAYER_SCALE_Y);
        renderComponent.setRotation(5);
        entity.add(renderComponent);

        TextureComponent texture = engine.createComponent(TextureComponent.class);
        entity.add(texture);

        StateComponent state = engine.createComponent(StateComponent.class);
        entity.add(state);

        CollisionComponent collision = engine.createComponent(CollisionComponent.class);
        entity.add(collision);

        engine.addEntity(entity);
    }

    private void createWorldBoundary() {
        Entity entity = engine.createEntity();

        Body body = bodyFactory.makeBoundary();
        body.setUserData(entity);
        BodyComponent bodyComponent = engine.createComponent(BodyComponent.class);
        bodyComponent.setBody(body);
        entity.add(bodyComponent);

        TypeComponent typeComponent =engine.createComponent(TypeComponent.class);
        typeComponent.setType(Type.BOUNDARY);
        entity.add(typeComponent);

        CollisionComponent collisionComponent = engine.createComponent(CollisionComponent.class);
        entity.add(collisionComponent);

        engine.addEntity(entity);
    }
}
