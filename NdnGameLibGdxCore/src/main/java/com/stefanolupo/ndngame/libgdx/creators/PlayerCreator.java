package com.stefanolupo.ndngame.libgdx.creators;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.PooledEngine;
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

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static com.stefanolupo.ndngame.libgdx.creators.GameWorldCreator.WORLD_HEIGHT;
import static com.stefanolupo.ndngame.libgdx.creators.GameWorldCreator.WORLD_WIDTH;

@Singleton
public class PlayerCreator implements OnPlayersDiscovered {

    public static final float PLAYER_WIDTH = 1;
    public static final float PLAYER_HEIGHT = 1.5f;
    public static final int MAX_HEALTH = 20;
    public static final int MAX_AMMO = 100;

    private static final Logger LOG = LoggerFactory.getLogger(PlayerCreator.class);
    private static final int MIN_EDGE_DISTANCE = 10;

    private final LocalConfig localConfig;
    private final PooledEngine engine;
    private final SpriteSheetLoader spriteSheetLoader;
    private final BodyFactory bodyFactory;
    private final EntityManager entityManager;

    @Inject
    public PlayerCreator(LocalConfig localConfig,
                         PooledEngine engine,
                         SpriteSheetLoader spriteSheetLoader,
                         BodyFactory bodyFactory,
                         EntityManager entityManager) {
        this.localConfig = localConfig;
        this.engine = engine;
        this.spriteSheetLoader = spriteSheetLoader;
        this.bodyFactory = bodyFactory;
        this.entityManager = entityManager;
    }

    @Override
    public void onPlayersDiscovered(Set<Player> players) {
        players.forEach(this::createRemotePlayer);
    }


    public void createLocalPlayer() {
        Set<Component> components = new HashSet<>();
        LocalPlayerComponent player = engine.createComponent(LocalPlayerComponent.class);
        components.add(player);

        if (!localConfig.isHeadless()) {
            components.add(spriteSheetLoader.buildAnimationComponent(SpriteSheet.PLAYER));
        }

        TypeComponent type = engine.createComponent(TypeComponent.class);
        type.setType(Type.PLAYER);
        components.add(type);

        StatusComponent statusComponent = engine.createComponent(StatusComponent.class);
        statusComponent.setStatus(buildStatus());
        components.add(statusComponent);

        createPlayer(components);
    }

    private void createRemotePlayer(Player player) {
        PlayerStatusName playerStatusName = new PlayerStatusName(localConfig.getGameId(), player.getName());

        Set<Component> components = new HashSet<>();

        RemotePlayerComponent remotePlayerComponent = engine.createComponent(RemotePlayerComponent.class);
        remotePlayerComponent.setPlayerStatusName(playerStatusName);
        components.add(remotePlayerComponent);

        if (!localConfig.isHeadless()) {
            components.add(spriteSheetLoader.buildAnimationComponent(SpriteSheet.ENEMY));
        }

        TypeComponent type = engine.createComponent(TypeComponent.class);
        type.setType(Type.REMOTE_PLAYER);
        components.add(type);

        // We don't know their status just yet as this is just discovery
        StatusComponent statusComponent = engine.createComponent(StatusComponent.class);
        statusComponent.setStatus(buildStatus());
        components.add(statusComponent);

        createPlayer(components);
    }

    private void createPlayer(Set<Component> components) {
        float x = ThreadLocalRandom.current().nextInt(MIN_EDGE_DISTANCE, (int) WORLD_WIDTH - MIN_EDGE_DISTANCE);
        float y = ThreadLocalRandom.current().nextInt(MIN_EDGE_DISTANCE, (int) WORLD_HEIGHT - MIN_EDGE_DISTANCE);

        GameObject gameObject = GameObjectFactory.buildBasicGameObject(x, y, PLAYER_WIDTH, PLAYER_HEIGHT);
        BodyCreationRequest bodyCreationRequest = bodyFactory.boxBody(gameObject, Material.PLAYER);

        RenderComponent renderComponent = engine.createComponent(RenderComponent.class);
        renderComponent.setGameObject(gameObject);
        components.add(renderComponent);

        TextureComponent texture = engine.createComponent(TextureComponent.class);
        components.add(texture);

        StateComponent state = engine.createComponent(StateComponent.class);
        components.add(state);

        CollisionComponent collision = engine.createComponent(CollisionComponent.class);
        components.add(collision);

        entityManager.addEntityCreationRequest(new EntityCreationRequest(components, bodyCreationRequest));
    }

    private static Status buildStatus() {
        return Status.newBuilder()
                .setHealth(MAX_HEALTH)
                .setAmmo(MAX_AMMO)
                .setMana(MAX_AMMO)
                .setXp(0)
                .build();
    }
}
