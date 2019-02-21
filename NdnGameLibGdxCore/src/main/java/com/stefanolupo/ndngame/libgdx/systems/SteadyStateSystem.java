package com.stefanolupo.ndngame.libgdx.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.physics.box2d.Body;
import com.stefanolupo.ndngame.libgdx.components.BodyComponent;
import com.stefanolupo.ndngame.libgdx.components.StateComponent;
import com.stefanolupo.ndngame.libgdx.components.enums.AttackState;
import com.stefanolupo.ndngame.libgdx.components.enums.InteractionState;
import com.stefanolupo.ndngame.libgdx.components.enums.MotionState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Updates state based on changes from previous ticks world update
 * Happens before any player / remote input changes
 */
public class SteadyStateSystem extends IteratingSystem implements HasComponentMappers {

    private static final Logger LOG = LoggerFactory.getLogger(SteadyStateSystem.class);

    public SteadyStateSystem() {
        super(Family.all(BodyComponent.class, StateComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

        StateComponent stateComponent = STATE_MAPPER.get(entity);
        Body body = BODY_MAPPER.get(entity).getBody();

        if (stateComponent.isInAttackState() &&
                stateComponent.getTimeInState() > StateComponent.ATTACK_STATE_ANIMATION_TIME_MS) {
            stateComponent.updateAttackState(AttackState.REST, deltaTime);
        }

        if (stateComponent.isInInteractState() &&
                stateComponent.getTimeInState() > StateComponent.INTERACT_STATE_ANIMATION_TIME_MS) {
            stateComponent.updateInteractionState(InteractionState.REST, deltaTime);
        }

        if (body.getLinearVelocity().x == 0) {
            stateComponent.updateHozState(MotionState.REST, deltaTime);
        }

        if (body.getLinearVelocity().y == 0) {
            stateComponent.updateVertState(MotionState.REST, deltaTime);
        }
    }
}
