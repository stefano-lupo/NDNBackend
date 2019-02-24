package com.stefanolupo.ndngame.backend.subscriber;

import com.stefanolupo.ndngame.backend.ndn.FaceManager;
import com.stefanolupo.ndngame.names.SequenceNumberedName;
import net.named_data.jndn.Data;
import net.named_data.jndn.Interest;
import net.named_data.jndn.OnData;
import net.named_data.jndn.OnTimeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

public class BaseSubscriber<D> implements OnData, OnTimeout {
    private static final Logger LOG = LoggerFactory.getLogger(BaseSubscriber.class);

    /**
     * The timeout of the interest
     */
    private static final long INTEREST_LIFETIME_MS = 1000;

    /**
     * The time to wait between receiving data for an interest and queueing up the next one
     */
    private static final long WAIT_TIME_BETWEEN_INTERESTS_MS = 10;

    private SequenceNumberedName name;
    private D entity;
    private final Function<Data, D> dataFunction;
    private final Function<Data, SequenceNumberedName> nameExtractor;

    private final FaceManager faceManager;
    private long lastInterestExpressTime = 0;


    public BaseSubscriber(FaceManager faceManager,
                          SequenceNumberedName name,
                          Function<Data, D> dataFunction,
                          Function<Data, SequenceNumberedName> nameExtractor) {
        this.faceManager = faceManager;
        this.name = name;
        this.dataFunction = dataFunction;
        this.nameExtractor = nameExtractor;
        expressInterestSafe(buildInterest(name));
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
//        LOG.info("Timeout for {}, resending interest", interest.toUri());
        expressInterestSafe(buildInterest(name));
    }

    public D getEntity() {
        return entity;
    }

    private Interest buildInterest(SequenceNumberedName name) {
        return name.buildInterest()
                .setMustBeFresh(true)
                .setInterestLifetimeMilliseconds(INTEREST_LIFETIME_MS)
                .setCanBePrefix(true);
    }

    private void expressInterestSafe(Interest i) {
        lastInterestExpressTime = System.currentTimeMillis();
        faceManager.expressInterestSafe(i, this, this);
    }
}
