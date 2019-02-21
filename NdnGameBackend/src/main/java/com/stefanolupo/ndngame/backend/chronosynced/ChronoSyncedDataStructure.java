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
import java.util.Collection;
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
    private static final Long DEFAULT_FACE_POLL_TIME_MS = 10L;
    private static final Long DEFAULT_FACE_POLL_INITIAL_WAIT_MS = 5000L;
    private static final Long DEFAULT_SYNC_LIFETIME_MS = 1000L;

    private final ChronoSync2013 chronoSync;
    private final Face face;
    private final KeyChain keyChain;
    private final Name certificateName;

    private final long session;
    private final Name broadcastPrefix;
    private final Name dataListenPrefix;
    private final Statistics statistics;

    public ChronoSyncedDataStructure(Name broadcastPrefix,
                                     Name dataListenPrefix) {
        this.broadcastPrefix = broadcastPrefix;
        this.dataListenPrefix = dataListenPrefix;
        session = System.currentTimeMillis() / 1000;
        statistics = new Statistics();

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
                    DEFAULT_SYNC_LIFETIME_MS,
                    this::registerPrefixFailure
            );

            face.registerPrefix(dataListenPrefix, this, this::registerPrefixFailure);
            Executors.newSingleThreadScheduledExecutor()
                    .scheduleAtFixedRate(this::pollFace,
                            DEFAULT_FACE_POLL_INITIAL_WAIT_MS,
                            DEFAULT_FACE_POLL_TIME_MS,
                            TimeUnit.MILLISECONDS);
        } catch (SecurityException | KeyChain.Error | PibImpl.Error | IOException e) {
            String errorMessage = String.format("Could not initialize chrono synced map (Broadcast: %s, Listen: %s)",
                    broadcastPrefix, dataListenPrefix);
            throw new RuntimeException(errorMessage, e);
        }
    }

    protected abstract Optional<Blob> localToBlob(Interest interest);

    protected abstract Collection<Interest> syncStatesToInterests(List<ChronoSync2013.SyncState> syncStates, boolean isRecovery);

    @Override
    public void onInitialized() {
        LOG.info("Initialized ChronoSyncedMap for {}", dataListenPrefix.toUri());
    }

    @Override
    public void onInterest(Name prefix, Interest interest, Face face, long interestFilterId, InterestFilter filter) {
        Optional<Blob> maybeBlob = localToBlob(interest);
        if (!maybeBlob.isPresent()) {
            return;
        }

        MetaInfo metaInfo = new MetaInfo();
        metaInfo.setFreshnessPeriod(100);
        Data data = new Data(interest.getName())
                .setContent(maybeBlob.get())
                .setMetaInfo(metaInfo);
        try {
            keyChain.sign(data, certificateName);
            face.putData(data);
        } catch (Exception e) {
            throw new RuntimeException("Unable to send data to satisfy interest " + interest.toUri(), e);
        }
    }

    @Override
    public void onTimeout(Interest interest) {
        LOG.error("Timeout for interest: {}", interest.toUri());
    }

    @Override
    public void onReceivedSyncState(List syncStates, boolean isRecovery) {
        statistics.numSyncs ++;
        statistics.totalNumSyncStates += syncStates.size();
        if (isRecovery) {
            statistics.numRecoveries ++;
        }

        /**
         * This is totally safe - jNDN only uses generic Lists to support older JDKs
         * Casting here handles a necessary cast that would otherwise need to be done by the client
         */
        @SuppressWarnings("unchecked")
        List<ChronoSync2013.SyncState> castedSyncStates = (List<ChronoSync2013.SyncState>) syncStates;

        Collection<Interest> interests = syncStatesToInterests(castedSyncStates, isRecovery);
        interests.forEach(this::expressInterestSafe);
    }

    private void expressInterestSafe(Interest i) {
        try {
            face.expressInterest(i, this, this);
        } catch (IOException e) {
            LOG.error("Unable to express interest for {}", i.toUri(), e);
        }
    }


    protected void publishUpdate() {
        publishUpdate(null);
    }

    protected void publishUpdate(byte[] content) {
        try {
            if (content != null) {
                chronoSync.publishNextSequenceNo(new Blob(content));
            } else {
                chronoSync.publishNextSequenceNo();
            }
        } catch (IOException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    protected void registerPrefixFailure(Name prefix) {
        throw new RuntimeException("Unable to register prefx: " + prefix);
    }

    private void pollFace() {
        try {
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

    private final class Statistics {
        long numSyncs = 0;
        long numRecoveries = 0;
        long totalNumSyncStates = 0;

        Statistics() {
            //Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(this::printStats, 7L, 15L, TimeUnit.SECONDS);
        }

        void printStats() {
            LOG.debug("{} sync updates ({}% recovery) - average num sync states = {}",
                    numSyncs,
                    (numRecoveries + 0.0) / numSyncs,
                    (totalNumSyncStates + 0.0) / numSyncs
            );
        }
    }
}
