package com.stefanolupo.ndngame.libgdx;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.IntMap;
import com.google.inject.Inject;
import com.stefanolupo.ndngame.backend.chronosynced.PlayerStatusManager;
import com.stefanolupo.ndngame.libgdx.components.*;
import com.stefanolupo.ndngame.libgdx.components.enums.Type;
import com.stefanolupo.ndngame.libgdx.systems.*;
import com.stefanolupo.ndngame.names.PlayerStatusName;


public class MainScreen implements Screen {

    private final InputController inputController;
    private final BodyFactory bodyFactory;
    private final GameAssetManager gameAssetManager;
    private final PooledEngine engine;
    private final ContactListener contactListener;
    private final World world;

    // Systems
    private final MovementSystem movementSystem;
    private final PlayerControlSystem playerControlSystem;
    private final RemotePlayerUpdateSystem remotePlayerUpdateSystem;
    private final LocalPlayerStatusSystem localPlayerStatusSystem;

    // These cant be initialized in the constructor
    private  TextureAtlas atlas = null;
    private SpriteBatch spriteBatch = null;

    @Inject
    public MainScreen(InputController inputController,
                      BodyFactory bodyFactory,
                      GameAssetManager gameAssetManager,
                      PooledEngine engine,
                      ContactListener contactListener,
                      World world,
                      MovementSystem movementSystem,
                      PlayerControlSystem playerControlSystem,
                      RemotePlayerUpdateSystem remotePlayerUpdateSystem,
                      PlayerStatusManager playerStatusManager,
                      LocalPlayerStatusSystem localPlayerStatusSystem) {
        this.inputController = inputController;
        this.bodyFactory = bodyFactory;
        this.gameAssetManager = gameAssetManager;
        this.engine = engine;
        this.contactListener = contactListener;
        this.world = world;
        this.movementSystem = movementSystem;
        this.playerControlSystem = playerControlSystem;
        this.remotePlayerUpdateSystem = remotePlayerUpdateSystem;
        this.localPlayerStatusSystem = localPlayerStatusSystem;

        world.setContactListener(contactListener);
        playerStatusManager.setPlayerStatusDiscovery(this::createRemotePlayer);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(inputController);

        // Create what can't be created until LibGdx is loaded
        atlas = gameAssetManager.getGameAtlas();
        spriteBatch = new SpriteBatch();
        RenderingSystem renderingSystem = new RenderingSystem(spriteBatch);
        spriteBatch.setProjectionMatrix(renderingSystem.getCamera().combined);

        // Add all the relevant systems our engine should run
        // Note the order here defines the order in which the system will run
        engine.addSystem(new SteadyStateSystem());
        engine.addSystem(remotePlayerUpdateSystem);
        engine.addSystem(playerControlSystem);
        engine.addSystem(movementSystem);
        engine.addSystem(new PhysicsSystem(world));
        engine.addSystem(new PhysicsDebugSystem(world, renderingSystem.getCamera()));
        engine.addSystem(new CollisionSystem());
        engine.addSystem(localPlayerStatusSystem);
        engine.addSystem(renderingSystem);
        engine.addSystem(new AnimationSystem());

        // create some game objects
        createLocalPlayer();
        createScenery(1, 2);
        createScenery(8, 4);
        createScenery(15, 6);
        createScenery(20, 7);
        createFloor();
    }


    private void createLocalPlayer() {
        Entity entity = engine.createEntity();
        LocalPlayerComponent player = engine.createComponent(LocalPlayerComponent.class);
        entity.add(player);

        createPlayer(entity, 6);
    }

    private void createRemotePlayer(PlayerStatusName playerStatusName) {
        Entity entity = engine.createEntity();
        RemotePlayerComponent remotePlayerComponent = engine.createComponent(RemotePlayerComponent.class);
        remotePlayerComponent.setPlayerStatusName(playerStatusName);
        entity.add(remotePlayerComponent);

        createPlayer(entity, 8);
    }

    private void createPlayer(Entity entity, float x) {

        Body body = bodyFactory.makeBoxPolyBody(x, 9.5f, 1f, 1.5f, BodyFactory.STONE, BodyDef.BodyType.DynamicBody, true);
        body.setUserData(entity);

        BodyComponent bodyComponent = engine.createComponent(BodyComponent.class);
        bodyComponent.setBody(body);
        entity.add(bodyComponent);

        RenderComponent position = engine.createComponent(RenderComponent.class);
//        position.getPosition().set(10, 10, 0);
        entity.add(position);

        TextureComponent texture = engine.createComponent(TextureComponent.class);
        texture.setRegion(atlas.findRegion("trump_00000"));
        entity.add(texture);

        TypeComponent type = engine.createComponent(TypeComponent.class);
        type.setType(Type.PLAYER);
        entity.add(type);

        MotionStateComponent state = engine.createComponent(MotionStateComponent.class);
        entity.add(state);

        CollisionComponent collision = engine.createComponent(CollisionComponent.class);
        entity.add(collision);

        AnimationComponent animationComponent = engine.createComponent(AnimationComponent.class);
        IntMap<Animation<TextureRegion>> animationMap = animationComponent.getAnimations();

        // TODO: Move this all to some animation manager
        String format = "trump_%05d";
        for (int i = 0; i < 4; i++) {
            TextureRegion[] textureRegions = new TextureRegion[6];
            for (int j = 0; j < 6; j++) {
                textureRegions[j] = atlas.findRegion(String.format(format, i*6+j));
            }
            Animation<TextureRegion> animation = new Animation<>(0.4f, textureRegions);
            animation.setPlayMode(Animation.PlayMode.LOOP);
            animationMap.put(i, animation);
        }
        entity.add(animationComponent);

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
        TextureComponent texture = engine.createComponent(TextureComponent.class);
        texture.setRegion(atlas.findRegion("player"));
        entity.add(texture);

        TypeComponent type = engine.createComponent(TypeComponent.class);
        type.setType(Type.SCENERY);
        entity.add(type);

        engine.addEntity(entity);
    }

    private void createFloor() {
        Entity entity = engine.createEntity();

        Body body = bodyFactory.makeBoxPolyBody(0, 0.5f, 100, 0.3f, BodyFactory.STONE, BodyDef.BodyType.StaticBody);
        body.setUserData(entity);

        BodyComponent bodyComponent = engine.createComponent(BodyComponent.class);
        bodyComponent.setBody(body);
        entity.add(bodyComponent);

        // TODO: Setup correct textures here
        TextureComponent texture = engine.createComponent(TextureComponent.class);
        texture.setRegion(atlas.findRegion("player"));
        entity.add(texture);

        TypeComponent typeComponent = engine.createComponent(TypeComponent.class);
        typeComponent.setType(Type.SCENERY);
        entity.add(typeComponent);

        CollisionComponent collisionComponent = engine.createComponent(CollisionComponent.class);
        entity.add(collisionComponent);

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
        atlas.dispose();
        world.dispose();
    }
}
