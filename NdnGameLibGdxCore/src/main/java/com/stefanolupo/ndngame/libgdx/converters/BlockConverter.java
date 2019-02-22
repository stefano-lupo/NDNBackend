package com.stefanolupo.ndngame.libgdx.converters;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Body;
import com.stefanolupo.ndngame.libgdx.components.BlockComponent;
import com.stefanolupo.ndngame.libgdx.components.BodyComponent;
import com.stefanolupo.ndngame.libgdx.components.RenderComponent;
import com.stefanolupo.ndngame.libgdx.systems.HasComponentMappers;
import com.stefanolupo.ndngame.protos.Block;
import com.stefanolupo.ndngame.protos.Transform;

/**
 * Maps to and from BlockProto and Game engine entities
 */
public class BlockConverter implements HasComponentMappers {

    public static Block blockEntityToProto(Entity entity) {

        // TODO: Some code to pull or throw from entity would be nice
        BlockComponent blockComponent = BLOCK_MAPPER.get(entity);
        BodyComponent bodyComponent = BODY_MAPPER.get(entity);
        RenderComponent renderComponent = RENDER_MAPPER.get(entity);

        return Block.newBuilder()
                .setId(blockComponent.getBlockName().getId())
                .setTransform(buildTransform(bodyComponent, renderComponent))
                .setHealth(blockComponent.getHealth())
                .build();
    }

    public static void reconcileRemoteBlock(Entity entity, Block block) {
        BodyComponent bodyComponent = BODY_MAPPER.get(entity);
        BlockComponent blockComponent = BLOCK_MAPPER.get(entity);

        setTransform(bodyComponent, block.getTransform());
        blockComponent.setHealth(block.getHealth());
    }

    private static void setTransform(BodyComponent bodyComponent, Transform transform) {
        bodyComponent.getBody()
                .setTransform(transform.getX(), transform.getY(), transform.getRotation());
    }

    private static Transform buildTransform(BodyComponent bodyComponent, RenderComponent renderComponent) {
        Body body = bodyComponent.getBody();
        return Transform.newBuilder()
                .setX(body.getPosition().x)
                .setY(body.getPosition().y)
                .setWidth(renderComponent.getWidth())
                .setHeight(renderComponent.getHeight())
                .setRotation(renderComponent.getRotation())
                .setScaleX(renderComponent.getScale().x)
                .setScaleY(renderComponent.getScale().y)
                .build();
    }
}
