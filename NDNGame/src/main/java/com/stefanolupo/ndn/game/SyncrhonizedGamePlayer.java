package com.stefanolupo.ndn.game;

import com.google.protobuf.InvalidProtocolBufferException;
import com.stefanolupo.ndn.GamePlayer;
import com.stefanolupo.ndn.NDNGameProtos;
import net.named_data.jndn.*;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class SyncrhonizedGamePlayer implements OnData, OnTimeout {

    private final GamePlayer gamePlayer;
    private final Face face = new Face();
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(5);
    private AtomicBoolean outstandingRequest = new AtomicBoolean(false);

    SyncrhonizedGamePlayer(String playerName) {
        gamePlayer = new GamePlayer(playerName);
        executorService.scheduleAtFixedRate(this::fetchLatestPosition, 0, 2, TimeUnit.SECONDS);
        executorService.scheduleAtFixedRate(this::checkFace, 0, 10, TimeUnit.MILLISECONDS);
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    private void fetchLatestPosition() {
        System.out.println("Fetching latest position for: " + gamePlayer.getPlayerName());
        if (outstandingRequest.get()) {
            return;
        }

        try {
            face.expressInterest(gamePlayer.getInterestName(), this, this);
            outstandingRequest.set(true);
        } catch (IOException e) {
            System.err.println("Could not express interest for " + gamePlayer.getInterestName());
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
            NDNGameProtos.PlayerStatus position = NDNGameProtos.PlayerStatus.parseFrom(data.getContent().getImmutableArray());
            gamePlayer.update(position);
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
