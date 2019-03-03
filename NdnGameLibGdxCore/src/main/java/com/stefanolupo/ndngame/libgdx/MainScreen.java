package com.stefanolupo.ndngame.libgdx;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import com.google.inject.Inject;
import com.stefanolupo.ndngame.config.LocalConfig;
import com.stefanolupo.ndngame.libgdx.contactlisteners.GameContactListener;
import com.stefanolupo.ndngame.libgdx.entities.EntityCreator;
import com.stefanolupo.ndngame.libgdx.inputcontrollers.InputController;
import com.stefanolupo.ndngame.libgdx.listeners.AttackListener;
import com.stefanolupo.ndngame.libgdx.listeners.BodyRemovalListener;
import com.stefanolupo.ndngame.libgdx.systems.BlockSystem;
import com.stefanolupo.ndngame.libgdx.systems.PlayerControlSystem;
import com.stefanolupo.ndngame.libgdx.systems.core.*;
import com.stefanolupo.ndngame.libgdx.systems.local.LocalPlayerStatusSystem;
import com.stefanolupo.ndngame.libgdx.systems.remote.AttackSystem;
import com.stefanolupo.ndngame.libgdx.systems.remote.BlockUpdateSystem;
import com.stefanolupo.ndngame.libgdx.systems.remote.RemotePlayerUpdateSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MainScreen implements Screen {

    private static final Logger LOG = LoggerFactory.getLogger(MainScreen.class);

    private final LocalConfig localConfig;
    private final InputController inputController;
    private final PooledEngine engine;
    private final World world;
    private final EntityCreator entityCreator;

    // Systems
    private final AnimationSystem animationSystem;
    private final AttackSystem attackSystem;
    private final BlockSystem blockSystem;
    private final BlockUpdateSystem blockUpdateSystem;
    private final ProjectileCollisionSystem projectileCollisionSystem;
    private final LocalPlayerStatusSystem localPlayerStatusSystem;
    private final MovementSystem movementSystem;
    private final PhysicsSystem physicsSystem;
    private final PlayerControlSystem playerControlSystem;
    private final RemotePlayerUpdateSystem remotePlayerUpdateSystem;
    private final RenderingSystem renderingSystem;
    private final SteadyStateSystem steadyStateSystem;

    // Listeners
    private final AttackListener attackListener;
    private final BodyRemovalListener bodyRemovalListener;

    // These cant be initialized in the constructor
    private SpriteBatch spriteBatch = null;

    @Inject
    public MainScreen(LocalConfig localConfig,
                      InputController inputController,
                      PooledEngine engine,
                      GameContactListener gameContactListener,
                      World world,
                      EntityCreator entityCreator,

                      // Systems
                      AnimationSystem animationSystem,
                      AttackSystem attackSystem,
                      BlockSystem blockSystem,
                      BlockUpdateSystem blockUpdateSystem,
                      ProjectileCollisionSystem projectileCollisionSystem,
                      LocalPlayerStatusSystem localPlayerStatusSystem,
                      MovementSystem movementSystem,
                      PhysicsSystem physicsSystem,
                      PlayerControlSystem playerControlSystem,
                      RemotePlayerUpdateSystem remotePlayerUpdateSystem,
                      RenderingSystem renderingSystem,
                      SteadyStateSystem steadyStateSystem,

                      // Listeners
                      AttackListener attackListener,
                      BodyRemovalListener bodyRemovalListener) {

        this.localConfig = localConfig;
        this.inputController = inputController;
        this.engine = engine;
        this.world = world;
        this.entityCreator = entityCreator;

        // Systems
        this.animationSystem = animationSystem;
        this.attackSystem = attackSystem;
        this.blockSystem = blockSystem;
        this.blockUpdateSystem = blockUpdateSystem;
        this.projectileCollisionSystem = projectileCollisionSystem;
        this.localPlayerStatusSystem = localPlayerStatusSystem;
        this.movementSystem = movementSystem;
        this.physicsSystem = physicsSystem;
        this.playerControlSystem = playerControlSystem;
        this.remotePlayerUpdateSystem = remotePlayerUpdateSystem;
        this.renderingSystem = renderingSystem;
        this.steadyStateSystem = steadyStateSystem;

        // Listeners
        this.attackListener = attackListener;
        this.bodyRemovalListener = bodyRemovalListener;

        world.setContactListener(gameContactListener);
    }

    @Override
    public void show() {
        if (!localConfig.isAutomated()) {
            Gdx.input.setInputProcessor((InputProcessor) inputController);
        }

        // Create what can't be created until LibGdx is loaded
        if (!localConfig.isHeadless()) {
            spriteBatch = new SpriteBatch();
            renderingSystem.configureOnInit(spriteBatch);
        }

        // Add all the relevant systems our engine should run
        // Note the order here defines the order in which the system will run
        engine.addSystem(steadyStateSystem);

        // Remote Updater Systems
        engine.addSystem(remotePlayerUpdateSystem);
        engine.addSystem(blockUpdateSystem);

        // Core game systems
        engine.addSystem(playerControlSystem);
//        engine.addSystem(attackSystem);
        engine.addSystem(movementSystem);
        engine.addSystem(blockSystem);

        if (!localConfig.isHeadless()) {
            engine.addSystem(new PhysicsDebugSystem(world, renderingSystem.getCamera()));
        }

        engine.addSystem(physicsSystem);
        engine.addSystem(projectileCollisionSystem);

        if (!localConfig.isHeadless()) {
            engine.addSystem(animationSystem);
            engine.addSystem(renderingSystem);
        }

        // Local publisher systems
        engine.addSystem(localPlayerStatusSystem);

        // Listeners
        engine.addEntityListener(AttackListener.FAMILY, attackListener);
        engine.addEntityListener(BodyRemovalListener.FAMILY, bodyRemovalListener);

        entityCreator.createInitialWorld();
    }

    @Override
    public void render(float delta) {
        if (!localConfig.isHeadless()) {
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        }

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
        if (spriteBatch != null) {
            spriteBatch.dispose();
        }
        world.dispose();
    }
}
