package com.stefanolupo.ndngame.libgdx.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.stefanolupo.ndngame.libgdx.components.TextureComponent;
import com.stefanolupo.ndngame.libgdx.components.RenderComponent;
import com.stefanolupo.ndngame.libgdx.components.ZComparator;

import java.util.Comparator;

public class RenderingSystem
        extends SortedIteratingSystem
        implements HasComponentMappers {

    private static final float PIXELS_PER_METER = 32.0f;

    private static final float FRUSTRUM_WIDTH = Gdx.graphics.getWidth() / PIXELS_PER_METER;
    private static final float FRUSTRUM_HEIGHT = Gdx.graphics.getHeight() / PIXELS_PER_METER;

    private static final float PIXELS_TO_METERS = 1.0f / PIXELS_PER_METER;

    private static final Comparator<Entity> zComparator = new ZComparator();

    private final SpriteBatch spriteBatch;
    private final Array<Entity> renderQueue;
    private final OrthographicCamera camera;

    public RenderingSystem(SpriteBatch spriteBatch) {
        super(Family.all(RenderComponent.class, TextureComponent.class).get(), zComparator);

        this.spriteBatch = spriteBatch;
        renderQueue = new Array<>();
        camera = new OrthographicCamera(FRUSTRUM_WIDTH, FRUSTRUM_HEIGHT);
        camera.position.set(FRUSTRUM_WIDTH / 2f, FRUSTRUM_HEIGHT / 2f, 0);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        camera.update();
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.enableBlending();
        spriteBatch.begin();

        for (Entity entity : renderQueue) {
            TextureComponent textureComponent = TEXTURE_MAPPER.get(entity);
            RenderComponent renderComponent = TRANSFORM_MAPPER.get(entity);

            if (textureComponent.getRegion() == null) {
                continue;
            }

            float width = textureComponent.getRegion().getRegionWidth();
            float height = textureComponent.getRegion().getRegionHeight();

            float originX = width / 2f;
            float originY = height / 2f;

            spriteBatch.draw(textureComponent.getRegion(),
                    renderComponent.getPosition().x - originX,
                    renderComponent.getPosition().y - originY,
                    originX,
                    originY,
                    width,
                    height,
                    pixelsToMeters(renderComponent.getScale().x),
                    pixelsToMeters(renderComponent.getScale().y),
                    renderComponent.getRotation()
            );
        }

        spriteBatch.end();
        renderQueue.clear();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        renderQueue.add(entity);
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public static float pixelsToMeters(float pixelValue) {
        return pixelValue * PIXELS_TO_METERS;
    }
}
