package com.stefanolupo.ndngame.backend.players;

import com.google.protobuf.InvalidProtocolBufferException;
import com.stefanolupo.ndngame.Player;
import com.stefanolupo.ndngame.protos.PlayerStatus;
import net.named_data.jndn.*;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class RemotePlayer extends Player implements OnData, OnTimeout {

    private final Face face = new Face();
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(5);
    private AtomicBoolean outstandingRequest = new AtomicBoolean(false);

    public RemotePlayer(String playerName) {
        super(playerName);
        executorService.scheduleAtFixedRate(this::fetchLatestPosition, 0, 20, TimeUnit.MILLISECONDS);
        executorService.scheduleAtFixedRate(this::checkFace, 0, 1, TimeUnit.MILLISECONDS);
    }


    private void fetchLatestPosition() {
        System.out.println("Fetching latest position for: " + playerName);
        if (outstandingRequest.get()) {
            return;
        }

        try {
            face.expressInterest(getInterestName(), this, this);
            outstandingRequest.set(true);
        } catch (IOException e) {
            System.err.println("Could not express interest for " + this.playerName);
            outstandingRequest.set(false);
        }

    }

    private final void checkFace() {
        try {
            face.processEvents();
        } catch (Exception e) {
            System.err.println("Failed to process events");
            e.printStackTrace();
        }
    }

    @Override
    public void onData(Interest interest, Data data) {
        System.out.println("Got data for: " + interest.toUri());
        outstandingRequest.set(false);
        try {
            PlayerStatus position = PlayerStatus.parseFrom(data.getContent().getImmutableArray());
            update(position);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTimeout(Interest interest) {
        outstandingRequest.set(false);
        System.out.println("Timeout on interest: " + interest.toUri());
    }
}
