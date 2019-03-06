package com.stefanolupo.ndngame.libgdx.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.stefanolupo.ndngame.protos.Attack;

public class AttackComponent implements Component {
    private Attack attack;
    private Vector2 mouseCoords;

    public Attack getAttack() {
        return attack;
    }

    public void setAttack(Attack attack) {
        this.attack = attack;
    }

    public Vector2 getMouseCoords() {
        return mouseCoords;
    }

    public void setMouseCoords(Vector2 mouseCoords) {
        this.mouseCoords = mouseCoords;
    }
}
