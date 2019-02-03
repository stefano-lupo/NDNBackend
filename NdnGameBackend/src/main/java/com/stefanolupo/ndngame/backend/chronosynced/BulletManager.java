package com.stefanolupo.ndngame.backend.chronosynced;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.protobuf.InvalidProtocolBufferException;
import com.stefanolupo.ndngame.backend.entities.Bullet;
import com.stefanolupo.ndngame.config.Config;
import com.stefanolupo.ndngame.names.BulletsName;
import com.stefanolupo.ndngame.protos.BulletsList;
import net.named_data.jndn.Data;
import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;
import net.named_data.jndn.sync.ChronoSync2013;
import net.named_data.jndn.util.Blob;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Singleton
public class BulletManager extends ChronoSyncedMap<BulletsName, List<Bullet>> {

    private static final String BROADCAST_PREFIX = "/com/stefanolupo/ndngame/%d/bullet/broadcast";
    private static final long BULLET_COOLDOWN_MS = 500L;

    private long lastBulletShot = 0;

    private final List<Bullet> localBullets = new ArrayList<>();
    private final Config config;

    @Inject
    public BulletManager(Config config) {
        super(new Name(String.format(BROADCAST_PREFIX, config.getGameId())),
                new BulletsName(config.getGameId(), config.getPlayerName()).getListenName());
        this.config = config;
    }

    public void addLocalBullet(Bullet bullet) {
        long time = System.currentTimeMillis();
        if (time - lastBulletShot > BULLET_COOLDOWN_MS) {
            localBullets.add(bullet);
            lastBulletShot = time;
            publishUpdate();
        }
    }

    @Override
    protected BulletsName interestToKey(Interest interest) {
        return new BulletsName(interest);
    }

    @Override
    protected List<Bullet> dataToVal(Data data, BulletsName key, List<Bullet> oldVal) {
        try {
            BulletsList bullets = BulletsList.parseFrom(data.getContent().getImmutableArray());
            return bullets.getBulletStatusesList().stream()
                    .map(Bullet::new)
                    .collect(Collectors.toList());
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException("Unable to parse incoming bullet from data", e);
        }
    }

    @Override
    protected Optional<Blob> localToBlob(Interest interest) {
        return Optional.of(new Blob(BulletsList.newBuilder()
                .addAllBulletStatuses(localBullets.stream().map(Bullet::getBulletStatus).collect(Collectors.toList()))
                .build().toByteArray()));
    }

    @Override
    protected Collection<Interest> syncStatesToInterests(List<ChronoSync2013.SyncState> syncStates, boolean isRecovery) {
        return syncStates.stream()
                .map(BulletsName::new)
                .filter(bn -> !bn.getPlayerName().equals(config.getPlayerName()))
                .map(BulletsName::toInterest)
                .collect(Collectors.toList());
    }

    public List<Bullet> getLocalBullets() {
        return localBullets;
    }

    public List<Bullet> getRemoteBullets() {
        List<Bullet> remoteBullets = new ArrayList<>();
        for (List<Bullet> bullets : getMap().values()) {
            remoteBullets.addAll(bullets);
        }

        return remoteBullets;
    }
    public List<Bullet> getAllBullets() {
        return Stream.concat(getLocalBullets().stream(), getRemoteBullets().stream())
                .collect(Collectors.toList());
    }
}
