package com.stefanolupo.ndngame.backend.chronosynced;

import com.stefanolupo.ndngame.names.PlayerStatusName;
import net.named_data.jndn.*;
import net.named_data.jndn.security.KeyChain;
import net.named_data.jndn.security.SecurityException;
import net.named_data.jndn.security.pib.PibImpl;
import net.named_data.jndn.sync.ChronoSync2013;
import net.named_data.jndn.util.Blob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * A map implementation backed by ChronoSync
 * @param <K> the key to the map (e.g. the corresponding Name to a piece of data)
 * @param <V> the value of the map (the piece of data)
 */
public abstract class ChronoSyncedMap<K, V> implements
        ChronoSync2013.OnInitialized,
        ChronoSync2013.OnReceivedSyncState,
        OnData,
        OnInterestCallback,
        OnTimeout
{

    private static final Logger LOG = LoggerFactory.getLogger(ChronoSyncedMap.class);
    private static final Long DEFAULT_FACE_POLL_TIME = 5L;

    private final Map<K, V> map;
    private final ChronoSync2013 chronoSync;
    private final Face face;
    private final KeyChain keyChain;
    private final Name certificateName;
    private final long session;

    private final Name broadcastPrefix;
    private final Name dataListenPrefix;

    public ChronoSyncedMap(Name broadcastPrefix,
                           Name dataListenPrefix) {
        this.broadcastPrefix = broadcastPrefix;
        this.dataListenPrefix = dataListenPrefix;

        map = new HashMap<>();
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

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(this::pollFace, 1000, DEFAULT_FACE_POLL_TIME, TimeUnit.MILLISECONDS);
        } catch (SecurityException | KeyChain.Error | PibImpl.Error | IOException e) {
            String errorMessage = String.format("Could not initialize chrono synced map (Broadcast: %s, Listen: %s",
                    broadcastPrefix, dataListenPrefix);
            throw new RuntimeException(errorMessage, e);
        }
    }

    protected abstract K interestToKey(Interest interest);
    protected abstract V dataToVal(Data data, K key, V oldVal);
    protected abstract Blob localToBlob(Interest interest);

    @Override
    public void onData(Interest interest, Data data) {
        K key = interestToKey(interest);
        V oldVal = map.get(key);
        oldVal = dataToVal(data, key, oldVal);
        map.put(key, oldVal);
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
    public void onInitialized() {
        LOG.debug("Initialized ChronoSyncedMap for %s", dataListenPrefix.toUri());
    }

    @Override
    public void onTimeout(Interest interest) {
        LOG.debug("Timeout for interest: %s", interest.toUri());
    }

    @Override
    public void onReceivedSyncState(List syncStates, boolean isRecovery) {
        LOG.info("Recieved sync statE");
        ChronoSync2013.SyncState state = (ChronoSync2013.SyncState) syncStates.get(syncStates.size() - 1);
        Name interestName = new PlayerStatusName(state).getExpressInterestName();

        try {
            face.expressInterest(interestName, this, this);
        } catch (IOException e) {
            LOG.error("Unable to express interest for %s", interestName, e);
        }
    }

    public void publishUpdate() {
        try {
            chronoSync.publishNextSequenceNo();
        } catch (IOException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<K, V> getMap() {
        return Collections.unmodifiableMap(map);
    }

    // TODO: Handle this better
    private void registerPrefixFailure(Name prefix) {
        throw new RuntimeException("Unable to register prefx: " + prefix);
    }

    private void pollFace() {
        while (true) {
            try {
                LOG.info("polling face");
                face.processEvents();
                Thread.sleep(5);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
