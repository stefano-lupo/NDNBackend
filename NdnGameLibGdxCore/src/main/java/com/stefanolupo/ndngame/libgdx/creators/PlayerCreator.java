package com.stefanolupo.ndngame.libgdx.creators;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.physics.box2d.Body;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.stefanolupo.ndngame.backend.chronosynced.OnPlayersDiscovered;
import com.stefanolupo.ndngame.config.LocalConfig;
import com.stefanolupo.ndngame.libgdx.assets.SpriteSheet;
import com.stefanolupo.ndngame.libgdx.assets.SpriteSheetLoader;
import com.stefanolupo.ndngame.libgdx.components.*;
import com.stefanolupo.ndngame.libgdx.components.enums.Type;
import com.stefanolupo.ndngame.names.PlayerStatusName;
import com.stefanolupo.ndngame.protos.GameObject;
import com.stefanolupo.ndngame.protos.Player;
import com.stefanolupo.ndngame.protos.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static com.stefanolupo.ndngame.libgdx.creators.GameWorldCreator.WORLD_HEIGHT;
import static com.stefanolupo.ndngame.libgdx.creators.GameWorldCreator.WORLD_WIDTH;

@Singleton
public class PlayerCreator implements OnPlayersDiscovered {

    public static final float PLAYER_WIDTH = 1;
    public static final float PLAYER_HEIGHT = 1.5f;

    private static final Logger LOG = LoggerFactory.getLogger(PlayerCreator.class);
    private static final int MIN_EDGE_DISTANCE = 10;

    private final LocalConfig localConfig;
    private final PooledEngine engine;
    private final SpriteSheetLoader spriteSheetLoader;
    private final BodyFactory bodyFactory;

    @Inject
    public PlayerCreator(LocalConfig localConfig,
                         PooledEngine engine,
                         SpriteSheetLoader spriteSheetLoader,
                         BodyFactory bodyFactory) {
        this.localConfig = localConfig;
        this.engine = engine;
        this.spriteSheetLoader = spriteSheetLoader;
        this.bodyFactory = bodyFactory;
    }

    @Override
    public void onPlayersDiscovered(Set<Player> players) {
        players.forEach(this::createRemotePlayer);
    }


    public void createLocalPlayer() {

        Entity entity = engine.createEntity();
        LocalPlayerComponent player = engine.createComponent(LocalPlayerComponent.class);
        entity.add(player);

        if (!localConfig.isHeadless()) {
            entity.add(spriteSheetLoader.buildAnimationComponent(SpriteSheet.PLAYER));
        }

        TypeComponent type = engine.createComponent(TypeComponent.class);
        type.setType(Type.PLAYER);
        entity.add(type);

        StatusComponent statusComponent = engine.createComponent(StatusComponent.class);
        statusComponent.setStatus(buildStatus());
        entity.add(statusComponent);

        createPlayer(entity);
    }

    public void createRemotePlayer(Player player) {
        LOG.debug("Creating remote player: {}", player);
        PlayerStatusName playerStatusName = new PlayerStatusName(localConfig.getGameId(), player.getName());

        Entity entity = engine.createEntity();

        RemotePlayerComponent remotePlayerComponent = engine.createComponent(RemotePlayerComponent.class);
        remotePlayerComponent.setPlayerStatusName(playerStatusName);
        entity.add(remotePlayerComponent);

        if (!localConfig.isHeadless()) {
            entity.add(spriteSheetLoader.buildAnimationComponent(SpriteSheet.ENEMY));
        }

        TypeComponent type = engine.createComponent(TypeComponent.class);
        type.setType(Type.REMOTE_PLAYER);
        entity.add(type);

        // We don't know their status just yet as this is just discovery
        StatusComponent statusComponent = engine.createComponent(StatusComponent.class);
        statusComponent.setStatus(buildStatus());
        entity.add(statusComponent);

        createPlayer(entity);
    }

    private void createPlayer(Entity entity) {
        float x = ThreadLocalRandom.current().nextInt(MIN_EDGE_DISTANCE, (int) WORLD_WIDTH - MIN_EDGE_DISTANCE);
        float y = ThreadLocalRandom.current().nextInt(MIN_EDGE_DISTANCE, (int) WORLD_HEIGHT - MIN_EDGE_DISTANCE);

        GameObject gameObject = GameObjectFactory.buildBasicGameObject(x, y, PLAYER_WIDTH, PLAYER_HEIGHT);
        Body body = bodyFactory.makeBoxBody(gameObject, Material.PLAYER);
        body.setUserData(entity);

        BodyComponent bodyComponent = engine.createComponent(BodyComponent.class);
        bodyComponent.setBody(body);
        entity.add(bodyComponent);

        RenderComponent renderComponent = engine.createComponent(RenderComponent.class);
        renderComponent.setGameObject(gameObject);
        entity.add(renderComponent);

        TextureComponent texture = engine.createComponent(TextureComponent.class);
        entity.add(texture);

        StateComponent state = engine.createComponent(StateComponent.class);
        entity.add(state);

        CollisionComponent collision = engine.createComponent(CollisionComponent.class);
        entity.add(collision);

        engine.addEntity(entity);
    }

    private static Status buildStatus() {
        return Status.newBuilder()
                .setAmmo(50)
                .setHealth(5)
                .setMana(5)
                .setXp(0)
                .build();
    }

}
