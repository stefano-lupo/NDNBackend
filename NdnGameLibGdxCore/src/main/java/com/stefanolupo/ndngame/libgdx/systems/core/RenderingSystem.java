package com.stefanolupo.ndngame.libgdx.systems.core;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.google.inject.Inject;
import com.stefanolupo.ndngame.config.Config;
import com.stefanolupo.ndngame.libgdx.EntityCreator;
import com.stefanolupo.ndngame.libgdx.components.LocalPlayerComponent;
import com.stefanolupo.ndngame.libgdx.components.RenderComponent;
import com.stefanolupo.ndngame.libgdx.components.TextureComponent;
import com.stefanolupo.ndngame.libgdx.systems.HasComponentMappers;
import com.stefanolupo.ndngame.protos.GameObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;

public class RenderingSystem
        extends SortedIteratingSystem
        implements HasComponentMappers {

    private static final Logger LOG = LoggerFactory.getLogger(RenderingSystem.class);
    private static final Comparator<Entity> Z_COMPARATOR = Comparator.comparing(e -> RENDER_MAPPER.get(e).getGameObject().getZ());
    private static final float PIXELS_PER_METER = 40f;

    private final Config config;
    private final OrthographicCamera camera;
    private final Array<Entity> renderQueue;

    //    private final BitmapFont font = new BitmapFont();

    private SpriteBatch spriteBatch;
    private ShapeRenderer shapeRenderer;

    @Inject
    public RenderingSystem(Config config,
                           OrthographicCamera camera) {
        super(Family.all(RenderComponent.class, TextureComponent.class).get(), Z_COMPARATOR);
        this.config = config;
        this.camera = camera;
        renderQueue = new Array<>();
    }

    /**
     * These require LibGdx to be set up and must be run before rendering
     */
    public void configureOnInit(SpriteBatch spriteBatch) {
        this.spriteBatch = spriteBatch;
        if (config.isMasterView()) {
            camera.setToOrtho(false, EntityCreator.WORLD_WIDTH, EntityCreator.WORLD_HEIGHT);
        } else {
            camera.setToOrtho(false,
                    Gdx.graphics.getWidth() / PIXELS_PER_METER,
                    Gdx.graphics.getHeight() / PIXELS_PER_METER);
        }
        camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
        camera.update();
        LOG.info("Camera viewport: {} x {} units", camera.viewportHeight, camera.viewportHeight);

        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(camera.combined);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        if (spriteBatch == null) {
            throw new IllegalStateException("configureOnInit must be called before rendering can start");
        }

        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.enableBlending();
        spriteBatch.begin();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        for (Entity entity : renderQueue) {
            TextureComponent textureComponent = TEXTURE_MAPPER.get(entity);
            RenderComponent renderComponent = RENDER_MAPPER.get(entity);

            if (textureComponent.getRegion() == null) {
                continue;
            }

            GameObject gameObject = renderComponent.getGameObject();
            float worldX = gameObject.getX();
            float worldY = gameObject.getY();
            float worldWidth = gameObject.getWidth();
            float worldHeight = gameObject.getHeight();

            // Want to draw to bottom left corner for sprites, but body uses the center
            float drawX = worldX - (worldWidth / 2);
            float drawY = worldY - (worldHeight / 2);

            spriteBatch.draw(textureComponent.getRegion(),
                    drawX, drawY,
                    textureComponent.getRegion().getRegionX(), textureComponent.getRegion().getRegionY(),
                    worldWidth, worldHeight,
                    gameObject.getScaleX(), gameObject.getScaleY(),
                    gameObject.getAngle());


            shapeRenderer.circle(drawX, drawY, 10);
    }
        updateCameraPosition();
        spriteBatch.end();
        shapeRenderer.end();
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

        if (!config.isMasterView()) {
            camera.position.set(renderComponent.getGameObject().getX(), renderComponent.getGameObject().getY(), 0);
        }

        camera.update();
    }

//    private void drawPositions(Entity entity, RenderComponent renderComponent) {
//        if (LOCAL_PLAYER_MAPPER.get(entity) != null) {
//            font.setColor(Color.BLUE);
//            font.draw(spriteBatch, String.format("x: %2f, y: %2f", renderComponent.getPosition().x, renderComponent.getPosition().y),
//                    renderComponent.getPosition().x, renderComponent.getPosition().y);
//        }
//
//        if (REMOTE_PLAYER_MAPPER.get(entity) != null) {
//            font.setColor(Color.RED);
//            font.draw(spriteBatch, String.format("x: %2f, y: %2f", renderComponent.getPosition().x, renderComponent.getPosition().y),
//                    renderComponent.getPosition().x, renderComponent.getPosition().y);
//        }
//
//    }
}
