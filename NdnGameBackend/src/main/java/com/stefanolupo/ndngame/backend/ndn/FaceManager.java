package com.stefanolupo.ndngame.backend.ndn;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.named_data.jndn.Face;
import net.named_data.jndn.Name;
import net.named_data.jndn.OnInterestCallback;
import net.named_data.jndn.OnRegisterFailed;
import net.named_data.jndn.encoding.EncodingException;
import net.named_data.jndn.security.KeyChain;
import net.named_data.jndn.security.SecurityException;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Singleton
public class FaceManager {

    private static final Long DEFAULT_FACE_POLL_TIME_MS = 10L;
    private static final Long DEFAULT_FACE_POLL_INITIAL_WAIT_MS = 1000L;

    private final KeyChain keyChain;
    private final Name certificateName;

    @Inject
    public FaceManager(KeyChain keyChain) {
        try {
            this.keyChain = keyChain;
            certificateName = keyChain.getDefaultCertificateName();
        } catch (SecurityException e) {
            throw new RuntimeException(e);
        }


    }

    public Face getBasicFace(Name prefix, OnInterestCallback onInterestCallback) {
        return getBasicFace(prefix, onInterestCallback, FaceManager::registerPrefixFailure);
    }

    // TODO: Something smarter
    public Face getBasicFace(Name prefix, OnInterestCallback onInterestCallback, OnRegisterFailed onRegisterFailed) {
        Face face = new Face();
        face.setCommandSigningInfo(keyChain, certificateName);
        try {
            face.registerPrefix(prefix, onInterestCallback, onRegisterFailed);
        } catch (IOException | SecurityException e) {
            throw new RuntimeException(String.format("Unable to obtain a face from NFD for: %s", prefix.toUri()));
        }

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(
                () -> pollFace(face),
                DEFAULT_FACE_POLL_INITIAL_WAIT_MS,
                DEFAULT_FACE_POLL_TIME_MS,
                TimeUnit.MILLISECONDS);
        return face;
    }

    private void pollFace(Face face) {
        try {
            face.processEvents();
        } catch (IOException | EncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private static void registerPrefixFailure(Name prefix) {
        throw new RuntimeException("Unable to register prefix: " + prefix);
    }

}
