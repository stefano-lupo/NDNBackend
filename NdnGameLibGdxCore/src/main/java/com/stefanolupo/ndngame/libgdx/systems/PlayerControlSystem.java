package com.stefanolupo.ndngame.libgdx.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.stefanolupo.ndngame.libgdx.InputController;
import com.stefanolupo.ndngame.libgdx.components.BodyComponent;
import com.stefanolupo.ndngame.libgdx.components.PlayerComponent;
import com.stefanolupo.ndngame.libgdx.components.StateComponent;

import static com.stefanolupo.ndngame.libgdx.systems.Mappers.*;

public class PlayerControlSystem extends IteratingSystem {

    private final InputController inputController;

    public PlayerControlSystem(InputController inputController) {
        super(Family.all(PlayerComponent.class).get());
        this.inputController = inputController;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        BodyComponent bodyComponent = BODY_MAPPER.get(entity);
        StateComponent stateComponent = STATE_MAPPER.get(entity);
        PlayerComponent playerComponent = PLAYER_MAPPER.get(entity);

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

        if(inputController.left){
            body.setLinearVelocity(MathUtils.lerp(body.getLinearVelocity().x, -5f, 0.2f), body.getLinearVelocity().y);
        }
        if(inputController.right){
            body.setLinearVelocity(MathUtils.lerp(body.getLinearVelocity().x, 5f, 0.2f), body.getLinearVelocity().y);
        }

        if(!inputController.left && ! inputController.right){
            body.setLinearVelocity(MathUtils.lerp(body.getLinearVelocity().x, 0, 0.1f), body.getLinearVelocity().y);
        }

        if(inputController.up &&
                (stateComponent.get() == StateComponent.STATE_NORMAL || stateComponent.get() == StateComponent.STATE_MOVING)){
            //b2body.body.applyForceToCenter(0, 3000,true);
            body.applyLinearImpulse(0, 50f, body.getWorldCenter().x, body.getWorldCenter().y, true);
            stateComponent.set(StateComponent.STATE_JUMPING);
        }
    }
}
