package com.stefanolupo.ndngame.libgdx.systems.local;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalSystem;
import com.google.inject.Inject;
import com.stefanolupo.ndngame.backend.LocalPlayerReference;
import com.stefanolupo.ndngame.libgdx.components.LocalPlayerComponent;
import com.stefanolupo.ndngame.libgdx.converters.PlayerStatusConverter;
import com.stefanolupo.ndngame.libgdx.systems.HasComponentMappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Pushes updates to Player Status from engine to PlayerStatusPublisher
 */
public class LocalPlayerStatusSystem
        extends IntervalSystem
        implements HasComponentMappers {

    private static final Logger LOG = LoggerFactory.getLogger(LocalPlayerStatusSystem.class);
    private static final Float UPDATE_INTERVAL_SEC = 20f / 1000;

    private final LocalPlayerReference localPlayerReference;

    @Inject
    public LocalPlayerStatusSystem(LocalPlayerReference localPlayerReference) {
        super(UPDATE_INTERVAL_SEC);
        this.localPlayerReference = localPlayerReference;
    }

    @Override
    protected void updateInterval() {
        Entity playerEntity = getEngine().getEntitiesFor(Family.all(LocalPlayerComponent.class).get()).get(0);
        localPlayerReference.setPlayerStatus(PlayerStatusConverter.protoFromEntity(playerEntity));
    }
}
