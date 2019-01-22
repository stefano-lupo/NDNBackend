package com.stefanolupo.ndn.pingpong;

import net.named_data.jndn.*;
import net.named_data.jndn.security.KeyChain;
import net.named_data.jndn.util.Blob;

public class Ponger implements OnInterestCallback {

    private final KeyChain keyChain;
    private final Face face;
    private final Name certificateName;

    private final String entityName = "ndngateway:/com/stefanolupo/desktop";

    public static void main(String[] args) throws Exception {
        Ponger ponger = new Ponger();
        ponger.processEvents();
    }

    public Ponger() throws Exception {
        keyChain = new KeyChain();
        certificateName = keyChain.getDefaultCertificateName();
        face = new Face();

        Name prefix = new Name(entityName);
        face.setCommandSigningInfo(keyChain, certificateName);
        face.registerPrefix(prefix, this,  this::handleFailureToRegisterName);
    }

    public void processEvents() throws Exception {
        while (true) {
            face.processEvents();
            Thread.sleep(100);
        }
    }

    @Override
    public void onInterest(Name name, Interest interest, Face face, long l, InterestFilter interestFilter) {
        System.out.println("Got interest for: " + name.toUri() + " - " + interest.getName().toUri());

        Name dataName = new Name(interest.getName());
        Data data = new Data(dataName).setContent(new Blob("derp"));

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
