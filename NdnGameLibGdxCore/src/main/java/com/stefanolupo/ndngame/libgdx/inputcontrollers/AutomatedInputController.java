package com.stefanolupo.ndngame.libgdx.inputcontrollers;

import com.badlogic.gdx.math.Vector2;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.stefanolupo.ndngame.config.LocalConfig;
import com.stefanolupo.ndngame.libgdx.creators.GameWorldCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

@Singleton
public class AutomatedInputController implements InputController {
    private static final Logger LOG = LoggerFactory.getLogger(AutomatedInputController.class);

    public static final long SEED = 10;
    private static final int WALK_TIME_MS = 400;
    private static final double WALK_RATE = 0;
    private static final double PLACE_RATE = 0.95;
    private static final double SHOOT_RATE = 0.75;
    private static final Random RANDOM = new Random(SEED);

    private static int count = 0;
    private static final List<Integer> moves = Arrays.asList(1, 2, 3, 4);
    private final boolean shouldWalk;
    private final boolean shouldPlace;
    private final boolean shouldShoot;

    private boolean isLeftPressed = false;
    private boolean isRightPressed = false;
    private boolean isUpPressed = false;
    private boolean isDownPressed = false;
    private boolean isSpacePressed = false;

    private boolean isMouse1Pressed = false;
    private boolean isMouse2Pressed = false;
    private boolean isMouse3Pressed = false;

    @Inject
    public AutomatedInputController(LocalConfig localConfig) {
        String automationType = localConfig.getAutomationType();
        shouldWalk = automationType.contains("w");
        shouldPlace = automationType.contains("p");
        shouldShoot = automationType.contains("s");
        LOG.debug("Starting with automation type: {}", automationType);

        ThreadFactory namedThreadFactory =
                new ThreadFactoryBuilder().setNameFormat("automated-controller-%d").build();
        Executors.newSingleThreadScheduledExecutor(namedThreadFactory)
                .scheduleAtFixedRate(this::moveOnPath, 2000, WALK_TIME_MS, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean isLeftPressed() {
        return isLeftPressed;
    }

    @Override
    public boolean isRightPressed() {
        return isRightPressed;
    }

    @Override
    public boolean isUpPressed() {
        return isUpPressed;
    }

    @Override
    public boolean isDownPressed() {
        return isDownPressed;
    }

    @Override
    public boolean isSpacePressed() {
        return isSpacePressed;
    }

    @Override
    public boolean isMouse1Down() {
        return isMouse1Pressed;
    }

    @Override
    public boolean isMouse2Down() {
        return isMouse2Pressed;
    }

    @Override
    public boolean isMouse3Down() {
        return isMouse3Pressed;
    }

    @Override
    public Vector2 getMouseCoords() {
        return new Vector2(GameWorldCreator.WORLD_WIDTH * (float)Math.random(), GameWorldCreator.WORLD_HEIGHT * (float)Math.random());
    }

    private void moveOnPath() {
        setAllFalse();

        double random = RANDOM.nextDouble();

        if (maybePlace(random)) return;
        if (maybeShoot(random)) return;
        if (maybeWalk(random)) return;
    }

    private boolean maybeWalk(double random) {

        if (!shouldWalk || random < WALK_RATE) {
            return false;
        }

        // Stand still for a tick
        count = ++count % (moves.size() + 1);

        if (count == 0) {
            isRightPressed = true;
        } else if (count == 1) {
            isDownPressed = true;
        } else if (count == 2) {
            isLeftPressed = true;
        } else if (count == 3) {
            isUpPressed = true;
        }


        return true;

    }

    private boolean maybePlace(double random) {
        if (!shouldPlace || random < PLACE_RATE) return false;

        isSpacePressed = true;
        return true;
    }

    private boolean maybeShoot(double random) {
        if (!shouldShoot || random < SHOOT_RATE) return false;

        double shotType = SHOOT_RATE + ((1-SHOOT_RATE)/ 2);
        System.out.println("Random " + random + ", shoot rate: " + shotType);
        if (random > shotType) {
            isMouse3Pressed = true;
        } else {
            isMouse2Pressed = true;
        }
        return true;
    }

    private void setAllFalse() {
        isSpacePressed = false;
        isLeftPressed = false;
        isRightPressed = false;
        isUpPressed = false;
        isDownPressed = false;
        isMouse1Pressed = false;
        isMouse2Pressed = false;
        isMouse3Pressed = false;
    }
}
