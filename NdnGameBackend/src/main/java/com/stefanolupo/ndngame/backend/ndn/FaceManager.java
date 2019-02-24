package com.stefanolupo.ndngame.backend.ndn;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.named_data.jndn.*;
import net.named_data.jndn.security.KeyChain;
import net.named_data.jndn.security.SecurityException;
import net.named_data.jndn.transport.AsyncTcpTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Singleton
public class FaceManager {

    private static final Logger LOG = LoggerFactory.getLogger(FaceManager.class);
    private static final int THREAD_POOL_SIZE = 20;

    private static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newScheduledThreadPool(THREAD_POOL_SIZE);
    private static final ThreadPoolFace THREAD_POOL_FACE = new ThreadPoolFace(
            EXECUTOR_SERVICE,
            new AsyncTcpTransport(EXECUTOR_SERVICE),
            new AsyncTcpTransport.ConnectionInfo("localhost"));
    @Inject
    public FaceManager(KeyChain keyChain) {
        try {
            Name certificateName = keyChain.getDefaultCertificateName();
            THREAD_POOL_FACE.setCommandSigningInfo(keyChain, certificateName);
        } catch ( Exception e) {
            throw new RuntimeException("Unable to initialize FaceManager", e);
        }
    }

    public void registerBasicPrefix(Name prefix, OnInterestCallback onInterestCallback) {
        registerBasicPrefix(prefix, onInterestCallback, FaceManager::registerPrefixFailure);
    }

    public void registerBasicPrefix(Name prefix, OnInterestCallback onInterestCallback, OnRegisterFailed onRegisterFailed) {
        try {
            THREAD_POOL_FACE.registerPrefix(prefix, onInterestCallback, onRegisterFailed);
        } catch (IOException | SecurityException e) {
            throw new RuntimeException(String.format("Unable to obtain a face from NFD for: %s", prefix.toUri()));
        }
    }

    public void expressInterestSafe(Interest interest) {
        expressInterestSafe(interest, FaceManager::onData, FaceManager::onTimeout);
    }

    public void expressInterestSafe(Interest interest, OnData onData) {
        expressInterestSafe(interest, onData, FaceManager::onTimeout);
    }

    public void expressInterestSafe(Interest interest, OnData onData, OnTimeout onTimeout) {
        try {
            THREAD_POOL_FACE.expressInterest(interest, onData, onTimeout);
        } catch (IOException e) {
            LOG.error("Unable to express interest for {}", interest.toUri());
        }
    }

    private static void registerPrefixFailure(Name prefix) {
        throw new RuntimeException("Unable to register prefix: " + prefix);
    }

    private static void onTimeout(Interest interest) {
        LOG.info("Timeout for {}", interest.toUri());
    }

    private static void onData(Interest interest, Data data) {
        LOG.info("Got data {} for interest {}", interest.toUri(), data.getName().toUri());
    }
}
