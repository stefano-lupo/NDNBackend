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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.stefanolupo.ndngame.libgdx.components.*;
import com.stefanolupo.ndngame.libgdx.systems.*;


public class MainScreen implements Screen {

    private final NdnGame ndnGame;
    private final KeyboardController controller;
    private final World world;
    private final BodyFactory bodyFactory;
    private final Sound ping;
    private final Sound boing;
    private final PooledEngine engine;

    private final SpriteBatch spriteBatch;

   public MainScreen(NdnGame ndnGame) {
        this.ndnGame = ndnGame;
        controller = new KeyboardController();
        world = new World(new Vector2(0, 10f), true);
        world.setContactListener(new MyContactListener());
        bodyFactory = BodyFactory.getInstance(world);

        ndnGame.myAssetManager.queueAddSounds();
        ndnGame.myAssetManager.assetManager.finishLoading();
        ping = ndnGame.myAssetManager.assetManager.get(MyAssetManager.PING_SOUND, Sound.class);
        boing = ndnGame.myAssetManager.assetManager.get(MyAssetManager.BOING_SOUND, Sound.class);

//       ndnGame.myAssetManager.queAddImages();
//       playerTexture = ndnGame.myAssetManager.assetManager.get("img/player.png");

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

        // create some game objects
        createPlayer();
        createPlatform(2,2);
        createPlatform(2,7);
        createPlatform(7,2);
        createPlatform(7,7);
        createFloor();
    }



    private void createPlayer() {
        Entity entity = engine.createEntity();

        Body body = bodyFactory.makeCirclePolyBody(10, 10, 1, BodyFactory.STONE, BodyDef.BodyType.DynamicBody, true);
        BodyComponent bodyComponent = engine.createComponent(BodyComponent.class);
        bodyComponent.body = body;
        bodyComponent.body.setUserData(entity);

        TransformComponent position = engine.createComponent(TransformComponent.class);
        position.getPosition().set(10, 10, 0);

        TextureComponent texture = engine.createComponent(TextureComponent.class);
        texture.region = atlas.findRegion("player");

        TypeComponent type = engine.createComponent(TypeComponent.class);
        type.type = TypeComponent.PLAYER;

        StateComponent state = engine.createComponent(StateComponent.class);
        state.set(StateComponent.STATE_NORMAL);

        CollisionComponent collision = engine.createComponent(CollisionComponent.class);
        PlayerComponent player = engine.createComponent(PlayerComponent.class);

        engine.addEntity(entity);

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
        BodyComponent b2dbody = engine.createComponent(BodyComponent.class);
        b2dbody.body = bodyFactory.makeBoxPolyBody(0, 0, 100, 0.2f, BodyFactory.STONE, BodyDef.BodyType.StaticBody, false);
        TextureComponent texture = engine.createComponent(TextureComponent.class);
        texture.region = atlas.findRegion("player");
        TypeComponent type = engine.createComponent(TypeComponent.class);
        type.type = TypeComponent.SCENERY;

        b2dbody.body.setUserData(entity);

        entity.add(b2dbody);
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
        debugRenderer.dispose();
    }
}
