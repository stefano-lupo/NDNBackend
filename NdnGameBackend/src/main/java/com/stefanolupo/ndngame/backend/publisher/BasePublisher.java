package com.stefanolupo.ndngame.backend.publisher;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;
import com.hubspot.liveconfig.value.Value;
import com.stefanolupo.ndngame.backend.ndn.FaceManager;
import com.stefanolupo.ndngame.names.SequenceNumberedName;
import net.named_data.jndn.*;
import net.named_data.jndn.util.Blob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

public class BasePublisher implements OnInterestCallback {

    private static final Logger LOG = LoggerFactory.getLogger(BasePublisher.class);

    private final Map<SequenceNumberedName, Face> outstandingInterests = new HashMap<>();
    private long totalNumInterests = 0;

    private final Function<Interest, SequenceNumberedName> interestTFunction;
    private final Value<Double> freshnessPeriod;

    private Blob latestBlob;
    private AtomicBoolean hasUpdate = new AtomicBoolean(false);
    private long sequenceNumber = 0;


    @Inject
    public BasePublisher(FaceManager faceManager,
                         @Named("base.publisher.queue.process.time.ms") Value<Long> processTimeMs,
                         @Assisted Name listenName,
                         @Assisted Function<Interest, SequenceNumberedName> interestTFunction,
                         @Assisted Value<Double> freshnessPeriod) {
        this.interestTFunction = interestTFunction;
        this.freshnessPeriod = freshnessPeriod;

        LOG.debug("Registering {}", listenName);
        faceManager.registerBasicPrefix(listenName, this);

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(
                this::processQueue,
                0,
                processTimeMs.get(),
                TimeUnit.MILLISECONDS);
//        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(
//                () -> LOG.info("Seen {} interests, {} outstanding", totalNumInterests, outstandingInterests.size()),
//                10,
//                60,
//                TimeUnit.SECONDS
//        );
    }

    /**
     * Update the blob that will be used to service interests
     * @param latestBlob the new blob to serve
     */
    public void updateLatestBlob(Blob latestBlob) {
        this.latestBlob = latestBlob;
        hasUpdate.set(true);
    }

    @Override
    public void onInterest(Name prefix, Interest interest, Face face, long interestFilterId, InterestFilter filter) {
        SequenceNumberedName interestName = interestTFunction.apply(interest);
        totalNumInterests++;
        outstandingInterests.put(interestName, face);
    }

    // TODO: Maybe multithread this?
    // TODO: Only send updates when there is one
    // TODO: Not sure about concurrent modifications here but think its okay
    private void processQueue() {

        if (hasUpdate.compareAndSet(true, false)) {
            sequenceNumber++;
        } else {
            return;
        }

        // Send any interests with sequenceNumber <= currentSequenceNumber
        for (Iterator<Map.Entry<SequenceNumberedName, Face>> i = outstandingInterests.entrySet().iterator(); i.hasNext();) {
            Map.Entry<SequenceNumberedName, Face> entry = i.next();
            SequenceNumberedName sequenceNumberedName = entry.getKey();
            Face face = entry.getValue();

            if (sequenceNumberedName.getLatestSequenceNumberSeen() <= sequenceNumber) {
                sendData(sequenceNumberedName, face);
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
