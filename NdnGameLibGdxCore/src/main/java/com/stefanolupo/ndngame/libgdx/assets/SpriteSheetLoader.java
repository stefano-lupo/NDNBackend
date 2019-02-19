package com.stefanolupo.ndngame.libgdx.assets;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.stefanolupo.ndngame.libgdx.components.AnimationComponent;
import com.stefanolupo.ndngame.libgdx.components.enums.AttackState;
import com.stefanolupo.ndngame.libgdx.components.enums.MotionState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SpriteSheetLoader {

    // Note: CWD on desktop is assets dir anyway
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final float FRAME_DURATION = 0.6f;
    private static final String KEY_FRAME_FORMAT = "%s_%05d";
    private static final Logger LOG = LoggerFactory.getLogger(SpriteSheetLoader.class);

    private final GameAssetManager gameAssetManager;
    private final PooledEngine engine;

    @Inject
    public SpriteSheetLoader(GameAssetManager gameAssetManager,
                             PooledEngine engine) {
        this.gameAssetManager = gameAssetManager;
        this.engine = engine;
    }

    public AnimationComponent buildAnimationComponent(SpriteSheet spriteSheet) {
        TextureAtlas atlas = gameAssetManager.getAtlas(spriteSheet);
        JsonDescription description = readJsonDescription(spriteSheet);

        AnimationComponent animationComponent = engine.createComponent(AnimationComponent.class);

        Map<MotionState, Animation<TextureRegion>> motionMap = animationComponent.getMotionAnimations();
        Map<AttackState, Animation<TextureRegion>> attackMap = animationComponent.getAttackAnimations();
        for (RowInfo rowInfo : description.rowInfo) {
            String animationName = rowInfo.name;

            if (!(MotionState.isMotionState(animationName) || AttackState.isAttackState(animationName))) {
                // Not currently using all of the sprites on the spritesheets
                continue;
            }

            TextureRegion[] textureRegions = new TextureRegion[rowInfo.numCols];
            for (int col = 0; col < rowInfo.numCols; col++) {
                String keyFrame = String.format(KEY_FRAME_FORMAT, animationName, col);
                textureRegions[col] = atlas.findRegion(keyFrame);
            }
            Animation<TextureRegion> animation = new Animation<>(FRAME_DURATION, textureRegions);
            animation.setPlayMode(Animation.PlayMode.LOOP);

            if (MotionState.isMotionState(animationName)) {
                motionMap.put(MotionState.fromString(animationName), animation);
            } else {
                attackMap.put(AttackState.fromString(animationName), animation);
            }
        }

        return animationComponent;
    }

    private JsonDescription readJsonDescription(SpriteSheet spriteSheet) {
        String fileName = spriteSheet.toJsonName();
        LOG.debug("Reading JSON for {} from {}", spriteSheet, fileName);
        try (InputStream fileStream = new FileInputStream(fileName)) {
            return MAPPER.readValue(fileStream, JsonDescription.class);
        } catch (IOException e) {
            throw new RuntimeException("Could not read JSON for " + spriteSheet, e);
        }
    }

    public static class JsonDescription {
        public List<RowInfo> rowInfo = new ArrayList<>();
        public int width;
        public int height;
    }

    public static class RowInfo {
        public String name;
        public int numCols;
    }
}
