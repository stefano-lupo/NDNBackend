package com.stefanolupo.ndngame.libgdx.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.stefanolupo.ndngame.libgdx.KeyboardController;
import com.stefanolupo.ndngame.libgdx.components.BodyComponent;
import com.stefanolupo.ndngame.libgdx.components.PlayerComponent;
import com.stefanolupo.ndngame.libgdx.components.StateComponent;

public class PlayerControlSystem extends BaseSystem {

    private final KeyboardController keyboardController;

    public PlayerControlSystem(KeyboardController keyboardController) {
        super(Family.all(PlayerComponent.class).get());
        this.keyboardController = keyboardController;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        BodyComponent bodyComponent = bodyMapper.get(entity);
        StateComponent stateComponent = stateMapper.get(entity);
        PlayerComponent playerComponent = playerMapper.get(entity);

        Body body = bodyComponent.getBody();

        if (playerComponent.onSping) {
            body.applyLinearImpulse(0, 175f, body.getWorldCenter().x, body.getWorldCenter().y, true);
            stateComponent.set(StateComponent.STATE_JUMPING);
            playerComponent.onSping = false;
        }


        if (body.getLinearVelocity().y > 0) {
            stateComponent.set(StateComponent.STATE_FALLING);
        }

        if (body.getLinearVelocity().y == 0) {
            if(stateComponent.get() == StateComponent.STATE_FALLING){
                stateComponent.set(StateComponent.STATE_NORMAL);
            }
            if(body.getLinearVelocity().x != 0){
                stateComponent.set(StateComponent.STATE_MOVING);
            }
        }

        if(keyboardController.left){
            body.setLinearVelocity(MathUtils.lerp(body.getLinearVelocity().x, -5f, 0.2f), body.getLinearVelocity().y);
        }
        if(keyboardController.right){
            body.setLinearVelocity(MathUtils.lerp(body.getLinearVelocity().x, 5f, 0.2f), body.getLinearVelocity().y);
        }

        if(!keyboardController.left && ! keyboardController.right){
            body.setLinearVelocity(MathUtils.lerp(body.getLinearVelocity().x, 0, 0.1f), body.getLinearVelocity().y);
        }

        if(keyboardController.up &&
                (stateComponent.get() == StateComponent.STATE_NORMAL || stateComponent.get() == StateComponent.STATE_MOVING)){
            //b2body.body.applyForceToCenter(0, 3000,true);
            body.applyLinearImpulse(0, 50f, body.getWorldCenter().x, body.getWorldCenter().y, true);
            stateComponent.set(StateComponent.STATE_JUMPING);
        }

    }
}
