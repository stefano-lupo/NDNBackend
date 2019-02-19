package com.stefanolupo.ndngame.backend.subscriber;

import net.named_data.jndn.*;
import net.named_data.jndn.encoding.EncodingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;

public class BaseSubscriber<T> implements OnData, OnTimeout {
    private static final Logger LOG = LoggerFactory.getLogger(BaseSubscriber.class);
    private static final long INTEREST_LIFETIME_MS = 1000;
    private static final long WAIT_TIME_MS = 20;

    private final Name name;
    private T entity;
    private final BiFunction<Interest, Data, T> dataFunction;

    private long latestVersionSeen = 0;
    private final Face face;
    private long lastInterestExpressTime = 0;


    public BaseSubscriber(Name name, BiFunction<Interest, Data, T> dataFunction) {
        this.name = name;
//        this.face = new Face(new UdpTransport(), new UdpTransport.ConnectionInfo("localhost", 6363));
        this.face = new Face();
        this.dataFunction = dataFunction;

        expressInterestSafe(buildInterest(name));
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(this::pollFace, 500, 10, TimeUnit.MILLISECONDS);
    }

//    protected abstract T typeFromData(Interest interest, Data data);

    @Override
    public void onData(Interest interest, Data data) {
        entity = dataFunction.apply(interest, data);
        latestVersionSeen++;

        long now = System.currentTimeMillis();
        long sleepTime = WAIT_TIME_MS - (now - lastInterestExpressTime);
        if (sleepTime > 10) {
//            LOG.debug("Sleeping for {}ms", sleepTime);
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        expressInterestSafe(buildInterest(name));
    }

    public long getLatestVersionSeen() {
        return latestVersionSeen;
    }

    @Override
    public void onTimeout(Interest interest) {
//        LOG.info("Timeout for {}, resending interest", interest.toUri());
        expressInterestSafe(buildInterest(name));
    }

    public T getEntity() {
        return entity;
    }

    private Interest buildInterest(Name name) {
        // We wan't routers on the way to provide cached copies if applicable (obviously)
        // But our sequence number can fall behind potentially
        // If falls pretty far behind, cached ones in routers will no longer be fresh and we will go to producer
        // They will send the LATEST SEQUENCE number which we will then jump to

        return new Interest(name)
                .setMustBeFresh(true)
                .setInterestLifetimeMilliseconds(INTEREST_LIFETIME_MS)
                .setCanBePrefix(true);
    }

    private void expressInterestSafe(Interest i) {
        lastInterestExpressTime = System.currentTimeMillis();
        try {
            face.expressInterest(i, this, this);
        } catch (IOException e) {
            LOG.error("Unable to express interest for {}", i.toUri(), e);
        }
    }

    private void pollFace() {
        try {
            face.processEvents();
        } catch (IOException | EncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
