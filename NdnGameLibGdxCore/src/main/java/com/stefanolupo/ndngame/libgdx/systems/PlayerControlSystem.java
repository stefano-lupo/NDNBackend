package com.stefanolupo.ndngame.libgdx.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.stefanolupo.ndngame.libgdx.InputController;
import com.stefanolupo.ndngame.libgdx.components.PlayerComponent;

public class PlayerControlSystem
        extends IteratingSystem
        implements HasComponentMappers {

    private static final Float MAX_VEL = 5f;

    private final InputController inputController;

    public PlayerControlSystem(InputController inputController) {
        super(Family.all(PlayerComponent.class).get());
        this.inputController = inputController;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

        Body body = BODY_MAPPER.get(entity).getBody();

        if(inputController.left){
            lerpVelocityX(body, -MAX_VEL);
        }
        if(inputController.right){
            lerpVelocityX(body, MAX_VEL);
        }

        if(!inputController.left && ! inputController.right){
            lerpVelocityX(body, 0);
        }

        if(inputController.up){
            lerpVelocityY(body, MAX_VEL);
        }
        if(inputController.down){
            lerpVelocityY(body, -MAX_VEL);
        }

        if(!inputController.up && ! inputController.down){
            lerpVelocityY(body, 0);
        }

    }

    private void lerpVelocityX(Body body, float toValue) {
        body.setLinearVelocity(MathUtils.lerp(body.getLinearVelocity().x, toValue, 0.2f), body.getLinearVelocity().y);
    }

    private void lerpVelocityY(Body body, float toValue) {
        body.setLinearVelocity(body.getLinearVelocity().x, MathUtils.lerp(body.getLinearVelocity().y, toValue, 0.2f));
    }
}
