package com.stefanolupo.ndngame.backend;

import com.stefanolupo.ndngame.Names;
import com.stefanolupo.ndngame.Player;
import com.stefanolupo.ndngame.exceptions.NdnException;
import net.named_data.jndn.*;
import net.named_data.jndn.encoding.EncodingException;
import net.named_data.jndn.security.KeyChain;
import net.named_data.jndn.security.SecurityException;
import net.named_data.jndn.security.pib.PibImpl;
import net.named_data.jndn.util.Blob;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class StatusResponder implements OnInterestCallback {

    private static final long FACE_POLL_TIME_MS = 50;

    private final Player player;
    private final KeyChain keyChain;
    private final Name certificateName;
    private final Face face;
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(5);

    public StatusResponder(Player player) throws NdnException {
        this.player = player;
        try {
            keyChain = new KeyChain();
            certificateName = keyChain.getDefaultCertificateName();

            face = new Face();
            face.setCommandSigningInfo(keyChain, certificateName);
            face.registerPrefix(getPrefix(), this, this::handleFailureToRegisterName);
        } catch (KeyChain.Error | PibImpl.Error | SecurityException |IOException e) {
            throw new NdnException("Unable to configure Ndn face listener", e);
        }
    }

    public void launch() {
        executorService.scheduleAtFixedRate(this::safeProcessEvents, 0, FACE_POLL_TIME_MS, TimeUnit.MILLISECONDS);
    }

    void safeProcessEvents() {
        try {
            face.processEvents();
        } catch (EncodingException | IOException e) {
            throw new RuntimeException("Unable to process events for face: " + getPrefix(), e);
        }
    }

    @Override
    public void onInterest(Name name, Interest interest, Face face, long l, InterestFilter interestFilter) {
        System.out.println("Got interest for: " + name.toUri() + " - " + interest.getName().toUri());
        Data data = new Data(name).setContent(new Blob(player.getPlayerStatus().toByteArray()));

        try {
            keyChain.sign(data, certificateName);
            face.send(data.getDefaultWireEncoding().buf());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleFailureToRegisterName(Name failedPrefix) {
        System.err.println("Failed to register prefix " + failedPrefix.toUri());
    }

    private Name getPrefix() {
        return Names.PLAYER_STATUS.getName(player.getPlayerName());
    }

    // Handy for debugging
    public static void main(String[] args) throws Exception {
        new StatusResponder(new Player("desktop")).launch();
    }
}
