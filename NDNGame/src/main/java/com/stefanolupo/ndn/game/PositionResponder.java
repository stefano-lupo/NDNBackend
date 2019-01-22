package com.stefanolupo.ndn.game;

import com.stefanolupo.ndn.NDNGameProtos;
import net.named_data.jndn.*;
import net.named_data.jndn.security.KeyChain;
import net.named_data.jndn.util.Blob;

import java.util.concurrent.ThreadLocalRandom;

public class PositionResponder implements OnInterestCallback {

    private final String prefix = "ndn:/com/stefanolupo/ndngame/player-position";

    private final KeyChain keyChain;
    private final Face face;
    private final Name certificateName;
    private final String playerName;


    public PositionResponder(String playerName) throws Exception {
        keyChain = new KeyChain();
        certificateName = keyChain.getDefaultCertificateName();
        face = new Face();
        this.playerName = playerName;

        Name prefix = new Name(String.format("%s/%s", this.prefix, this.playerName));
        face.setCommandSigningInfo(keyChain, certificateName);
        face.registerPrefix(prefix, this, this::handleFailureToRegisterName);
    }

    void processEvents() throws Exception {
        while (true) {
            face.processEvents();
            Thread.sleep(100);
        }
    }

    @Override
    public void onInterest(Name name, Interest interest, Face face, long l, InterestFilter interestFilter) {
        System.out.println("Got interest for: " + name.toUri() + " - " + interest.getName().toUri());

        Name dataName = new Name(interest.getName());
        NDNGameProtos.Position position = NDNGameProtos.Position.newBuilder()
                .setX(ThreadLocalRandom.current().nextInt(0, 100))
                .setY(ThreadLocalRandom.current().nextInt(0, 100)).build();
        Data data = new Data(dataName).setContent(new Blob(position.toByteArray()));

        try {
            keyChain.sign(data, certificateName);
            face.send(data.getDefaultWireEncoding().buf());
            System.out.println("Sent: " + data.getName().toUri() + "\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleFailureToRegisterName(Name failedPrefix) {
        System.err.println("Failed to register prefix " + failedPrefix.toUri());
    }
}
