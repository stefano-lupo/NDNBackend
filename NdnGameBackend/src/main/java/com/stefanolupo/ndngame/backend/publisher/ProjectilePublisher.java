package com.stefanolupo.ndngame.backend.publisher;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.hubspot.liveconfig.value.Value;
import com.stefanolupo.ndngame.backend.ndn.FaceManager;
import com.stefanolupo.ndngame.config.LocalConfig;
import com.stefanolupo.ndngame.names.projectiles.ProjectileName;
import com.stefanolupo.ndngame.names.projectiles.ProjectilesSyncName;
import com.stefanolupo.ndngame.protos.Projectile;
import com.stefanolupo.ndngame.protos.Projectiles;
import net.named_data.jndn.*;
import net.named_data.jndn.util.Blob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Singleton
public class ProjectilePublisher {

    private static final Logger LOG = LoggerFactory.getLogger(BlockPublisher.class);

    private final ConcurrentMap<ProjectilesSyncName, Face> outstandingInterests = new ConcurrentHashMap<>();
    private final SequenceNumberedCache<Projectile> projectileCache;
    private final Value<Double> freshnessPeriod;

    @Inject
    public ProjectilePublisher(LocalConfig localConfig,
                               FaceManager faceManager,
                               @Named("projectile.cache.size") Value<Integer> cacheSize,
                               @Named("projectile.publisher.freshness.period.ms") Value<Double> freshnessPeriod) {
        this.freshnessPeriod = freshnessPeriod;
        projectileCache = SequenceNumberedCache.getInstance(cacheSize.get());

        ProjectilesSyncName projectilesSyncName = new ProjectilesSyncName(localConfig.getGameId(), localConfig.getPlayerName());

        faceManager.registerBasicPrefix(projectilesSyncName.getAsPrefix(), this::onSyncInterest);

        ProjectileName projectileName = new ProjectileName(localConfig.getGameId(), localConfig.getPlayerName());
        faceManager.registerBasicPrefix(projectileName.getAsPrefix(), this::onInteractionInterest);

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(
                this::processOutstandingInterests,
                0, 20, TimeUnit.MILLISECONDS
        );
    }

    public void insertProjectile(ProjectileName projectileName, Projectile projectile) {
        projectileCache.insert(projectile);
    }


    private void onInteractionInterest(Name prefix, Interest interest, Face face, long interestFilterId, InterestFilter filter) {
        ProjectileName projectileName = new ProjectileName(interest);
        LOG.debug("Got interaction for {}", interest.toUri());
    }

    private void processOutstandingInterests() {
        for (Iterator<Map.Entry<ProjectilesSyncName, Face>> it = outstandingInterests.entrySet().iterator(); it.hasNext();) {
            Map.Entry<ProjectilesSyncName, Face> entry = it.next();
            ProjectilesSyncName name = entry.getKey();
            Face face = entry.getValue();

            List<Projectile> projectiles = projectileCache.getFrom(name.getLatestSequenceNumberSeen() + 1);
            if (projectiles.isEmpty()) continue;

            name.setNextSequenceNumber(projectileCache.getMaxVal());
            Data data = new Data(name.getFullName());
            MetaInfo metaInfo = new MetaInfo();
            metaInfo.setFreshnessPeriod(freshnessPeriod.get());
            data.setMetaInfo(metaInfo);
            Blob blob = new Blob(Projectiles.newBuilder()
                    .addAllProjectiles(projectiles)
                    .build().toByteArray());
            data.setContent(blob);

            try {
                face.putData(data);
                it.remove();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void onSyncInterest(Name prefix, Interest interest, Face face, long interestFilterId, InterestFilter filter) {
        ProjectilesSyncName syncName = new ProjectilesSyncName(interest);
        outstandingInterests.put(syncName, face);
    }
}
