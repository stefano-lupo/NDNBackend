package com.stefanolupo.ndngame.backend.publisher;

import com.stefanolupo.ndngame.names.BaseName;
import com.stefanolupo.ndngame.names.HasNameWithSequenceNumber;
import com.stefanolupo.ndngame.names.HasSequenceNumber;
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

public class BasePublisher <T extends BaseName & HasSequenceNumber & HasNameWithSequenceNumber> implements OnInterestCallback {

    private static final Logger LOG = LoggerFactory.getLogger(BasePublisher.class);
    private static final Long DEFAULT_FACE_POLL_TIME_MS = 10L;
    private static final Long DEFAULT_FACE_POLL_INITIAL_WAIT_MS = 1000L;
    private static final Long DEFAULT_QUEUE_PROCESS_TIME_MS = 50L;
    private static final Long DEFAULT_QUEUE_PROCESS_INITIAL_WAIT_MS = 1000L;
    private static final Double FRESHNESS_PERIOD_MS = 20.0;

    private final List<T> outstandingInterests = new ArrayList<>();

    private final Name syncName;
    private final Function<Interest, T> interestTFunction;

    private Blob latestBlob;
    private AtomicBoolean hasUpdate = new AtomicBoolean(false);
    private long sequenceNumber = 0;

    private final Face face;
    private final KeyChain keyChain;
    private final Name certificateName;

    public BasePublisher(Name syncName, Function<Interest, T> interestTFunction) {
        this.syncName = syncName;
        this.interestTFunction = interestTFunction;

        try {
            keyChain = new KeyChain();
            certificateName = keyChain.getDefaultCertificateName();
//            this.face = new Face(new UdpTransport(), new UdpTransport.ConnectionInfo("localhost", 6363));
            this.face = new Face();
            face.setCommandSigningInfo(keyChain, certificateName);

            face.registerPrefix(syncName, this, this::registerPrefixFailure);
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
                DEFAULT_FACE_POLL_INITIAL_WAIT_MS,
                DEFAULT_FACE_POLL_TIME_MS,
                TimeUnit.MILLISECONDS);
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
        outstandingInterests.add(interestTFunction.apply(interest));
    }

    // TODO: Maybe multithread this?
    // TODO: Only send updates when there is one
    // TODO: Not sure about concurrent modifications here but think its okay
    private void processQueue() {
        // If had an update, consume the update
        if (hasUpdate.compareAndSet(true, false)) {
            sequenceNumber++;
        }

        // Get all interests with sequence number < current sequence number
        for (Iterator<T> i = outstandingInterests.iterator(); i.hasNext();) {
            T t = i.next();
            if (t.getSequenceNumber() <= sequenceNumber) {
                sendData(t.getNameWithSequenceNumber());
                i.remove();
            }
        }
    }

    private void sendData(Name name) {
        Data data = new Data(name).setContent(latestBlob);
        data.getMetaInfo().setFreshnessPeriod(FRESHNESS_PERIOD_MS);
        try {
            keyChain.sign(data, certificateName);
            face.putData(data);
        } catch (Exception e) {
            LOG.error("Unable to send data to satisfy interest " + name.toUri(), e);
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
