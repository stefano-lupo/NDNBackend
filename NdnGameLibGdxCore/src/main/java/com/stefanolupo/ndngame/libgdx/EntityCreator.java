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
import com.stefanolupo.ndngame.libgdx.assets.SpriteSheet;
import com.stefanolupo.ndngame.libgdx.assets.SpriteSheetLoader;
import com.stefanolupo.ndngame.libgdx.components.*;
import com.stefanolupo.ndngame.libgdx.components.enums.Type;
import com.stefanolupo.ndngame.names.AttackName;
import com.stefanolupo.ndngame.names.PlayerStatusName;
import com.stefanolupo.ndngame.protos.Block;
import com.stefanolupo.ndngame.protos.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.UUID;

@Singleton
public class EntityCreator implements OnPlayersDiscovered {

    private static final Logger LOG = LoggerFactory.getLogger(EntityCreator.class);

    private final Config config;
    private final PooledEngine engine;
    private final SpriteSheetLoader spriteSheetLoader;
    private final BodyFactory bodyFactory;

    // Backend Connections
    private final BlockPublisher blockPublisher;
    private final BlockSubscriber blockSubscriber;

    @Inject
    public EntityCreator(Config config,
                         PooledEngine engine,
                         SpriteSheetLoader spriteSheetLoader,
                         BodyFactory bodyFactory,

                         //Backend Connections
                         BlockPublisher blockPublisher,
                         BlockSubscriber blockSubscriber) {

        this.config = config;
        this.engine = engine;
        this.spriteSheetLoader = spriteSheetLoader;
        this.bodyFactory = bodyFactory;

        // Backend Connections
        this.blockPublisher = blockPublisher;
        this.blockSubscriber = blockSubscriber;
    }

    @Override
    public void onPlayersDiscovered(Set<Player> players) {
        players.forEach(this::createRemotePlayer);
    }

    public void createLocalBlock(float x, float y) {
        Block block = Block.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setX(x)
                .setY(y)
                .setWidth(2f)
                .setHeight(2f)
                .setHealth(5)
                .build();
        Entity entity = createBlockEntity(block, false);
        blockPublisher.addBlock(block);

        // TODO I have no idea why I don't need to add this to the engine..
         engine.addEntity(entity);
    }

    public void createRemoteBlock(Block block) {
        Entity entity = createBlockEntity(block, true);
        // TODO I have no idea why I don't need to add this to the engine..
         engine.addEntity(entity);
    }

    private Entity createBlockEntity(Block block, boolean isRemote) {
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

        BlockComponent blockComponent = engine.createComponent(BlockComponent.class);
        blockComponent.setId(block.getId());
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

        createPlayer(entity, 6);
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


        createPlayer(entity, 8);
    }

    private void createPlayer(Entity entity, float x) {

        Body body = bodyFactory.makeBoxPolyBody(x, 9.5f, 1f, 1.5f, BodyFactory.STONE, BodyDef.BodyType.DynamicBody, true);
        body.setUserData(entity);

        BodyComponent bodyComponent = engine.createComponent(BodyComponent.class);
        bodyComponent.setBody(body);
        entity.add(bodyComponent);

        RenderComponent position = engine.createComponent(RenderComponent.class);
        entity.add(position);

        TextureComponent texture = engine.createComponent(TextureComponent.class);
        entity.add(texture);

        StateComponent state = engine.createComponent(StateComponent.class);
        entity.add(state);

        CollisionComponent collision = engine.createComponent(CollisionComponent.class);
        entity.add(collision);

        engine.addEntity(entity);
    }

    public void createScenery(float x, float y) {
        Entity entity = engine.createEntity();

        Body body = bodyFactory.makeBoxPolyBody(
                x,
                y, 3,
                0.2f,
                BodyFactory.STONE,
                BodyDef.BodyType.StaticBody,
                false);
        body.setUserData(entity);

        BodyComponent bodyComponent = engine.createComponent(BodyComponent.class);
        bodyComponent.setBody(body);
        entity.add(bodyComponent);

        // TODO: Setup correct textures here
//        TextureComponent texture = engine.createComponent(TextureComponent.class);
//        texture.setRegion(atlas.findRegion("player"));
//        entity.add(texture);

        TypeComponent type = engine.createComponent(TypeComponent.class);
        type.setType(Type.SCENERY);
        entity.add(type);

        engine.addEntity(entity);
    }
}
