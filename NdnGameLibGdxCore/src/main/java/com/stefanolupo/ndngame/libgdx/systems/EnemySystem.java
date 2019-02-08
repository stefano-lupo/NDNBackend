package com.stefanolupo.ndngame.libgdx.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.stefanolupo.ndngame.libgdx.components.BodyComponent;
import com.stefanolupo.ndngame.libgdx.components.EnemyComponent;

public class EnemySystem extends BaseSystem {

    public EnemySystem() {
        super(Family.all(EnemyComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        EnemyComponent enemyComponent = enemyMapper.get(entity);
        BodyComponent bodyComponent = bodyMapper.get(entity);

        float distFromOrigin = Math.abs(enemyComponent.xPosCenter - bodyComponent.body.getPosition().x);

        enemyComponent.isGoingLeft = (distFromOrigin > 1) ? !enemyComponent.isGoingLeft : enemyComponent.isGoingLeft;

        float speed = enemyComponent.isGoingLeft ? -0.01f : 0.01f;

        bodyComponent.body.setTransform(bodyComponent.getBody().getPosition().x + speed,
                bodyComponent.body.getPosition().y,
                bodyComponent.body.getAngle());
    }
}
