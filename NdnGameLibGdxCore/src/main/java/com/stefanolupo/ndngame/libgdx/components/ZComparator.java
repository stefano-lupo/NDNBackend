package com.stefanolupo.ndngame.libgdx.components;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

import java.util.Comparator;

public class ZComparator implements Comparator<Entity> {

    private final ComponentMapper<RenderComponent> transformComponentComponentMapper;

    public ZComparator() {
        transformComponentComponentMapper = ComponentMapper.getFor(RenderComponent.class);
    }

    @Override
    public int compare(Entity e1, Entity e2) {

        float e1Z = transformComponentComponentMapper.get(e1).getPosition().z;
        float e2Z = transformComponentComponentMapper.get(e2).getPosition().z;

        return Float.compare(e1Z, e2Z);
    }
}
