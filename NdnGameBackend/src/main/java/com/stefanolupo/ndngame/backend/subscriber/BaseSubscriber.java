package com.stefanolupo.ndngame.backend.subscriber;

import com.stefanolupo.ndngame.names.SequenceNumberedName;
import net.named_data.jndn.*;
import net.named_data.jndn.encoding.EncodingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class BaseSubscriber<D> implements OnData, OnTimeout {
    private static final Logger LOG = LoggerFactory.getLogger(BaseSubscriber.class);

    /**
     * The timeout of the interest
     */
    private static final long INTEREST_LIFETIME_MS = 2000000;

    /**
     * The time to wait between receiving data for an interest and queueing up the next one
     */
    private static final long WAIT_TIME_BETWEEN_INTERESTS_MS = 10;

    private SequenceNumberedName name;
    private D entity;
    private final Function<Data, D> dataFunction;
    private final Function<Data, SequenceNumberedName> nameExtractor;

    private final Face face;
    private long lastInterestExpressTime = 0;


    public BaseSubscriber(SequenceNumberedName name,
                          Function<Data, D> dataFunction,
                          Function<Data, SequenceNumberedName> nameExtractor) {
        this.name = name;
//        this.face = new Face(new UdpTransport(), new UdpTransport.ConnectionInfo("localhost", 6363));
        this.face = new Face();
        this.dataFunction = dataFunction;
        this.nameExtractor = nameExtractor;
        expressInterestSafe(buildInterest(name));
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(this::pollFace, 500, 10, TimeUnit.MILLISECONDS);
    }


    @Override
    public void onData(Interest interest, Data data) {
        entity = dataFunction.apply(data);

        // Setup the name for the next data based on what came from publisher
        name = nameExtractor.apply(data);

        long now = System.currentTimeMillis();
        long sleepTime = WAIT_TIME_BETWEEN_INTERESTS_MS - (now - lastInterestExpressTime);
        if (sleepTime > 10) {
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        expressInterestSafe(buildInterest(name));
    }

    public long getLatestVersionSeen() {
        return name.getLatestSequenceNumberSeen();
    }

    @Override
    public void onTimeout(Interest interest) {
        LOG.info("Timeout for {}, resending interest", interest.toUri());
        expressInterestSafe(buildInterest(name));
    }

    public D getEntity() {
        return entity;
    }

    private Interest buildInterest(SequenceNumberedName name) {
        // We wan't routers on the way to provide cached copies if applicable (obviously)
        // But our sequence number can fall behind potentially
        // If falls pretty far behind, cached ones in routers will no longer be fresh and we will go to producer
        // They will send the LATEST SEQUENCE number which we will then jump to
        return name.buildInterest()
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
