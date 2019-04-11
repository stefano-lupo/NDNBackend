package com.stefanolupo.ndngame.backend.publisher;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.hubspot.liveconfig.value.Value;
import com.stefanolupo.ndngame.backend.annotations.BackendMetrics;
import com.stefanolupo.ndngame.backend.ndn.FaceManager;
import com.stefanolupo.ndngame.config.LocalConfig;
import com.stefanolupo.ndngame.metrics.MetricNames;
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
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.stefanolupo.ndngame.util.MathUtils.MICRO_SECONDS_PER_SEC;

@Singleton
public class ProjectilePublisher {

    private static final Logger LOG = LoggerFactory.getLogger(BlockPublisher.class);

    private final Supplier<Meter> interestMeterDelayedSupplier;
    private Meter interestMeter;
    private final ConcurrentMap<ProjectilesSyncName, Face> outstandingInterests = new ConcurrentHashMap<>();
    private final SequenceNumberedCache<Projectile> projectileCache;
    private final Value<Double> freshnessPeriod;
    private final Histogram projectilesPacketSizeHist;

    private final Consumer<DataSend> dataSendConsumer;

    @Inject
    public ProjectilePublisher(LocalConfig localConfig,
                               FaceManager faceManager,
                               @BackendMetrics MetricRegistry metrics,
                               @Named("projectile.publisher.queue.process.per.sec") Value<Long> queueProcPerSec,
                               @Named("projectile.publisher.queue.process.multithread") Value<Boolean> queueProcMultithread,
                               @Named("projectile.cache.size") Value<Integer> cacheSize,
                               @Named("projectile.publisher.freshness.period.ms") Value<Double> freshnessPeriod) {
        this.freshnessPeriod = freshnessPeriod;
        projectilesPacketSizeHist = metrics.histogram(MetricNames.packetSizeHistogram(MetricNames.PacketSizeType.PROJECTILE));
        projectileCache = SequenceNumberedCache.getInstance(cacheSize.get());
        if (queueProcMultithread.get()) {
            ThreadFactory factory = new ThreadFactoryBuilder()
                    .setNameFormat("projectile-publisher-sender")
                    .build();
            ExecutorService executorService = Executors.newCachedThreadPool(factory);
            dataSendConsumer = (ds -> CompletableFuture.runAsync(() -> doSendData(ds), executorService)
                        .exceptionally(e -> {
                            LOG.error("Unable to send projectile update", e);
                            return null;
                        })
            );
        } else {
            dataSendConsumer = this::doSendData;
        }

        ProjectilesSyncName projectilesSyncName = new ProjectilesSyncName(localConfig.getGameId(), localConfig.getPlayerName());
        this.interestMeterDelayedSupplier = () ->  metrics.meter(MetricNames.basePublisherInterestRate(projectilesSyncName.getAsPrefix()));

        faceManager.registerBasicPrefix(projectilesSyncName.getAsPrefix(), this::onSyncInterest);

        ProjectileName projectileName = new ProjectileName(localConfig.getGameId(), localConfig.getPlayerName());
        faceManager.registerBasicPrefix(projectileName.getAsPrefix(), this::onInteractionInterest);

        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat("projectile-publisher-%d")
                .build();
        Executors.newSingleThreadScheduledExecutor(threadFactory).scheduleAtFixedRate(
                this::processOutstandingInterests,
                0, MICRO_SECONDS_PER_SEC / queueProcPerSec.get(), TimeUnit.MICROSECONDS
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

            long sequenceNumber = name.getLatestSequenceNumberSeen();
            if (sequenceNumber >= projectileCache.getMaxVal()) continue;

            Projectiles projectiles = Projectiles.newBuilder()
                .addAllProjectiles(projectileCache.getFrom(sequenceNumber + 1))
                .build();
            projectilesPacketSizeHist.update(projectiles.getSerializedSize());
            Blob blob = new Blob(projectiles.toByteArray());

            dataSendConsumer.accept(new DataSend(face, name, blob));
            it.remove();
        }
    }

    private void doSendData(DataSend dataSend) {
        dataSend.getName().setNextSequenceNumber(projectileCache.getMaxVal());
        long now = System.currentTimeMillis();
        dataSend.getName().setUpdateTimestamp(now);
        Data data = new Data(dataSend.getName().getFullName()).setContent(dataSend.getBlob());
        data.getMetaInfo().setFreshnessPeriod(freshnessPeriod.get());
        try {
            dataSend.getFace().putData(data);
        } catch (IOException e) {
            LOG.error("Got error sending projectile data {}", e);
            throw new RuntimeException(e);
        }
    }

    private void onSyncInterest(Name prefix, Interest interest, Face face, long interestFilterId, InterestFilter filter) {
        if (interestMeter == null) {
            LOG.info("Creating interest meter");
            interestMeter = interestMeterDelayedSupplier.get();
        }
        interestMeter.mark();
        ProjectilesSyncName syncName = new ProjectilesSyncName(interest);
        outstandingInterests.put(syncName, face);
    }
}
