package com.stefanolupo.ndngame.libgdx.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.stefanolupo.ndngame.libgdx.components.LocalPlayerComponent;
import com.stefanolupo.ndngame.libgdx.components.RenderComponent;
import com.stefanolupo.ndngame.libgdx.components.TextureComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;

public class RenderingSystem
        extends SortedIteratingSystem
        implements HasComponentMappers {

    private static final Logger LOG = LoggerFactory.getLogger(RenderingSystem.class);
    private static final float PIXELS_PER_METER = 20f;
    private static final float WORLD_VIEW_WIDTH = Gdx.graphics.getWidth() / PIXELS_PER_METER;
    private static final float WORLD_VIEW_HEIGHT = Gdx.graphics.getHeight() / PIXELS_PER_METER;
    private static final Comparator<Entity> Z_COMPARATOR = Comparator.comparing(e -> RENDER_MAPPER.get(e).getPosition().z);

    private final SpriteBatch spriteBatch;
    private final Array<Entity> renderQueue;
    private final OrthographicCamera camera;
    private final BitmapFont font = new BitmapFont();

    public RenderingSystem(SpriteBatch spriteBatch) {
        super(Family.all(RenderComponent.class, TextureComponent.class).get(), Z_COMPARATOR);
        this.spriteBatch = spriteBatch;
        renderQueue = new Array<>();

        camera = new OrthographicCamera(WORLD_VIEW_WIDTH, WORLD_VIEW_HEIGHT);
        camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
        LOG.info("Camera viewport: {} x {} units", camera.viewportHeight, camera.viewportHeight);
        camera.update();
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.enableBlending();
        spriteBatch.begin();

        for (Entity entity : renderQueue) {
            TextureComponent textureComponent = TEXTURE_MAPPER.get(entity);
            RenderComponent renderComponent = RENDER_MAPPER.get(entity);

            if (textureComponent.getRegion() == null) {
                continue;
            }

            float worldX = renderComponent.getPosition().x;
            float worldY = renderComponent.getPosition().y;
            float worldWidth = renderComponent.getWidth();
            float worldHeight = renderComponent.getHeight();

            // Want to draw to bottom left corner for sprites, but body uses the center
            float drawX = worldX - (worldWidth / 2);
            float drawY = worldY - (worldHeight / 2);

            spriteBatch.draw(textureComponent.getRegion(),
                    drawX, drawY,
                    textureComponent.getRegion().getRegionX(), textureComponent.getRegion().getRegionY(),
                    worldWidth, worldHeight,
                    renderComponent.getScale().x, renderComponent.getScale().y,
                    renderComponent.getRotation());
        }
        updateCameraPosition();
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

    private void updateCameraPosition() {

        Entity localPlayerEntity = getEngine().getEntitiesFor(Family.all(LocalPlayerComponent.class).get()).get(0);
        if (localPlayerEntity == null) {
            throw new IllegalStateException("Renderer had no local player!");
        }

        RenderComponent renderComponent = RENDER_MAPPER.get(localPlayerEntity);
        if (renderComponent == null) {
            throw new IllegalStateException("Local player had no render component!");
        }

        camera.position.set(renderComponent.getPosition().x, renderComponent.getPosition().y, 0);
        camera.update();
    }

    private void drawPositions(Entity entity, RenderComponent renderComponent) {
        if (LOCAL_PLAYER_MAPPER.get(entity) != null) {
            font.setColor(Color.BLUE);
            font.draw(spriteBatch, String.format("x: %2f, y: %2f", renderComponent.getPosition().x, renderComponent.getPosition().y),
                    renderComponent.getPosition().x, renderComponent.getPosition().y);
        }

        if (REMOTE_PLAYER_MAPPER.get(entity) != null) {
            font.setColor(Color.RED);
            font.draw(spriteBatch, String.format("x: %2f, y: %2f", renderComponent.getPosition().x, renderComponent.getPosition().y),
                    renderComponent.getPosition().x, renderComponent.getPosition().y);
        }

    }
}
