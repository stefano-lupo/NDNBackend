package com.stefanolupo.ndngame.libgdx.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.stefanolupo.ndngame.libgdx.components.BodyComponent;
import com.stefanolupo.ndngame.libgdx.components.EnemyComponent;

import static com.stefanolupo.ndngame.libgdx.systems.Mappers.*;

public class EnemySystem extends IteratingSystem {

    public EnemySystem() {
        super(Family.all(EnemyComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        EnemyComponent enemyComponent = ENEMY_MAPPER.get(entity);
        BodyComponent bodyComponent = BODY_MAPPER.get(entity);

        float distFromOrigin = Math.abs(enemyComponent.xPosCenter - bodyComponent.body.getPosition().x);

        enemyComponent.isGoingLeft = (distFromOrigin > 1) ? !enemyComponent.isGoingLeft : enemyComponent.isGoingLeft;

        float speed = enemyComponent.isGoingLeft ? -0.01f : 0.01f;

        bodyComponent.body.setTransform(bodyComponent.getBody().getPosition().x + speed,
                bodyComponent.body.getPosition().y,
                bodyComponent.body.getAngle());
    }
}
