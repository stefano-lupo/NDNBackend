package com.stefanolupo.ndngame.backend.publisher;

import com.stefanolupo.ndngame.names.SequenceNumberedName;
import net.named_data.jndn.*;
import net.named_data.jndn.encoding.EncodingException;
import net.named_data.jndn.security.KeyChain;
import net.named_data.jndn.security.SecurityException;
import net.named_data.jndn.security.pib.PibImpl;
import net.named_data.jndn.util.Blob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

public class BasePublisher <T extends SequenceNumberedName> implements OnInterestCallback {

    private static final Logger LOG = LoggerFactory.getLogger(BasePublisher.class);
    private static final Long DEFAULT_FACE_POLL_TIME_MS = 10L;
    private static final Long DEFAULT_FACE_POLL_INITIAL_WAIT_MS = 1000L;
    private static final Long DEFAULT_QUEUE_PROCESS_TIME_MS = 10L;
    private static final Long DEFAULT_QUEUE_PROCESS_INITIAL_WAIT_MS = 1000L;
    private static final Double FRESHNESS_PERIOD_MS = 20.0;

    private final List<T> outstandingInterests = new ArrayList<>();
    private long totalNumInterests = 0;

    private final SequenceNumberedName syncName;
    private final Function<Interest, T> interestTFunction;

    private Blob latestBlob;
    private AtomicBoolean hasUpdate = new AtomicBoolean(false);
    private long sequenceNumber = 0;

    private final Face face;
    private final KeyChain keyChain;
    private final Name certificateName;

    public BasePublisher(SequenceNumberedName syncName, Function<Interest, T> interestTFunction) {
        this.syncName = syncName;
        this.interestTFunction = interestTFunction;

        try {
            keyChain = new KeyChain();
            certificateName = keyChain.getDefaultCertificateName();
//            this.face = new Face(new UdpTransport(), new UdpTransport.ConnectionInfo("localhost", 6363));
            this.face = new Face();
            face.setCommandSigningInfo(keyChain, certificateName);
            LOG.debug("Registering {}", syncName.getListenName());
            face.registerPrefix(syncName.getListenName(), this, this::registerPrefixFailure);
        } catch (SecurityException | KeyChain.Error | PibImpl.Error | IOException e) {
            String errorMessage = String.format("Could not initialize Producer (Prefix: %s)", syncName);
            throw new RuntimeException(errorMessage, e);
        }

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(
                this::pollFace,
                DEFAULT_FACE_POLL_INITIAL_WAIT_MS,
                DEFAULT_FACE_POLL_TIME_MS,
                TimeUnit.MILLISECONDS);
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(
                this::processQueue,
                DEFAULT_QUEUE_PROCESS_INITIAL_WAIT_MS,
                DEFAULT_QUEUE_PROCESS_TIME_MS,
                TimeUnit.MILLISECONDS);
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(
                () -> LOG.info("Seen {} interests, {} outstanding", totalNumInterests, outstandingInterests.size()),
                10,
                10,
                TimeUnit.SECONDS
        );
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
        T interestName = interestTFunction.apply(interest);
        totalNumInterests++;
        outstandingInterests.add(interestName);
    }

    // TODO: Maybe multithread this?
    // TODO: Only send updates when there is one
    // TODO: Not sure about concurrent modifications here but think its okay
    private void processQueue() {
        // If had an update, consume the update


        if (hasUpdate.compareAndSet(true, false)) {
            sequenceNumber++;
//            LOG.debug("new sequence number: {}", sequenceNumber);
        } else {
            return;
        }

        // Get all interests with sequence number < current sequence number
        for (Iterator<T> i = outstandingInterests.iterator(); i.hasNext();) {
            T t = i.next();
            if (t.getLatestSequenceNumberSeen() <= sequenceNumber) {
                sendData(t);
                i.remove();
            } else {
                LOG.debug("Had Update but {} already had sn {}", t.getFullName(), sequenceNumber);
            }
        }
    }

    // TODO: Naming of return here might be important
    // If i send interest for /data/45 and the sn is actually at 65 now
        // Publisher can send back something like /data/45/65
        // If another subscriber later requests /data/45, will caches send back /data/45/65?
        // Do we even want them to? The SN might be >> than 65 now..
        // Can use freshness to control this somewhat
    // If we're always sending back newest one --> we don't need sequence numbers

    private void sendData(SequenceNumberedName name) {
//        nameWithSequenceNumber.setSequenceNumber(sequenceNumber);
//        Name name = new Name(name.getNameWithSequenceNumber()).append(String.valueOf(sequenceNumber));
        name.setNextSequenceNumber(sequenceNumber);
        Data data = new Data(name.getFullName()).setContent(latestBlob);
        data.getMetaInfo().setFreshnessPeriod(FRESHNESS_PERIOD_MS);
        try {
            keyChain.sign(data, certificateName);
            face.putData(data);
        } catch (Exception e) {
            LOG.error("Unable to send data to satisfy interest " + name.getFullName(), e);
        }
    }

    private void registerPrefixFailure(Name prefix) {
        throw new RuntimeException("Unable to register prefx: " + prefix);
    }

    private void pollFace() {
        try {
            face.processEvents();
        } catch (IOException | EncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
