package com.stefanolupo.ndngame.libgdx.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.stefanolupo.ndngame.libgdx.components.PlayerComponent;
import com.stefanolupo.ndngame.libgdx.components.TransformComponent;
import com.stefanolupo.ndngame.libgdx.levels.LevelFactory;

public class LevelGenerationSystem extends BaseSystem {

    private final LevelFactory levelFactory;

    public LevelGenerationSystem(LevelFactory levelFactory) {
        super(Family.all(PlayerComponent.class).get());
        this.levelFactory = levelFactory;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        TransformComponent transformComponent = transformMapper.get(entity);
        int currentPosition = (int) transformComponent.getPosition().y;

        if ((currentPosition + 7) > levelFactory.currentLevel) {
            levelFactory.generateLevel(currentPosition + 7);
        }
    }
}
