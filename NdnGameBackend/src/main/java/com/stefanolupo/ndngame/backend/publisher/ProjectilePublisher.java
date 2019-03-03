package com.stefanolupo.ndngame.backend.publisher;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.hubspot.liveconfig.value.Value;
import com.stefanolupo.ndngame.backend.ndn.BasePublisherFactory;
import com.stefanolupo.ndngame.backend.ndn.FaceManager;
import com.stefanolupo.ndngame.config.LocalConfig;
import com.stefanolupo.ndngame.names.projectiles.ProjectileName;
import com.stefanolupo.ndngame.names.projectiles.ProjectilesSyncName;
import com.stefanolupo.ndngame.protos.Projectile;
import com.stefanolupo.ndngame.protos.Projectiles;
import net.named_data.jndn.Face;
import net.named_data.jndn.Interest;
import net.named_data.jndn.InterestFilter;
import net.named_data.jndn.Name;
import net.named_data.jndn.util.Blob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@Singleton
public class ProjectilePublisher {

    private static final Logger LOG = LoggerFactory.getLogger(BlockPublisher.class);

    private final Map<ProjectileName, Projectile> localProjectilesByName = new HashMap<>();
    private final FaceManager faceManager;
    private final BasePublisher publisher;

    @Inject
    public ProjectilePublisher(LocalConfig localConfig,
                               BasePublisherFactory factory,
                               FaceManager faceManager,
                               @Named("projectile.publisher.freshness.period.ms") Value<Double> freshnessPeriod) {
        this.faceManager = faceManager;

        ProjectilesSyncName projectilesSyncName = new ProjectilesSyncName(localConfig.getGameId(), localConfig.getPlayerName());
        publisher = factory.create(projectilesSyncName.getAsPrefix(), ProjectilesSyncName::new, freshnessPeriod);

        ProjectileName projectileName = new ProjectileName(localConfig.getGameId(), localConfig.getPlayerName());
        faceManager.registerBasicPrefix(projectileName.getAsPrefix(), this::onInteractionInterest);
    }

    public void upsertProjectile(ProjectileName projectileName, Projectile projectile) {
        localProjectilesByName.put(projectileName, projectile);
        updateBlob();
    }

    public void upsertBatch(Map<ProjectileName, Projectile> projectileUpdates) {
        localProjectilesByName.putAll(projectileUpdates);
        updateBlob();
    }

    public void removeProjectile(ProjectileName projectileName) {
        localProjectilesByName.remove(projectileName);
        updateBlob();
    }

    public Map<ProjectileName, Projectile> getLocalProjectiles() {
        return localProjectilesByName;
    }

    private void updateBlob() {
        Projectiles projectiles = Projectiles.newBuilder().addAllProjectiles(localProjectilesByName.values()).build();
        publisher.updateLatestBlob(new Blob(projectiles.toByteArray()));
    }

    private void onInteractionInterest(Name prefix, Interest interest, Face face, long interestFilterId, InterestFilter filter) {
        ProjectileName projectileName = new ProjectileName(interest);
        removeProjectile(projectileName);
//        if (projectile != null) {
//            Block updatedBlock = projectile.toBuilder()
//                    .setHealth(projectile.getHealth() - 1)
//                    .build();
//            upsertProjectile(projectileName, updatedBlock);
//            LOG.debug("Updated projectile: {}", updatedBlock.getId());
//        }
    }
}
