package com.stefanolupo.ndngame.backend.chronosynced;

import net.named_data.jndn.*;
import net.named_data.jndn.encoding.EncodingException;
import net.named_data.jndn.security.KeyChain;
import net.named_data.jndn.security.SecurityException;
import net.named_data.jndn.security.pib.PibImpl;
import net.named_data.jndn.sync.ChronoSync2013;
import net.named_data.jndn.util.Blob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public abstract class ChronoSyncedDataStructure implements
        OnData,
        OnTimeout,
        OnInterestCallback,
        ChronoSync2013.OnInitialized,
        ChronoSync2013.OnReceivedSyncState
{

    private static final Logger LOG = LoggerFactory.getLogger(ChronoSyncedDataStructure.class);
    private static final Long DEFAULT_FACE_POLL_TIME_MS = 5L;
    private static final Long DEFAULT_FACE_POLL_INITIAL_WAIT_MS = 2000L;

    private final ChronoSync2013 chronoSync;
    private final Face face;
    private final KeyChain keyChain;
    private final Name certificateName;

    private final long session;
    private final Name broadcastPrefix;
    private final Name dataListenPrefix;

    public ChronoSyncedDataStructure(Name broadcastPrefix,
                                     Name dataListenPrefix) {
        this.broadcastPrefix = broadcastPrefix;
        this.dataListenPrefix = dataListenPrefix;
        session = System.currentTimeMillis() / 1000;

        try {
            keyChain = new KeyChain();
            certificateName = keyChain.getDefaultCertificateName();
            face = new Face();
            face.setCommandSigningInfo(keyChain, certificateName);

            chronoSync = new ChronoSync2013(
                    this,
                    this,
                    dataListenPrefix,
                    broadcastPrefix,
                    session,
                    face,
                    keyChain,
                    certificateName,
                    1000.0,
                    this::registerPrefixFailure
            );

            Executors.newSingleThreadScheduledExecutor()
                    .scheduleAtFixedRate(this::pollFace,
                            DEFAULT_FACE_POLL_INITIAL_WAIT_MS,
                            DEFAULT_FACE_POLL_TIME_MS,
                            TimeUnit.MILLISECONDS);
        } catch (SecurityException | KeyChain.Error | PibImpl.Error | IOException e) {
            String errorMessage = String.format("Could not initialize chrono synced map (Broadcast: %s, Listen: %s",
                    broadcastPrefix, dataListenPrefix);
            throw new RuntimeException(errorMessage, e);
        }
    }

    protected abstract Blob localToBlob(Interest interest);

    protected abstract Optional<Interest> syncStatesToMaybeInterest(List<ChronoSync2013.SyncState> syncStates, boolean isRecovery);

    @Override
    public void onInitialized() {
        LOG.debug("Initialized ChronoSyncedMap for %s", dataListenPrefix.toUri());
    }

    @Override
    public void onInterest(Name prefix, Interest interest, Face face, long interestFilterId, InterestFilter filter) {
        Data data = new Data(interest.getName())
                .setContent(localToBlob(interest));
        try {
            keyChain.sign(data, certificateName);
            face.send(data.wireEncode());
        } catch (Exception e) {
            throw new RuntimeException("Unable to send data to satisfy interest " + interest.toUri(), e);
        }
    }

    @Override
    public void onTimeout(Interest interest) {
        LOG.debug("Timeout for interest: %s", interest.toUri());
    }

    @Override
    public void onReceivedSyncState(List syncStates, boolean isRecovery) {

        List<ChronoSync2013.SyncState> castedSyncStates = (List<ChronoSync2013.SyncState>) syncStates;

        Optional<Interest> maybeInterest = syncStatesToMaybeInterest(castedSyncStates, isRecovery);
        if (!maybeInterest.isPresent()) {
            return;
        }

        try {
            face.expressInterest(maybeInterest.get(), this, this);
        } catch (IOException e) {
            LOG.error("Unable to express interest for %s", maybeInterest.get().toUri(), e);
        }
    }


    protected void publishUpdate() {
        try {
            chronoSync.publishNextSequenceNo();
        } catch (IOException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    protected void registerPrefixFailure(Name prefix) {
        throw new RuntimeException("Unable to register prefx: " + prefix);
    }

    private void pollFace() {
        try {
            LOG.info("polling face");
            face.processEvents();
        } catch (IOException | EncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public Face getFace() {
        return face;
    }

    public KeyChain getKeyChain() {
        return keyChain;
    }

    public Name getDataListenPrefix() {
        return dataListenPrefix;
    }
}
