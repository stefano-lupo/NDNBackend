package com.stefanolupo.ndngame.backend;

import net.named_data.jndn.*;
import net.named_data.jndn.security.KeyChain;
import net.named_data.jndn.sync.ChronoSync2013;
import net.named_data.jndn.util.Blob;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChronoSyncTest implements
        ChronoSync2013.OnInitialized,
        ChronoSync2013.OnReceivedSyncState,
        OnData,
        OnInterestCallback
{

    private static final Name GAME_BROADCAST_PREFIX = new Name("/ndngame/broadcast");
    private static final Name NODE_HUB_PREFIX = new Name("/com/stefanolupo/house");

    private final Map<Long, String> messagesBySeqNo = new HashMap<>();
    private final ChronoSync2013 chronoSync;
    private final Face face;
    private final KeyChain keyChain;
    private final Name certificateName;

    ChronoSyncTest(String userName, String chatroomName) throws Exception {

        // Note name here should be randomised
        int session = (int)Math.round(System.currentTimeMillis() / 1000.0);
        Name producerDataName = NODE_HUB_PREFIX.append(chatroomName).append(userName).append(String.valueOf(session));
        Name broadcastName = GAME_BROADCAST_PREFIX.append(chatroomName);

        // Set up things need for NDN comms
        face = new Face();
        keyChain = new KeyChain();
        certificateName = keyChain.getDefaultCertificateName();
        face.setCommandSigningInfo(keyChain, certificateName);


        // Note chronosync handles creating a face for the broadcast channel
        // But does not create a face for the data channel
        // It needs it for naming things when it publishes next sequence number though
        chronoSync = new ChronoSync2013(
                this,
                this,
                producerDataName,
                broadcastName,
                session,
                face,
                keyChain,
                certificateName,
                1000.0,
                this::registerPrefixFailure
        );

        // Set up listener for data interests that come from me
        face.registerPrefix(producerDataName, this, this::registerPrefixFailure);
//        face.setInterestFilter(new InterestFilter(producerDataName, "[0-9]+"), this);
    }

    private void registerPrefixFailure(Name prefixName) {
        System.err.println("Unable to register " + prefixName.toUri());
    }

    @Override
    public void onData(Interest interest, Data data) {
        System.out.println("Message Received: " + data.getContent().toString());
    }

    @Override
    public void onInterest(Name prefix, Interest interest, Face face, long interestFilterId, InterestFilter filter) {
        String fullName = interest.getName().toUri();
        System.out.println("Got interest: " + fullName);
        long sequenceNumber = Long.valueOf(fullName.substring(fullName.lastIndexOf('/') + 1));

        if (messagesBySeqNo.containsKey(sequenceNumber)) {
            Data data = new Data(new Name(fullName)).setContent(new Blob(messagesBySeqNo.get(sequenceNumber)));
            try {
                keyChain.sign(data, certificateName);
                face.send(data.getDefaultWireEncoding().buf());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("Could not satisfy interest for sequence number " + sequenceNumber);
        }

    }

    @Override
    public void onInitialized() {
        System.out.println("Im initialized");
    }

    @Override
    public void onReceivedSyncState(List syncStates, boolean isRecovery) {
        ChronoSync2013.SyncState latestSyncState = (ChronoSync2013.SyncState) syncStates.get(syncStates.size() - 1);
        Name interestName = new Name(latestSyncState.getDataPrefix()).append(String.valueOf(latestSyncState.getSequenceNo()));
        System.out.println("Sync states received, expressing interest for: " + interestName);
        try {
            face.expressInterest(interestName, this, i -> System.err.println("Timeout for " + i.toUri()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Name buildName(String... args) {
        Name name = new Name();
        for (String arg : args) {
            name = name.append(arg);
        }

        return name;
    }

    public void sendMessage(String s) {
        try {
            chronoSync.publishNextSequenceNo();
            messagesBySeqNo.put(chronoSync.getSequenceNo(), s);
        } catch (Exception e) {
            throw new RuntimeException("Exeption senidng message", e);
        }
    }

    public static void main(String[] args) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        ChronoSyncTest chronoSyncTest = new ChronoSyncTest(args[0], args[1]);

        while (true) {
            if (reader.ready()) {
                chronoSyncTest.sendMessage(reader.readLine());
                System.out.print("Enter message: ");
            }
            chronoSyncTest.face.processEvents();
            Thread.sleep(10);
        }
    }
}
