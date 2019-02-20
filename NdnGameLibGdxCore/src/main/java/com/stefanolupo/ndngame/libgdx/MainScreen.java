package com.stefanolupo.ndngame.libgdx;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import com.google.inject.Inject;
import com.stefanolupo.ndngame.config.Config;
import com.stefanolupo.ndngame.libgdx.contactlisteners.MyContactListener;
import com.stefanolupo.ndngame.libgdx.inputcontrollers.InputController;
import com.stefanolupo.ndngame.libgdx.listeners.AttackListener;
import com.stefanolupo.ndngame.libgdx.systems.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MainScreen implements Screen {

    private static final Logger LOG = LoggerFactory.getLogger(MainScreen.class);

    private final Config config;
    private final InputController inputController;
    private final PooledEngine engine;
    private final World world;
    private final EntityCreator entityCreator;

    // Systems
    private final MovementSystem movementSystem;
    private final PlayerControlSystem playerControlSystem;
    private final RemotePlayerUpdateSystem remotePlayerUpdateSystem;
    private final LocalPlayerStatusSystem localPlayerStatusSystem;
    private final AttackSystem attackSystem;
    private final BlockSystem blockSystem;

    // Listeners
    private final AttackListener attackListener;

    // These cant be initialized in the constructor
    private SpriteBatch spriteBatch = null;

    @Inject
    public MainScreen(Config config,
                      InputController inputController,
                      PooledEngine engine,
                      MyContactListener myContactListener,
                      World world,
                      EntityCreator entityCreator,

                      // Systems
                      MovementSystem movementSystem,
                      PlayerControlSystem playerControlSystem,
                      RemotePlayerUpdateSystem remotePlayerUpdateSystem,
                      LocalPlayerStatusSystem localPlayerStatusSystem,
                      AttackSystem attackSystem,
                      BlockSystem blockSystem,

                      // Listeners
                      AttackListener attackListener) {
        this.config = config;
        this.inputController = inputController;
        this.engine = engine;
        this.world = world;
        this.entityCreator = entityCreator;

        // Systems
        this.movementSystem = movementSystem;
        this.playerControlSystem = playerControlSystem;
        this.remotePlayerUpdateSystem = remotePlayerUpdateSystem;
        this.localPlayerStatusSystem = localPlayerStatusSystem;
        this.attackSystem = attackSystem;
        this.blockSystem = blockSystem;

        // Listeners
        this.attackListener = attackListener;

        world.setContactListener(myContactListener);
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
//        engine.addSystem(attackSystem);
        engine.addSystem(blockSystem);
        engine.addSystem(movementSystem);
        engine.addSystem(new PhysicsSystem(world));
        engine.addSystem(new PhysicsDebugSystem(world, renderingSystem.getCamera()));
        engine.addSystem(new CollisionSystem());
        engine.addSystem(localPlayerStatusSystem);
        engine.addSystem(new AnimationSystem());
        engine.addSystem(renderingSystem);

        // Add listeners
        engine.addEntityListener(AttackListener.FAMILY, attackListener);

        // create some game objects
        entityCreator.createLocalPlayer();
        entityCreator.createScenery(1, 2);
        entityCreator.createScenery(8, 4);
        entityCreator.createScenery(15, 6);
        entityCreator.createScenery(20, 7);
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
