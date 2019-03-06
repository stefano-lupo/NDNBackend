package com.stefanolupo.ndngame.backend.subscriber;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.protobuf.InvalidProtocolBufferException;
import com.hubspot.liveconfig.value.Value;
import com.stefanolupo.ndngame.backend.chronosynced.OnPlayersDiscovered;
import com.stefanolupo.ndngame.backend.ndn.FaceManager;
import com.stefanolupo.ndngame.backend.statistics.HistogramFactory;
import com.stefanolupo.ndngame.config.LocalConfig;
import com.stefanolupo.ndngame.names.projectiles.ProjectileName;
import com.stefanolupo.ndngame.names.projectiles.ProjectilesSyncName;
import com.stefanolupo.ndngame.protos.Player;
import com.stefanolupo.ndngame.protos.Projectile;
import com.stefanolupo.ndngame.protos.Projectiles;
import net.named_data.jndn.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Singleton
public class ProjectileSubscriber implements OnPlayersDiscovered {

    private static final Logger LOG = LoggerFactory.getLogger(BlockSubscriber.class);

    private final List<BaseSubscriber<Void>> subscribersList = new ArrayList<>();
    private final ConcurrentMap<ProjectileName, Projectile> projectileMap = new ConcurrentHashMap<>();
    private final LocalConfig localConfig;
    private final FaceManager faceManager;
    private final HistogramFactory histogramFactory;
    private final Value<Long> waitTime;

    @Inject
    public ProjectileSubscriber(LocalConfig localConfig,
                                FaceManager faceManager,
                                HistogramFactory histogramFactory,
                                @Named("projectile.sub.inter.interest.max.wait.time.ms") Value<Long> maxWaitTime) {
        this.localConfig = localConfig;
        this.faceManager = faceManager;
        this.histogramFactory = histogramFactory;
        this.waitTime = maxWaitTime;
    }

    private void addSubscription(ProjectilesSyncName projectilesSyncName) {
        LOG.info("Adding subscription for {}", projectilesSyncName);
        BaseSubscriber<Void> subscriber = new BaseSubscriber<>(
                faceManager,
                projectilesSyncName,
                this::typeFromData,
                ProjectilesSyncName::new,
                l -> waitTime.get(),
                histogramFactory.create(ProjectileSubscriber.class, projectilesSyncName.getAsPrefix().toUri())
        );
        subscribersList.add(subscriber);
    }

    public Map<ProjectileName, Projectile> getNewProjectiles() {
        synchronized (projectileMap) {
            Map<ProjectileName, Projectile> copy = new HashMap<>(projectileMap);
            projectileMap.clear();
            return copy;
        }
    }

    // TODO: just update player status etc
    public void interactWithProjectile(ProjectileName projectileName) {
//        for (BaseSubscriber<Map<ProjectileName, Projectile>> subscriber : subscribersList) {
//            if (subscriber.getEntity().containsKey(projectileName)) {
//                Interest interest = projectileName.buildInterest();
//                LOG.info("Interacting with block: {}", interest.toUri());
//                faceManager.expressInterestSafe(interest);
//                return;
//            }
//        }
    }

    private Void typeFromData(Data data) {
        try {
            List<Projectile> projectiles = Projectiles.parseFrom(data.getContent().getImmutableArray()).getProjectilesList();
            ProjectilesSyncName projectilesSyncName = new ProjectilesSyncName(data);

            Map<ProjectileName, Projectile> map = Maps.uniqueIndex(projectiles, b -> ProjectileName.fromProjectileSyncNameAndId(projectilesSyncName, b.getId()));
            projectileMap.putAll(map);
            return null;
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException("Unable to parse Block for %s" + data.getName().toUri(), e);
        }
    }

    @Override
    public void onPlayersDiscovered(Set<Player> players) {
        players.forEach(p -> this.addSubscription(new ProjectilesSyncName(localConfig.getGameId(), p.getName())));
    }
}
