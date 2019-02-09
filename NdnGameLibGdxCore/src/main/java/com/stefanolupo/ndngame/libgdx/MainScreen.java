package com.stefanolupo.ndngame.libgdx;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.World;
import com.google.inject.Inject;
import com.stefanolupo.ndngame.libgdx.components.*;
import com.stefanolupo.ndngame.libgdx.systems.*;


public class MainScreen implements Screen {

    private final InputController inputController;
    private final BodyFactory bodyFactory;
    private final GameAssetManager gameAssetManager;
    private final PooledEngine engine;
    private final ContactListener contactListener;
    private final World world;

    // This cant be initialized in the constructor
    private  TextureAtlas atlas = null;
    private SpriteBatch spriteBatch = null;
//    private final LevelFactory levelFactory;

    @Inject
    public MainScreen(InputController inputController,
                      BodyFactory bodyFactory,
                      GameAssetManager gameAssetManager,
                      PooledEngine engine,
                      ContactListener contactListener,
                      World world) {
        this.inputController = inputController;
        this.bodyFactory = bodyFactory;
        this.gameAssetManager = gameAssetManager;
        this.engine = engine;
        this.contactListener = contactListener;
        this.world = world;

        world.setContactListener(contactListener);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(inputController);
//        levelFactory = new LevelFactory(engine, atlas);

        // Create what can't be created until LibGdx is loaded
        atlas = gameAssetManager.getGameAtlas();
        spriteBatch = new SpriteBatch();

        // Add all the relevant systems our engine should run
        RenderingSystem renderingSystem = new RenderingSystem(spriteBatch);
        spriteBatch.setProjectionMatrix(renderingSystem.getCamera().combined);
        engine.addSystem(new AnimationSystem());
        engine.addSystem(new CollisionSystem());
        engine.addSystem(new PlayerControlSystem(inputController));
        engine.addSystem(new PhysicsDebugSystem(world, renderingSystem.getCamera()));
        engine.addSystem(new PhysicsSystem(world));
        engine.addSystem(renderingSystem);
        engine.addSystem(new EnemySystem());
//        engine.addSystem(new LevelGenerationSystem(levelFactory));

        // create some game objects
         createPlayer();
        createPlatform(1, 2);
        createPlatform(8, 4);
        createPlatform(15, 6);
        createPlatform(20, 7);
        createFloor();
    }


    private Entity createPlayer() {
        Entity entity = engine.createEntity();

        Body body = bodyFactory.makeCirclePolyBody(10, 10, 0.5f, BodyFactory.STONE, BodyDef.BodyType.DynamicBody, true);
        BodyComponent bodyComponent = engine.createComponent(BodyComponent.class);
        bodyComponent.body = body;
        bodyComponent.body.setUserData(entity);
        entity.add(bodyComponent);

        TransformComponent position = engine.createComponent(TransformComponent.class);
        position.getPosition().set(10, 10, 0);
        entity.add(position);

        TextureComponent texture = engine.createComponent(TextureComponent.class);
        texture.region = atlas.findRegion("player");
        entity.add(texture);

        TypeComponent type = engine.createComponent(TypeComponent.class);
        type.type = TypeComponent.PLAYER;
        entity.add(type);

        StateComponent state = engine.createComponent(StateComponent.class);
        state.set(StateComponent.STATE_NORMAL);
        entity.add(state);

        CollisionComponent collision = engine.createComponent(CollisionComponent.class);
        entity.add(collision);

        PlayerComponent player = engine.createComponent(PlayerComponent.class);
        entity.add(player);

        engine.addEntity(entity);
        return entity;
    }

    private void createPlatform(float x, float y) {
        Entity entity = engine.createEntity();
        BodyComponent bodyCompo = engine.createComponent(BodyComponent.class);
        bodyCompo.body = bodyFactory.makeBoxPolyBody(x, y, 3, 0.2f, BodyFactory.STONE, BodyDef.BodyType.StaticBody, false);
        TextureComponent texture = engine.createComponent(TextureComponent.class);
        texture.region = atlas.findRegion("player");
        TypeComponent type = engine.createComponent(TypeComponent.class);
        type.type = TypeComponent.SCENERY;
        bodyCompo.body.setUserData(entity);

        entity.add(bodyCompo);
        entity.add(texture);
        entity.add(type);

        engine.addEntity(entity);
    }

    private void createFloor() {
        Entity entity = engine.createEntity();
        BodyComponent bodyComponent = engine.createComponent(BodyComponent.class);
        bodyComponent.body = bodyFactory.makeBoxPolyBody(0, 0.3f, 100, 0.3f, BodyFactory.STONE, BodyDef.BodyType.StaticBody, false);
        TextureComponent texture = engine.createComponent(TextureComponent.class);
        texture.region = atlas.findRegion("player");
        TypeComponent type = engine.createComponent(TypeComponent.class);
        type.type = TypeComponent.SCENERY;

        bodyComponent.body.setUserData(entity);

        entity.add(bodyComponent);
        entity.add(texture);
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
        atlas.dispose();
        world.dispose();
    }
}
