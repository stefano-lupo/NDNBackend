package com.stefanolupo.ndngame.backend.publisher;

import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;
import com.hubspot.liveconfig.value.Value;
import com.stefanolupo.ndngame.backend.annotations.LogScheduleExecutor;
import com.stefanolupo.ndngame.backend.ndn.FaceManager;
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
import java.util.function.Function;

public class BasePublisher implements OnInterestCallback {

    private static final Logger LOG = LoggerFactory.getLogger(BasePublisher.class);

    private final ConcurrentMap<SequenceNumberedName, Face> outstandingInterests = new ConcurrentHashMap<>();
    private long totalNumInterests = 0;

    private final Function<Interest, SequenceNumberedName> interestTFunction;
    private final Value<Double> freshnessPeriod;

    private Blob latestBlob;
    private AtomicBoolean hasUpdate = new AtomicBoolean(false);
    private long sequenceNumber = 0;


    @Inject
    public BasePublisher(FaceManager faceManager,
                         @LogScheduleExecutor ScheduledExecutorService executorService,
                         @Named("base.publisher.queue.process.time.ms") Value<Long> processTimeMs,
                         @Assisted Name listenName,
                         @Assisted Function<Interest, SequenceNumberedName> interestToSequenceNumberedName,
                         @Assisted Value<Double> freshnessPeriod) {
        this.interestTFunction = interestToSequenceNumberedName;
        this.freshnessPeriod = freshnessPeriod;

        LOG.debug("Registering {}", listenName);
        faceManager.registerBasicPrefix(listenName, this);
        executorService.scheduleAtFixedRate(
                () -> LOG.info("Seen {} interests, {} outstanding", totalNumInterests, outstandingInterests.size()),
                10,
                60,
                TimeUnit.SECONDS
        );

        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
                .setNameFormat("bp- " + listenName.toUri() + "-%d")
                .build();
        Executors.newSingleThreadScheduledExecutor(namedThreadFactory).scheduleAtFixedRate(
                this::processQueue,
                0,
                processTimeMs.get(),
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
        totalNumInterests++;
        outstandingInterests.put(interestName, face);
    }

    // TODO: Maybe multithread this?
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
                sendData(sequenceNumberedName, face);
//                LOG.debug("Seen {}, sent: {}", sequenceNumberedName.getFullName(), sequenceNumber);
                i.remove();
            } else {
                LOG.debug("Had Update but {} already had sn {}", sequenceNumberedName.getFullName(), sequenceNumber);
            }
        }
    }

    private void sendData(SequenceNumberedName name, Face face) {
        name.setNextSequenceNumber(sequenceNumber);
        Data data = new Data(name.getFullName()).setContent(latestBlob);
        data.getMetaInfo().setFreshnessPeriod(freshnessPeriod.get());
        try {
            face.putData(data);
        } catch (Exception e) {
            LOG.error("Unable to send data to satisfy interest " + name.getFullName(), e);
        }
    }
}
