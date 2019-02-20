package com.stefanolupo.ndngame.libgdx.inputcontrollers;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class RealInputController implements InputProcessor, InputController {

    private static final Logger LOG = LoggerFactory.getLogger(RealInputController.class);

    private boolean left;
    private boolean right;
    private boolean up;
    private boolean down;
    private boolean space;

    private boolean isMouse1Down, isMouse2Down,isMouse3Down;
    private boolean isDragged;
    private Vector2 mouseLocation = new Vector2();

    @Override
    public boolean isLeftPressed() {
        return left;
    }

    @Override
    public boolean isRightPressed() {
        return right;
    }

    @Override
    public boolean isUpPressed() {
        return up;
    }

    @Override
    public boolean isDownPressed() {
        return down;
    }

    @Override
    public boolean isSpacePressed() {
        return space;
    }

    @Override
    public boolean isMouse1Down() {
        return isMouse1Down;
    }

    @Override
    public boolean isMouse2Down() {
        return isMouse2Down;
    }

    @Override
    public boolean isMouse3Down() {
        return isMouse3Down;
    }

    @Override
    public boolean keyDown(int keycode) {
        boolean keyProcessed = false;
        switch (keycode) {
            case Input.Keys.A:
                left = true;
                keyProcessed = true;
                break;
            case Input.Keys.D:
                right = true;
                keyProcessed = true;
                break;
            case Input.Keys.W:
                up = true;
                keyProcessed = true;
                break;
            case Input.Keys.S:
                down = true;
                keyProcessed = true;
                break;
            case Input.Keys.SPACE:
                space = true;
                keyProcessed = true;
                break;
        }
        return keyProcessed;
    }

    @Override
    public boolean keyUp(int keycode) {
        boolean keyProcessed = false;
        switch (keycode)
        {
            case Input.Keys.A:
                left = false;
                keyProcessed = true;
                break;
            case Input.Keys.D:
                right = false;
                keyProcessed = true;
                break;
            case Input.Keys.W:
                up = false;
                keyProcessed = true;
                break;
            case Input.Keys.S:
                down = false;
                keyProcessed = true;
                break;
            case Input.Keys.SPACE:
                space = false;
                keyProcessed = true;
                break;
        }
        return keyProcessed;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if(button == 0){
            isMouse1Down = true;
        }else if(button == 1){
            isMouse2Down = true;
        }else if(button == 2){
            isMouse3Down = true;
        }
        mouseLocation.x = screenX;
        mouseLocation.y = screenY;
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        isDragged = false;
        if(button == 0){
            isMouse1Down = false;
        }else if(button == 1){
            isMouse2Down = false;
        }else if(button == 2){
            isMouse3Down = false;
        }
        mouseLocation.x = screenX;
        mouseLocation.y = screenY;
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        isDragged = true;
        mouseLocation.x = screenX;
        mouseLocation.y = screenY;
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        mouseLocation.x = screenX;
        mouseLocation.y = screenY;
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    public boolean isAttackButtonPressed() {
        return isMouse1Down || isMouse2Down || isMouse3Down;
    }
}
