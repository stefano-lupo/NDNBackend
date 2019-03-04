package com.stefanolupo.ndngame.libgdx.components;

import com.badlogic.ashley.core.Component;
import com.stefanolupo.ndngame.names.projectiles.ProjectileName;

public class ProjectileComponent implements Component {

    private ProjectileName projectileName;
    private boolean isRemote;
    private int damage;

    public void setProjectileName(ProjectileName projectileName) {
        this.projectileName = projectileName;
    }

    public ProjectileName getProjectileName() {
        return projectileName;
    }

    public boolean isRemote() {
        return isRemote;
    }

    public void setRemote(boolean remote) {
        isRemote = remote;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }
}
