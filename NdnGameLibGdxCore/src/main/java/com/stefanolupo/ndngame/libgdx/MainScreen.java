package com.stefanolupo.ndngame.libgdx;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.stefanolupo.ndngame.libgdx.components.*;
import com.stefanolupo.ndngame.libgdx.levels.LevelFactory;
import com.stefanolupo.ndngame.libgdx.systems.*;
import com.stefanolupo.ndngame.libgdx.util.Util;


public class MainScreen implements Screen {

    private final NdnGame ndnGame;
    private final KeyboardController controller;
    private final World world;
    private final BodyFactory bodyFactory;
    private final Sound ping;
    private final Sound boing;
    private final PooledEngine engine;
    private final TextureAtlas atlas;
    private final SpriteBatch spriteBatch;
    private final LevelFactory levelFactory;

   public MainScreen(NdnGame ndnGame) {
        this.ndnGame = ndnGame;
        controller = new KeyboardController();
        world = new World(new Vector2(0, -10f), true);
        world.setContactListener(new MyContactListener());
        bodyFactory = BodyFactory.getInstance(world);

        ndnGame.myAssetManager.queueAddSounds();
        ndnGame.myAssetManager.queueAddImages();
        ndnGame.myAssetManager.assetManager.finishLoading();
        atlas = ndnGame.myAssetManager.assetManager.get(MyAssetManager.GAME_IMAGES_ATLAS);
        ping = ndnGame.myAssetManager.assetManager.get(MyAssetManager.PING_SOUND, Sound.class);
        boing = ndnGame.myAssetManager.assetManager.get(MyAssetManager.BOING_SOUND, Sound.class);


        spriteBatch = new SpriteBatch();
        RenderingSystem renderingSystem = new RenderingSystem(spriteBatch);
        spriteBatch.setProjectionMatrix(renderingSystem.getCamera().combined);

        /* add all the relevant systems our engine should run */
        engine = new PooledEngine();
        engine.addSystem(new AnimationSystem());
        engine.addSystem(renderingSystem);
        engine.addSystem(new PhysicsSystem(world));
        engine.addSystem(new PhysicsDebugSystem(world, renderingSystem.getCamera()));
        engine.addSystem(new CollisionSystem());
        engine.addSystem(new PlayerControlSystem(controller));
        engine.addSystem(new EnemySystem());


        levelFactory = new LevelFactory(engine, atlas);
        engine.addSystem(new LevelGenerationSystem(levelFactory));

        // create some game objects
        Entity playerEntity = createPlayer();
        engine.addSystem(new WaterFloorSystem(playerEntity));
        createPlatform(1,2);
        createPlatform(8,4);
        createPlatform(15,6);
        createPlatform(20,7);
        createFloor();

       int floorWidth = (int) (40*RenderingSystem.PIXELS_PER_METER);
       int floorHeight = (int) (1*RenderingSystem.PIXELS_PER_METER);
       TextureRegion floorRegion = Util.makeTextureRegion(floorWidth, floorHeight, "11331180");
       levelFactory.createFloor();

       int wFloorWidth = (int) (40*RenderingSystem.PIXELS_PER_METER);
       int wFloorHeight = (int) (10*RenderingSystem.PIXELS_PER_METER);
       TextureRegion wFloorRegion = Util.makeTextureRegion(wFloorWidth, wFloorHeight, "11113380");
       levelFactory.createWaterFloor();

//       int wallWidth = (int) (1*RenderingSystem.PIXELS_PER_METER);
//       int wallHeight = (int) (60*RenderingSystem.PIXELS_PER_METER);
//       TextureRegion wallRegion = DFUtils.makeTextureRegion(wallWidth, wallHeight, "222222FF");
//       levelFactory.createWalls(wallRegion);
   }



    private Entity createPlayer() {
    }



    private void createPlayer() {
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

    private void createPlatform(float x, float y){
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

    private void createFloor(){
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
    public void show() {
        Gdx.input.setInputProcessor(controller);
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
        spriteBatch.end();
        spriteBatch.dispose();
        atlas.dispose();
        world.dispose();
    }
}
