package com.stefanolupo.ndngame.backend.ndn;

import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.hubspot.liveconfig.value.Value;
import net.named_data.jndn.*;
import net.named_data.jndn.security.KeyChain;
import net.named_data.jndn.security.SecurityException;
import net.named_data.jndn.transport.AsyncTcpTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Singleton
public class FaceManager {

    private static final Logger LOG = LoggerFactory.getLogger(FaceManager.class);

    private final Value<Integer> numFaces;
    private final Value<Integer> threadsPerFace;
    private final Iterator<ThreadPoolFace> iterator;

    private final KeyChain keyChain;

    @Inject
    public FaceManager(KeyChain keyChain,
                       @Named("facemanager.max.num.faces") Value<Integer> numFaces,
                       @Named("facemanager.num.threads.per.face") Value<Integer> threadsPerFace) {

        this.keyChain = keyChain;
        this.numFaces = numFaces;
        this.threadsPerFace = threadsPerFace;

        Set<ThreadPoolFace> faces = buildFaces();
        iterator = Iterables.cycle(faces).iterator();
    }

    public void registerBasicPrefix(Name prefix, OnInterestCallback onInterestCallback) {
        ThreadPoolFace face = getNextFace();
        RegisterPrefixAttempt prefixAttempt =
                new RegisterPrefixAttempt(face, prefix, onInterestCallback);
        doRegisterPrefix(face, prefixAttempt);
    }

    public void expressInterestSafe(Interest interest) {
        expressInterestSafe(interest, FaceManager::onData, FaceManager::onTimeout);
    }

    public void expressInterestSafe(Interest interest, OnData onData, OnTimeout onTimeout) {
        try {
            getNextFace().expressInterest(interest, onData, onTimeout);
        } catch (IOException e) {
            LOG.error("Unable to express interest for {}", interest.toUri());
        }
    }

    private void doRegisterPrefix(ThreadPoolFace face, RegisterPrefixAttempt attempt) {
        try {
            face.registerPrefix(attempt.prefix, attempt.onInterestCallback, attempt);
        } catch (IOException | SecurityException e) {
            throw new RuntimeException(String.format("Unable to obtain a faces from NFD for: %s", attempt.prefix.toUri()));
        }
    }

    private ThreadPoolFace getNextFace() {
        return iterator.next();
    }

    private Set<ThreadPoolFace> buildFaces() {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(threadsPerFace.get());
        Name certificateName;
        try {
            certificateName = keyChain.getDefaultCertificateName();
        } catch (Exception e) {
            throw new RuntimeException("Unable to initialize FaceManager", e);
        }

        Set<ThreadPoolFace> faces = new LinkedHashSet<>();

        for (int i = 0; i < numFaces.get(); i++) {
            ThreadPoolFace face = new ThreadPoolFace(
                    executorService,
                    new AsyncTcpTransport(executorService),
                    new AsyncTcpTransport.ConnectionInfo("localhost")
            );
            face.setCommandSigningInfo(keyChain, certificateName);
            faces.add(face);
        }

        return faces;
    }

    private static void onTimeout(Interest interest) {
        LOG.info("Timeout for {}", interest.toUri());
    }

    private static void onData(Interest interest, Data data) {
        LOG.info("Got data {} for interest {}", interest.toUri(), data.getName().toUri());
    }

    class RegisterPrefixAttempt implements OnRegisterFailed {
        private final Logger LOG = LoggerFactory.getLogger(RegisterPrefixAttempt.class);

        final ThreadPoolFace face;
        final Name prefix;
        final OnInterestCallback onInterestCallback;

        RegisterPrefixAttempt(ThreadPoolFace face,
                              Name prefix,
                              OnInterestCallback onInterestCallback) {
            this.face = face;
            this.prefix = prefix;
            this.onInterestCallback = onInterestCallback;
        }

        @Override
        public void onRegisterFailed(Name prefix) {
            LOG.error("Failed to register prefix: {}, retrying..", prefix);
            FaceManager.this.doRegisterPrefix(face, this);
        }
    }
}
