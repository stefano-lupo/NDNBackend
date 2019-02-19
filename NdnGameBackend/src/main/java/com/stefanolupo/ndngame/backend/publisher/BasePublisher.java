package com.stefanolupo.ndngame.backend.publisher;

import net.named_data.jndn.*;
import net.named_data.jndn.encoding.EncodingException;
import net.named_data.jndn.security.KeyChain;
import net.named_data.jndn.security.SecurityException;
import net.named_data.jndn.security.pib.PibImpl;
import net.named_data.jndn.util.Blob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public abstract class BasePublisher<T> implements OnInterestCallback {

    private static final Logger LOG = LoggerFactory.getLogger(BasePublisher.class);
    private static final Long DEFAULT_FACE_POLL_TIME_MS = 10L;
    private static final Long DEFAULT_FACE_POLL_INITIAL_WAIT_MS = 5000L;
    private static final Double FRESHNESS_PERIOD_MS = 20.0;

    private final BlockingQueue<Interest> outstandingInterests = new LinkedBlockingQueue<>();

    private final Name syncName;
    private T entity;

    private final Face face;
    private final KeyChain keyChain;
    private final Name certificateName;

    public BasePublisher(Name syncName,
                         T entity) {
        this.syncName = syncName;
        this.entity = entity;

        try {
            keyChain = new KeyChain();
            certificateName = keyChain.getDefaultCertificateName();
//            this.face = new Face(new UdpTransport(), new UdpTransport.ConnectionInfo("localhost", 6363));
            this.face = new Face();
            face.setCommandSigningInfo(keyChain, certificateName);

            face.registerPrefix(syncName, this, this::registerPrefixFailure);
            Executors.newSingleThreadScheduledExecutor()
                    .scheduleAtFixedRate(this::pollFace,
                            DEFAULT_FACE_POLL_INITIAL_WAIT_MS,
                            DEFAULT_FACE_POLL_TIME_MS,
                            TimeUnit.MILLISECONDS);
        } catch (SecurityException | KeyChain.Error | PibImpl.Error | IOException e) {
            String errorMessage = String.format("Could not initialize Producer (Prefix: %s)", syncName);
            throw new RuntimeException(errorMessage, e);
        }
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(this::pollFace, 500, 10, TimeUnit.MILLISECONDS);
        Executors.newSingleThreadExecutor().submit(this::pollQueue);
    }

    protected abstract byte[] entityToByteArray(T entity);

    public void updateEntity(T entity) {
        this.entity = entity;
    }

    @Override
    public void onInterest(Name prefix, Interest interest, Face face, long interestFilterId, InterestFilter filter) {
        outstandingInterests.add(interest);
    }

    // TODO: I should probably multithread this but not sure if sendData is thread safe
    private void pollQueue() {
        while (true) {
            try {
                // TODO: I need to only update if there is an update..
                Interest interest = outstandingInterests.take();
                Blob blob = new Blob(entityToByteArray(entity));
                sendData(interest, blob);
            } catch (InterruptedException e) {
                LOG.debug("Interupted, shutting down..");
                return;
            }
        }
    }

    private void sendData(Interest interest, Blob blob) {
        Data data = new Data(interest.getName()).setContent(blob);
        data.getMetaInfo().setFreshnessPeriod(FRESHNESS_PERIOD_MS);
        try {
            keyChain.sign(data, certificateName);
            face.putData(data);
        } catch (Exception e) {
            LOG.error("Unable to send data to satisfy interest " + interest.toUri(), e);
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
