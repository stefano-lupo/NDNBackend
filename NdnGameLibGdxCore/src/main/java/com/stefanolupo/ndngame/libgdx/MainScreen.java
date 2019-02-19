package com.stefanolupo.ndngame.libgdx;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.google.inject.Inject;
import com.stefanolupo.ndngame.config.Config;
import com.stefanolupo.ndngame.libgdx.components.*;
import com.stefanolupo.ndngame.libgdx.components.enums.Type;
import com.stefanolupo.ndngame.libgdx.systems.*;
import com.stefanolupo.ndngame.names.AttackName;
import com.stefanolupo.ndngame.names.PlayerStatusName;


public class MainScreen implements Screen {

    private final Config config;
    private final InputController inputController;
    private final BodyFactory bodyFactory;
    private final PooledEngine engine;
    private final World world;
    private final SpriteSheetLoader spriteSheetLoader;

    // Systems
    private final MovementSystem movementSystem;
    private final PlayerControlSystem playerControlSystem;
    private final RemotePlayerUpdateSystem remotePlayerUpdateSystem;
    private final LocalPlayerStatusSystem localPlayerStatusSystem;
    private final AttackSystem attackSystem;

    // These cant be initialized in the constructor
    private SpriteBatch spriteBatch = null;

    @Inject
    public MainScreen(Config config,
                      InputController inputController,
                      BodyFactory bodyFactory,
                      PooledEngine engine,
                      MyContactListener myContactListener,
                      World world,
                      SpriteSheetLoader spriteSheetLoader,

                      // Systems
                      MovementSystem movementSystem,
                      PlayerControlSystem playerControlSystem,
                      RemotePlayerUpdateSystem remotePlayerUpdateSystem,
//                      PlayerStatusManager playerStatusManager,
                      LocalPlayerStatusSystem localPlayerStatusSystem,
                      AttackSystem attackSystem) {
        this.config = config;
        this.inputController = inputController;
        this.bodyFactory = bodyFactory;
        this.engine = engine;
        this.world = world;
        this.spriteSheetLoader = spriteSheetLoader;

        // Systems
        this.movementSystem = movementSystem;
        this.playerControlSystem = playerControlSystem;
        this.remotePlayerUpdateSystem = remotePlayerUpdateSystem;
        this.localPlayerStatusSystem = localPlayerStatusSystem;
        this.attackSystem = attackSystem;

        world.setContactListener(myContactListener);
//        playerStatusManager.setPlayerStatusDiscovery(this::createRemotePlayer);
    }

    @Override
    public void show() {
        if (!config.isAutomated()) {
            Gdx.input.setInputProcessor((InputProcessor) inputController);
        }

        // Create what can't be created until LibGdx is loaded
        spriteBatch = new SpriteBatch();
        RenderingSystem renderingSystem = new RenderingSystem(spriteBatch);
        spriteBatch.setProjectionMatrix(renderingSystem.getCamera().combined);

        // Add all the relevant systems our engine should run
        // Note the order here defines the order in which the system will run
        engine.addSystem(new SteadyStateSystem());
        engine.addSystem(remotePlayerUpdateSystem);
        engine.addSystem(playerControlSystem);
        engine.addSystem(attackSystem);
        engine.addSystem(movementSystem);
        engine.addSystem(new PhysicsSystem(world));
        engine.addSystem(new PhysicsDebugSystem(world, renderingSystem.getCamera()));
        engine.addSystem(new CollisionSystem());
        engine.addSystem(localPlayerStatusSystem);
        engine.addSystem(new AnimationSystem());
        engine.addSystem(renderingSystem);

        // create some game objects
        createLocalPlayer();
        createScenery(1, 2);
        createScenery(8, 4);
        createScenery(15, 6);
        createScenery(20, 7);

        String otherName = config.getPlayerName().equals("desktop") ? "desktoptwo" : "desktop";
        PlayerStatusName remotePlayer = new PlayerStatusName(0, otherName);
        createRemotePlayer(remotePlayer);
    }


    private void createLocalPlayer() {
        Entity entity = engine.createEntity();
        LocalPlayerComponent player = engine.createComponent(LocalPlayerComponent.class);
        entity.add(player);

        entity.add(spriteSheetLoader.buildAnimationComponent(SpriteSheet.PLAYER));

        TypeComponent type = engine.createComponent(TypeComponent.class);
        type.setType(Type.PLAYER);
        entity.add(type);

        createPlayer(entity, 6);
    }

    private void createRemotePlayer(PlayerStatusName playerStatusName) {
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

    private void createScenery(float x, float y) {
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

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        engine.update(delta);
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
        world.dispose();
    }
}
