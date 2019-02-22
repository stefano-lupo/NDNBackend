package com.stefanolupo.ndngame.libgdx.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.google.inject.Inject;
import com.stefanolupo.ndngame.libgdx.assets.GameAssetManager;
import com.stefanolupo.ndngame.libgdx.components.BlockComponent;

/**
 * Ticks blocks
 * Blocks don't really need to be ticket atm, but this is where they would be ticked
 */
public class BlockSystem extends IteratingSystem implements HasComponentMappers {

    private final GameAssetManager gameAssetManager;

    @Inject
    public BlockSystem(GameAssetManager gameAssetManager) {
        super(Family.all(BlockComponent.class).get());
        this.gameAssetManager = gameAssetManager;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

//        BlockComponent blockComponent = BLOCK_MAPPER.get(entity);

//        // Just temp to test block update
//        int health = blockComponent.getHealth();
//        health = Math.max(0, Math.min(health, Textures.values().length) - 1);
//
//        TextureComponent textureComponent = TEXTURE_MAPPER.get(entity);
//        textureComponent.setRegion(gameAssetManager.getTexture(Textures.values()[health]));
    }
}
