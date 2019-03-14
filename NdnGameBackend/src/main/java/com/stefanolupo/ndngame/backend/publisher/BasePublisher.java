package com.stefanolupo.ndngame.backend.publisher;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;
import com.hubspot.liveconfig.value.Value;
import com.stefanolupo.ndngame.backend.annotations.BackendMetrics;
import com.stefanolupo.ndngame.backend.metrics.PercentageGauge;
import com.stefanolupo.ndngame.backend.ndn.FaceManager;
import com.stefanolupo.ndngame.metrics.MetricNames;
import com.stefanolupo.ndngame.names.SequenceNumberedName;
import net.named_data.jndn.*;
import net.named_data.jndn.util.Blob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.stefanolupo.ndngame.util.MathUtils.MICRO_SECONDS_PER_SEC;

public class BasePublisher implements OnInterestCallback {

    private static final Logger LOG = LoggerFactory.getLogger(BasePublisher.class);

    private final Supplier<Meter> interestMeterDelayedSupplier;
    private final Timer dataSendTimer;
    private Meter interestMeter;
    private final PercentageGauge percentageGauge;

    // Assisted
    private final Function<Interest, SequenceNumberedName> interestTFunction;
    private final Value<Double> freshnessPeriod;

    // Class logic
    private final ConcurrentMap<SequenceNumberedName, Face> outstandingInterests = new ConcurrentHashMap<>();
    private final BiConsumer<DataSend, Long> sendDataFunction;
    private Blob latestBlob;
    private long sequenceNumber = 0;

    private final AtomicReference<UpdateWithTimestamp> updateReference =
            new AtomicReference<>(UpdateWithTimestamp.withoutUpdate());

    @Inject
    public BasePublisher(FaceManager faceManager,
                         @BackendMetrics MetricRegistry metrics,
                         @Named("base.publisher.queue.process.per.sec") Value<Long> queueProcessPerSec,
                         @Named("base.publisher.queue.process.multithread") Value<Boolean> queueProcessMultithread,
                         @Assisted Name listenName,
                         @Assisted Function<Interest, SequenceNumberedName> interestToSequenceNumberedName,
                         @Assisted Value<Double> freshnessPeriod) {
        this.interestMeterDelayedSupplier = () -> metrics.meter(MetricNames.basePublisherInterestRate(listenName));
        this.dataSendTimer = metrics.timer(MetricNames.basePublisherQueueTimer(listenName));
        this.percentageGauge = metrics.register(MetricNames.basePublisherUpdatePercentage(listenName), PercentageGauge.getInstance());

        this.interestTFunction = interestToSequenceNumberedName;
        this.freshnessPeriod = freshnessPeriod;

        LOG.debug("Registering {}", listenName);
        faceManager.registerBasicPrefix(listenName, this);

        if (queueProcessMultithread.get()) {
            ThreadFactory builder = new ThreadFactoryBuilder()
                    .setNameFormat("bp-data-sender-" + listenName.toUri() + "-%d")
                    .build();
            ExecutorService executor = Executors.newCachedThreadPool(builder);
            sendDataFunction = (ds, ts) -> executor.submit(() -> doSendData(ds, ts));
        } else {
            sendDataFunction = this::doSendData;
        }

        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
                .setNameFormat("bp-queue-proc-" + listenName.toUri() + "-%d")
                .build();
        Executors.newSingleThreadScheduledExecutor(namedThreadFactory).scheduleAtFixedRate(
                this::processQueue,
                0,
                MICRO_SECONDS_PER_SEC / queueProcessPerSec.get(),
                TimeUnit.MICROSECONDS);
    }

    /**
     * Update the blob that will be used to service interests
     *
     * @param latestBlob the new blob to serve
     */
    public long updateLatestBlob(Blob latestBlob) {
        this.latestBlob = latestBlob;
        updateReference.getAndSet(UpdateWithTimestamp.withUpdate());
        return ++sequenceNumber;
    }

    public Set<SequenceNumberedName> getOutstandingInterests() {
        return ImmutableSet.copyOf(outstandingInterests.keySet());
    }

    @Override
    public void onInterest(Name prefix, Interest interest, Face face, long interestFilterId, InterestFilter filter) {
        SequenceNumberedName interestName = interestTFunction.apply(interest);
        outstandingInterests.put(interestName, face);
        // Fairly gross, but don't want to start meter until we get first interest
        if (interestMeter == null) {
            interestMeter = interestMeterDelayedSupplier.get();
        }
        interestMeter.mark();
    }

    private void processQueue() {

        // Consume the update by setting hasUpdate to false (regardless of its previous value)
        // while getting the previous value
        UpdateWithTimestamp previousValue = updateReference.getAndSet(UpdateWithTimestamp.withoutUpdate());

        if (!previousValue.hasUpdate) {
            percentageGauge.miss();
            return;
        } else {
            percentageGauge.hit();
        }

        long updateTimestamp = previousValue.timestamp;

        // Send any interests with sequenceNumber <= currentSequenceNumber
        for (Iterator<Map.Entry<SequenceNumberedName, Face>> i = outstandingInterests.entrySet().iterator(); i.hasNext(); ) {
            Map.Entry<SequenceNumberedName, Face> entry = i.next();
            SequenceNumberedName sequenceNumberedName = entry.getKey();
            Face face = entry.getValue();

            if (sequenceNumberedName.getLatestSequenceNumberSeen() <= sequenceNumber) {
                sendDataFunction.accept(new DataSend(face, sequenceNumberedName, latestBlob), updateTimestamp);
                i.remove();
            } else {
                LOG.debug("Had Update but {} already had sn {}", sequenceNumberedName.getFullName(), sequenceNumber);
            }
        }
    }

    private void doSendData(DataSend dataSend, long updateTimestamp) {
        Timer.Context context = dataSendTimer.time();
        SequenceNumberedName name = dataSend.getName();
        if (name.getLatestSequenceNumberSeen() >= sequenceNumber) {
            LOG.error("Producing name with next sequence number that will be less than or equal to current name: was {}, new {}", name.getLatestSequenceNumberSeen(), sequenceNumber);
            // TODO: I have no idea why this happens but its very rare and leads to all sorts of problems
            name.setNextSequenceNumber(sequenceNumber+10);
        } else {
            name.setNextSequenceNumber(sequenceNumber);
        }
        name.setUpdateTimestamp(updateTimestamp);
        Data data = new Data(name.getFullName()).setContent(latestBlob);
        data.getMetaInfo().setFreshnessPeriod(freshnessPeriod.get());
        try {
            dataSend.getFace().putData(data);
        } catch (Exception e) {
            LOG.error("Unable to send data to satisfy interest " + name.getFullName(), e);
        }
        context.stop();
    }

    private static final class UpdateWithTimestamp {
        private boolean hasUpdate;
        private long timestamp;

        private UpdateWithTimestamp(boolean hasUpdate, long timestamp) {
            this.hasUpdate = hasUpdate;
            this.timestamp = timestamp;
        }

        static UpdateWithTimestamp withUpdate() {
            return new UpdateWithTimestamp(true, System.currentTimeMillis());
        }

        static UpdateWithTimestamp withoutUpdate() {
            return new UpdateWithTimestamp(false, -1);
        }

        @Override
        public String toString() {
            return "UpdateWithTimestamp{" +
                    "hasUpdate=" + hasUpdate +
                    ", timestamp=" + timestamp +
                    '}';
        }
    }
}
