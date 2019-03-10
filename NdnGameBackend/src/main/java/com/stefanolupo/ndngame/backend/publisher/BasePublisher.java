package com.stefanolupo.ndngame.backend.publisher;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;
import com.hubspot.liveconfig.value.Value;
import com.stefanolupo.ndngame.backend.annotations.BackendMetrics;
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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class BasePublisher implements OnInterestCallback {

    private static final Logger LOG = LoggerFactory.getLogger(BasePublisher.class);

    // Injected
    private final MetricRegistry metrics;
    private final Timer dataSendTimer;

    // Assisted
    private final Function<Interest, SequenceNumberedName> interestTFunction;
    private final Value<Double> freshnessPeriod;

    // Class logic
    private final ConcurrentMap<SequenceNumberedName, Face> outstandingInterests = new ConcurrentHashMap<>();
    private final BiConsumer<SequenceNumberedName, Face> sendDataFunction;
    private Blob latestBlob;
    private AtomicBoolean hasUpdate = new AtomicBoolean(false);
    private long sequenceNumber = 0;

    @Inject
    public BasePublisher(FaceManager faceManager,
                         @BackendMetrics MetricRegistry metrics,
                         @Named("base.publisher.queue.process.per.sec") Value<Long> queueProcessPerSec,
                         @Named("base.publisher.queue.process.multithread") Value<Boolean> queueProcessMultithread,
                         @Assisted Name listenName,
                         @Assisted Function<Interest, SequenceNumberedName> interestToSequenceNumberedName,
                         @Assisted Value<Double> freshnessPeriod) {
        this.metrics = metrics;
        this.dataSendTimer = metrics.timer(MetricNames.basePublisherQueueTimer(listenName));
        this.interestTFunction = interestToSequenceNumberedName;
        this.freshnessPeriod = freshnessPeriod;

        this.metrics.register(MetricNames.basePublisherQueueSize(listenName),
                (Gauge<Integer>) outstandingInterests::size);

        LOG.debug("Registering {}", listenName);
        faceManager.registerBasicPrefix(listenName, this);

        if (queueProcessMultithread.get()) {
            ThreadFactory builder = new ThreadFactoryBuilder()
                    .setNameFormat("bp-data-sender-" + listenName.toUri() + "-%d")
                    .build();
            ExecutorService executor = Executors.newCachedThreadPool(builder);
            sendDataFunction = (n, f) -> executor.submit(() -> doSendData(n, f));
        } else {
            sendDataFunction = this::doSendData;
        }

        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
                .setNameFormat("bp-queue-proc-" + listenName.toUri() + "-%d")
                .build();
        Executors.newSingleThreadScheduledExecutor(namedThreadFactory).scheduleAtFixedRate(
                this::processQueue,
                0,
                (1000L) / queueProcessPerSec.get(),
                TimeUnit.MILLISECONDS);
    }

    /**
     * Update the blob that will be used to service interests
     * @param latestBlob the new blob to serve
     */
    public long updateLatestBlob(Blob latestBlob) {
        this.latestBlob = latestBlob;
        hasUpdate.set(true);
        return ++sequenceNumber;
    }

    public Set<SequenceNumberedName> getOutstandingInterests() {
        return ImmutableSet.copyOf(outstandingInterests.keySet());
    }

    @Override
    public void onInterest(Name prefix, Interest interest, Face face, long interestFilterId, InterestFilter filter) {
        SequenceNumberedName interestName = interestTFunction.apply(interest);
        outstandingInterests.put(interestName, face);
    }

    private void processQueue() {

        // Set the flag to false IFF it is currently true
        // Returns true if the operation succeeds (i.e. if the flag was true and its now false)
        if (!hasUpdate.compareAndSet(true, false)) {
            return;
        }

        // Send any interests with sequenceNumber <= currentSequenceNumber
        for (Iterator<Map.Entry<SequenceNumberedName, Face>> i = outstandingInterests.entrySet().iterator(); i.hasNext();) {
            Map.Entry<SequenceNumberedName, Face> entry = i.next();
            SequenceNumberedName sequenceNumberedName = entry.getKey();
            Face face = entry.getValue();

            if (sequenceNumberedName.getLatestSequenceNumberSeen() <= sequenceNumber) {
                sendDataFunction.accept(sequenceNumberedName, face);
                i.remove();
            } else {
                LOG.debug("Had Update but {} already had sn {}", sequenceNumberedName.getFullName(), sequenceNumber);
            }
        }
    }

    private void doSendData(SequenceNumberedName name, Face face) {
        Timer.Context context = dataSendTimer.time();
        name.setNextSequenceNumber(sequenceNumber);
        Data data = new Data(name.getFullName()).setContent(latestBlob);
        data.getMetaInfo().setFreshnessPeriod(freshnessPeriod.get());
        try {
            face.putData(data);
        } catch (Exception e) {
            LOG.error("Unable to send data to satisfy interest " + name.getFullName(), e);
        }
        context.stop();
    }
}
