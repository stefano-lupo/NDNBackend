package com.stefanolupo.ndngame.libgdx.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.physics.box2d.Body;
import com.google.inject.Inject;
import com.stefanolupo.ndngame.config.Config;
import com.stefanolupo.ndngame.libgdx.EntityCreator;
import com.stefanolupo.ndngame.libgdx.components.AttackComponent;
import com.stefanolupo.ndngame.libgdx.components.LocalPlayerComponent;
import com.stefanolupo.ndngame.libgdx.components.StateComponent;
import com.stefanolupo.ndngame.libgdx.components.enums.AttackState;
import com.stefanolupo.ndngame.libgdx.components.enums.InteractionState;
import com.stefanolupo.ndngame.libgdx.components.enums.MotionState;
import com.stefanolupo.ndngame.libgdx.inputcontrollers.InputController;
import com.stefanolupo.ndngame.protos.Attack;
import com.stefanolupo.ndngame.protos.AttackType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * Updates StateComponent based on input from the InputController
 */
public class PlayerControlSystem
        extends IteratingSystem
        implements HasComponentMappers {

    private static final Logger LOG = LoggerFactory.getLogger(PlayerControlSystem.class);

    private final InputController inputController;
    private final PooledEngine pooledEngine;
    private final EntityCreator entityCreator;
    private final Config config;

    @Inject
    public PlayerControlSystem(InputController inputController,
                               PooledEngine pooledEngine,
                               EntityCreator entityCreator,
                               Config config) {
        super(Family.all(LocalPlayerComponent.class).get());
        this.inputController = inputController;
        this.pooledEngine = pooledEngine;
        this.entityCreator = entityCreator;
        this.config = config;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        StateComponent stateComponent = STATE_MAPPER.get(entity);
        Body body = BODY_MAPPER.get(entity).getBody();

        // No input allowed if currently busy
        if (stateComponent.isBusy()) {
            return;
        }

        // If attacking: don't allow movement
        if (inputController.isAttackButtonPressed()) {
            handleAttackCommand(stateComponent, deltaTime, body);
            return;
        }

        if (inputController.isInteractButtonPressed()) {
            handleInteractionCommand(stateComponent, deltaTime, body);
            return;
        }

        handleMovementCommand(stateComponent, deltaTime);
    }

    private void handleAttackCommand(StateComponent stateComponent,
                                     float deltaTime,
                                     Body body) {
        AttackComponent attackComponent = null;
        if (inputController.isMouse1Down()) {
            attackComponent = buildAttackComponent(body, 1f, AttackType.SWING);
            stateComponent.updateAttackState(AttackState.SWING, deltaTime);
        } else if (inputController.isMouse2Down()) {
            attackComponent = buildAttackComponent(body, 1f, AttackType.CAST);
            stateComponent.updateAttackState(AttackState.CAST, deltaTime);
        } else if (inputController.isMouse3Down()) {
            attackComponent = buildAttackComponent(body, 1f, AttackType.SHIELD);
            stateComponent.updateAttackState(AttackState.SHIELD, deltaTime);
        } else {
            LOG.error("Unknown attack command: {}", stateComponent.getAttackState());
        }

        Entity entity = pooledEngine.createEntity();
        entity.add(attackComponent);
        pooledEngine.addEntity(entity);

        stateComponent.updateMotionState(0, 0, deltaTime);
    }

    private AttackComponent buildAttackComponent(Body body, float radius, AttackType type) {
        AttackComponent attackComponent = pooledEngine.createComponent(AttackComponent.class);
//        AttackName name = new AttackName(config.getGameId(), config.getPlayerName());
        Attack attack = Attack.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setRadius(radius)
                .setX(body.getPosition().x)
                .setY(body.getPosition().y)
                .setType(type)
                .build();
//        attackComponent.setAttackName(name);
        attackComponent.setAttack(attack);
        return attackComponent;
    }

    private void handleInteractionCommand(StateComponent stateComponent, float deltaTime, Body body) {
        if (inputController.isSpacePressed()) {
            stateComponent.updateInteractionState(InteractionState.PLACE_BLOCK, deltaTime);
            entityCreator.createLocalBlock(body.getPosition().x, body.getPosition().y);

            // Force velocity to 0 when placing
            stateComponent.updateMotionState(0, 0, deltaTime);
        }
    }

    private void handleMovementCommand(StateComponent stateComponent, float deltaTime) {
        if (inputController.isLeftPressed()){
            stateComponent.updateHozState(MotionState.MOVE_LEFT, deltaTime);
        } else if(inputController.isRightPressed()){
            stateComponent.updateHozState(MotionState.MOVE_RIGHT, deltaTime);
        } else {
            stateComponent.updateHozState(MotionState.REST, deltaTime);
        }

        if (inputController.isUpPressed()){
            stateComponent.updateVertState(MotionState.MOVE_UP, deltaTime);
        } else if(inputController.isDownPressed()){
            stateComponent.updateVertState(MotionState.MOVE_DOWN, deltaTime);
        } else {
            stateComponent.updateVertState(MotionState.REST, deltaTime);
        }
    }
}
