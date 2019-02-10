package com.stefanolupo.ndngame.libgdx.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.stefanolupo.ndngame.libgdx.components.BodyComponent;
import com.stefanolupo.ndngame.libgdx.components.RenderComponent;

/**
 * Performs a world update, updating the positions of all bodies based on their velocities
 * Also sets up the RenderComponents for all of the entities with Bodies based on this world update
 *
 * super.update() calls process entity for each entity
 *      this is overidden and just adds bodies to be processed to the array
 * Then in remainder of update we step world and setup RenderComponents
 */
public class PhysicsSystem
        extends IteratingSystem
        implements HasComponentMappers {

    private static final float MAX_STEP_TIME = 1/45f;

    private final World world;
    private final Array<Entity> bodiesQueue;

    public PhysicsSystem(World world) {
        super(Family.all(BodyComponent.class, RenderComponent.class).get());
        this.world = world;
        bodiesQueue = new Array<>();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        bodiesQueue.add(entity);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        // Updates positions of all of the bodies in the world
        // Also invokes contact listeners
        world.step(MAX_STEP_TIME, 6, 2);

        // Create RenderComponents for each of the renderable bodies
        for (Entity entity : bodiesQueue) {
            Body body = BODY_MAPPER.get(entity).getBody();
            Vector2 bodyPosition = body.getPosition();

            RenderComponent renderComponent = TRANSFORM_MAPPER.get(entity);
            renderComponent.setPosition(bodyPosition.x, bodyPosition.y);
            renderComponent.setRotation(body.getAngle() * MathUtils.radiansToDegrees);
        }

        bodiesQueue.clear();
    }
}
