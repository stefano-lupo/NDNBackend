package com.stefanolupo.ndngame.backend.entities;

import com.stefanolupo.ndngame.protos.BulletStatus;

public class Bullet {
    private BulletStatus bulletStatus;

    public Bullet(BulletStatus bulletStatus) {
        this.bulletStatus = bulletStatus;
    }

    public Bullet(int x, int y) {
        bulletStatus = BulletStatus.newBuilder()
                .setX(x)
                .setY(y)
                .setDamage(99)
                .setId(System.currentTimeMillis())
                .build();
    }
}
