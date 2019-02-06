package com.stefanolupo.ndngame.libgdx.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.stefanolupo.ndngame.libgdx.components.TextureComponent;
import com.stefanolupo.ndngame.libgdx.components.TransformComponent;
import com.stefanolupo.ndngame.libgdx.components.ZComparator;

import java.util.Comparator;

public class RenderingSystem extends SortedIteratingSystem {

    private static final float PIXELS_PER_METER = 32.0f;

    private static final float FRUSTRUM_WIDTH = Gdx.graphics.getWidth() / PIXELS_PER_METER;
    private static final float FRUSTRUM_HEIGHT = Gdx.graphics.getHeight() / PIXELS_PER_METER;

    private static final float PIXELS_TO_METERS = 1.0f / PIXELS_PER_METER;

    private static final Vector2 meterDimensions = new Vector2();
    private static final Vector2 pixelDimensions = new Vector2();
    private static final Comparator<Entity> zComparator = new ZComparator();

    private final SpriteBatch spriteBatch;
    private final Array<Entity> renderQueue;
    private final OrthographicCamera camera;
    private final ComponentMapper<TextureComponent> textureMapper;
    private final ComponentMapper<TransformComponent> transformComponentComponentMapper;

    public RenderingSystem(SpriteBatch spriteBatch) {
        super(Family.all(TransformComponent.class, TextureComponent.class).get(), zComparator);

        this.spriteBatch = spriteBatch;
        textureMapper = ComponentMapper.getFor(TextureComponent.class);
        transformComponentComponentMapper = ComponentMapper.getFor(TransformComponent.class);
        renderQueue = new Array<>();
        camera = new OrthographicCamera(FRUSTRUM_WIDTH, FRUSTRUM_HEIGHT);
        camera.position.set(FRUSTRUM_WIDTH / 2f, FRUSTRUM_HEIGHT / 2f, 0);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        // Sort render queue based on z axis
        renderQueue.sort(zComparator);

        camera.update();
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.enableBlending();
        spriteBatch.begin();

        for (Entity entity : renderQueue) {
            TextureComponent textureComponent = textureMapper.get(entity);
            TransformComponent transformComponent = transformComponentComponentMapper.get(entity);

            if (textureComponent.getRegion() == null || transformComponent.isHidden()) {
                continue;
            }

            float width = textureComponent.getRegion().getRegionWidth();
            float height = textureComponent.getRegion().getRegionHeight();

            float originX = width / 2f;
            float originY = height / 2f;

            spriteBatch.draw(textureComponent.getRegion(),
                    transformComponent.getPosition().x - originX,
                    transformComponent.getPosition().y - originY,
                    originX,
                    originY,
                    width,
                    height,
                    pixelstoMeters(transformComponent.getScale().x),
                    pixelstoMeters(transformComponent.getScale().y),
                    transformComponent.getRotation()
            );

            spriteBatch.end();
            renderQueue.clear();
        }
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        renderQueue.add(entity);
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public static Vector2 getScreenSizePixels() {
        pixelDimensions.set(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        return pixelDimensions;
    }

    public static float pixelstoMeters(float pixelValue) {
        return pixelValue * PIXELS_TO_METERS;
    }
}
