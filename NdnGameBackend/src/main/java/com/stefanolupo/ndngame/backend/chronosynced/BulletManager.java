package com.stefanolupo.ndngame.backend.chronosynced;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.protobuf.InvalidProtocolBufferException;
import com.stefanolupo.ndngame.backend.entities.Bullet;
import com.stefanolupo.ndngame.config.Config;
import com.stefanolupo.ndngame.names.BulletsName;
import com.stefanolupo.ndngame.protos.BulletRemove;
import com.stefanolupo.ndngame.protos.BulletsList;
import net.named_data.jndn.Data;
import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;
import net.named_data.jndn.sync.ChronoSync2013;
import net.named_data.jndn.util.Blob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Singleton
public class BulletManager extends ChronoSyncedDataStructure {

    private static final Logger LOG = LoggerFactory.getLogger(BulletManager.class);
    private static final String BROADCAST_PREFIX = "/com/stefanolupo/ndngame/%d/bullet/broadcast";
    private static final long BULLET_COOLDOWN_MS = 500L;

    private long lastBulletShot = 0;
    private final Map<Long, Bullet> localBulletsById = new HashMap<>();
    private final Map<Long, Bullet> remoteBulletsById = new HashMap<>();

    private final Config config;

    @Inject
    public BulletManager(Config config) {
        super(new Name(String.format(BROADCAST_PREFIX, config.getGameId())),
                new BulletsName(config.getGameId(), config.getPlayerName()).getListenName());
        this.config = config;
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            LOG.info("{} bullets active", remoteBulletsById.size());
        },  5, 5, TimeUnit.SECONDS);
    }

    public void addLocalBullet(Bullet bullet) {
        long time = System.currentTimeMillis();
        if (time - lastBulletShot > BULLET_COOLDOWN_MS) {
            localBulletsById.put(bullet.getBulletStatus().getId(), bullet);
            lastBulletShot = time;
            publishUpdate();
        }
    }

    @Override
    public void onData(Interest interest, Data data) {
        try {
            List<Bullet> bullets = getBulletsFromData(data);
            bullets.forEach(b -> remoteBulletsById.put(b.getBulletStatus().getId(), b));
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException("Unable to parse incoming bullet from data", e);
        }
    }

    @Override
    protected Optional<Blob> localToBlob(Interest interest) {
        return Optional.of(new Blob(BulletsList.newBuilder()
                .addAllBulletStatuses(localBulletsById.values().stream().map(Bullet::getBulletStatus).collect(Collectors.toList()))
                .build().toByteArray()));
    }

    @Override
    protected Collection<Interest> syncStatesToInterests(List<ChronoSync2013.SyncState> syncStates, boolean isRecovery) {
        List<Interest> interests = new ArrayList<>();
        for (ChronoSync2013.SyncState syncState : syncStates) {
            BulletsName bulletsName = new BulletsName(syncState);
            if (bulletsName.getPlayerName().equals(config.getPlayerName())) {
                continue;
            }

            if (!syncState.getApplicationInfo().isNull()) {
                removeBullet(syncState);
            } else {
                if (bulletsName.getPlayerName().equals(config.getPlayerName())) {
                    return Collections.emptyList();
                }

                interests.add(bulletsName.toInterest());
            }
        }

        return interests;
    }

    public void destroyBullet(Bullet bullet) {
        long bulletId = bullet.getBulletStatus().getId();
        localBulletsById.remove(bulletId);

        BulletRemove bulletRemove = BulletRemove.newBuilder().setId(bulletId).build();
        publishUpdate(bulletRemove.toByteArray());
    }

    public Collection<Bullet> getLocalBullets() {
        return Collections.unmodifiableCollection(localBulletsById.values());
    }

    public Collection<Bullet> getRemoteBullets() {
        return Collections.unmodifiableCollection(remoteBulletsById.values());
    }

    public Collection<Bullet> getAllBullets() {
        return Stream.concat(getLocalBullets().stream(), getRemoteBullets().stream())
                .collect(Collectors.toList());
    }

    private List<Bullet> getBulletsFromData(Data data) throws InvalidProtocolBufferException {
        BulletsList bullets = BulletsList.parseFrom(data.getContent().getImmutableArray());
        return bullets.getBulletStatusesList().stream()
                .map(Bullet::new)
                .collect(Collectors.toList());
    }

    private void removeBullet(ChronoSync2013.SyncState syncState) {
        try {
            long id = BulletRemove.parseFrom(syncState.getApplicationInfo().getImmutableArray()).getId();
            LOG.info("Asked to remove remote bullet: {}", id);
            if (!remoteBulletsById.containsKey(id)) {
                LOG.info("{} not found in remote bullet map", id);
            } else {
                remoteBulletsById.remove(id);
                LOG.info("Removed bullet: {}", id);
            }
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }
    }
}
