package com.stefanolupo.ndngame.backend.entities;

import com.stefanolupo.ndngame.protos.BulletStatus;

public class Bullet {

    private static final int DEFAULT_BULLET_SPEED = 10;

    private BulletStatus bulletStatus;

    public Bullet(BulletStatus bulletStatus) {
        this.bulletStatus = bulletStatus;
    }

    public void update(BulletStatus bulletStatus) {
        this.bulletStatus = bulletStatus;
    }

    public BulletStatus getBulletStatus() {
        return bulletStatus;
    }

    public void tick() {
        bulletStatus = bulletStatus.toBuilder()
                .setX(bulletStatus.getX() + DEFAULT_BULLET_SPEED * bulletStatus.getVelX())
                .setY(bulletStatus.getY() + DEFAULT_BULLET_SPEED * bulletStatus.getVelY())
                .build();
    }
}
