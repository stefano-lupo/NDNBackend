package com.stefanolupo.ndngame.libgdx.systems.core;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.hubspot.liveconfig.value.Value;
import com.stefanolupo.ndngame.config.LocalConfig;
import com.stefanolupo.ndngame.libgdx.components.LocalPlayerComponent;
import com.stefanolupo.ndngame.libgdx.components.RenderComponent;
import com.stefanolupo.ndngame.libgdx.components.TextureComponent;
import com.stefanolupo.ndngame.libgdx.creators.GameWorldCreator;
import com.stefanolupo.ndngame.libgdx.creators.PlayerCreator;
import com.stefanolupo.ndngame.libgdx.systems.HasComponentMappers;
import com.stefanolupo.ndngame.protos.GameObject;
import com.stefanolupo.ndngame.protos.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;

public class RenderingSystem
        extends SortedIteratingSystem
        implements HasComponentMappers {

    private static final Logger LOG = LoggerFactory.getLogger(RenderingSystem.class);
    private static final Comparator<Entity> Z_COMPARATOR =
            Comparator.comparing(e -> RENDER_MAPPER.get(e).getGameObject().getZ());
    private static final float PIXELS_PER_METER = 40f;

    private static final float REMOTE_STATUS_BAR_MAX_WIDTH = 1.5f * PlayerCreator.PLAYER_WIDTH;
    private static final float REMOTE_STATUS_BAR_HEIGHT = 0.3f;

    private final LocalConfig localConfig;
    private final OrthographicCamera camera;
    private final Value<Float> innerRadius;
    private final Value<Float> outerRadius;

    private final Array<Entity> renderQueue;

    private SpriteBatch spriteBatch;
    private ShapeRenderer filledRenderer;
    private ShapeRenderer lineRenderer;

    @Inject
    public RenderingSystem(LocalConfig localConfig,
                           OrthographicCamera camera,
                           @Named("linear.interest.zone.filter.inner.radius") Value<Float> innerRadius,
                           @Named("linear.interest.zone.filter.outer.radius") Value<Float> outerRadius) {
        super(Family.all(RenderComponent.class, TextureComponent.class).get(), Z_COMPARATOR);
        this.localConfig = localConfig;
        this.camera = camera;
        this.innerRadius = innerRadius;
        this.outerRadius = outerRadius;
        renderQueue = new Array<>();
    }

    /**
     * These require LibGdx to be set up and must be run before rendering
     */
    public void configureOnInit(SpriteBatch spriteBatch) {
        this.spriteBatch = spriteBatch;
        if (localConfig.isMasterView()) {
            camera.setToOrtho(false, GameWorldCreator.WORLD_WIDTH, GameWorldCreator.WORLD_HEIGHT);
        } else {
            camera.setToOrtho(false,
                    Gdx.graphics.getWidth() / PIXELS_PER_METER,
                    Gdx.graphics.getHeight() / PIXELS_PER_METER);
        }
        camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
        camera.update();
        LOG.info("Camera viewport: {} x {} units", camera.viewportHeight, camera.viewportHeight);

        filledRenderer = new ShapeRenderer();
        filledRenderer.setProjectionMatrix(camera.combined);

        lineRenderer = new ShapeRenderer();
        lineRenderer.setProjectionMatrix(camera.combined);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        Preconditions.checkArgument(spriteBatch != null,
                "configureOnInit must be called before rendering can start");

        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.enableBlending();
        spriteBatch.begin();
        filledRenderer.begin(ShapeRenderer.ShapeType.Filled);
        lineRenderer.begin(ShapeRenderer.ShapeType.Line);

        renderLocalPlayerStatus();


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

            drawInterestZone(entity, drawX, drawY);

            if (REMOTE_PLAYER_MAPPER.get(entity) != null) {
                renderRemotePlayerStatus(entity, gameObject);
            }

    }
        updateCameraPosition();
        spriteBatch.end();
        filledRenderer.end();
        lineRenderer.end();
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

        if (!localConfig.isMasterView()) {
            camera.position.set(renderComponent.getGameObject().getX(), renderComponent.getGameObject().getY(), 0);
        }

        camera.update();
    }

    private void drawInterestZone(Entity entity, float drawX, float drawY) {
        if (REMOTE_PLAYER_MAPPER.get(entity) != null || LOCAL_PLAYER_MAPPER.get(entity) != null) {
            lineRenderer.circle(drawX, drawY, innerRadius.get());
            lineRenderer.circle(drawX, drawY, outerRadius.get());
        }

    }

    private void renderLocalPlayerStatus() {
        Entity localPlayer = getEngine().getEntitiesFor(Family.all(LocalPlayerComponent.class).get()).get(0);
        Status status = STATUS_MAPPER.get(localPlayer).getStatus();
        float radius = camera.viewportWidth / 50;
        float offset = radius;
        float healthHeight = camera.viewportHeight / 10;
        float ammoHeight = healthHeight - (offset + radius);

        filledRenderer.setColor(Color.FIREBRICK);
        for (int i = 0; i < status.getHealth(); i++) {
            filledRenderer.circle(offset + i * (radius + offset), healthHeight, radius);
        }

        filledRenderer.setColor(Color.BLUE);
        for (int i = 0; i <status.getAmmo(); i++) {
            filledRenderer.circle(offset + i * (radius + offset), ammoHeight, radius);
        }

    }

    private void renderRemotePlayerStatus(Entity entity, GameObject gameObject) {
        Status status = STATUS_MAPPER.get(entity).getStatus();
        int health = status.getHealth();
        int ammo = status.getAmmo();

        float healthBarWidth = REMOTE_STATUS_BAR_MAX_WIDTH * (health + 0f) / PlayerCreator.MAX_HEALTH;
        float ammoBarWidth = REMOTE_STATUS_BAR_MAX_WIDTH * (ammo + 0f) / PlayerCreator.MAX_AMMO;

        float drawX = gameObject.getX() - (REMOTE_STATUS_BAR_MAX_WIDTH / 2);
        float drawY = gameObject.getY() + 1;

        filledRenderer.setColor(Color.FIREBRICK);
        filledRenderer.rect(drawX, drawY, healthBarWidth, REMOTE_STATUS_BAR_HEIGHT);
        filledRenderer.setColor(Color.BLUE);
        filledRenderer.rect(drawX, drawY + REMOTE_STATUS_BAR_HEIGHT + 0.1f, ammoBarWidth, REMOTE_STATUS_BAR_HEIGHT);
    }
}
