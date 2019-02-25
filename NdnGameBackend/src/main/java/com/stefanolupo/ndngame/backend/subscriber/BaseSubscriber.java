package com.stefanolupo.ndngame.backend.subscriber;

import com.stefanolupo.ndngame.backend.ndn.FaceManager;
import com.stefanolupo.ndngame.names.SequenceNumberedName;
import net.named_data.jndn.Data;
import net.named_data.jndn.Interest;
import net.named_data.jndn.OnData;
import net.named_data.jndn.OnTimeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class BaseSubscriber<D> implements OnData, OnTimeout {
    private static final Logger LOG = LoggerFactory.getLogger(BaseSubscriber.class);

    private final int[] HISTOGRAM = {0, 0, 0, 0, 0};
    private static final int HISTOGRAM_BIN_SIZE_MS = 20;

    /**
     * The timeout of the interest
     */
    private static final long INTEREST_LIFETIME_MS = 1000;

    /**
     * The time to wait between receiving data for an interest and queueing up the next one
     */
    private static final long WAIT_TIME_BETWEEN_INTERESTS_MS = 10;

    private static final long MIN_SLEEP_TIME_TO_BOTHER_MS = 10;

    private SequenceNumberedName name;
    private D entity;
    private final Function<Data, D> dataFunction;
    private final Function<D, Long> sleepTimeFunction;
    private final Function<Data, SequenceNumberedName> nameExtractor;

    private final FaceManager faceManager;
    private long lastInterestExpressTime = 0;


    public BaseSubscriber(FaceManager faceManager,
                          SequenceNumberedName name,
                          Function<Data, D> dataFunction,
                          Function<Data, SequenceNumberedName> nameExtractor,
                          Function<D, Long> sleepTimeFunction) {
        this.faceManager = faceManager;
        this.name = name;
        this.dataFunction = dataFunction;
        this.nameExtractor = nameExtractor;
        this.sleepTimeFunction = sleepTimeFunction;
        expressInterestSafe(buildInterest(name));
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(this::logHistogram, 10, 20, TimeUnit.SECONDS);
    }

    public BaseSubscriber(FaceManager faceManager,
                          SequenceNumberedName name,
                          Function<Data, D> dataFunction,
                          Function<Data, SequenceNumberedName> nameExtractor) {
        this(faceManager,
                name,
                dataFunction,
                nameExtractor,
                l -> WAIT_TIME_BETWEEN_INTERESTS_MS);
    }

    @Override
    public void onData(Interest interest, Data data) {
        long now = System.currentTimeMillis();
        long delta = now - lastInterestExpressTime;
        long bin = delta / HISTOGRAM_BIN_SIZE_MS;
        updateHistogram(bin);

        entity = dataFunction.apply(data);

        // Setup the name for the next data based on what came from publisher
        name = nameExtractor.apply(data);

        long targetSleepTime = sleepTimeFunction.apply(entity);

        long sleepTime = targetSleepTime - delta;
        if (sleepTime > MIN_SLEEP_TIME_TO_BOTHER_MS) {
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

    private void updateHistogram(long bin) {
        int safeBin = (int) Math.max(0, Math.min(bin, HISTOGRAM.length - 1));
        HISTOGRAM[safeBin] = ++HISTOGRAM[safeBin];
    }

    private void logHistogram() {
        StringBuilder stringBuilder = new StringBuilder(HISTOGRAM.length);
        String format = "[%d - %d]: %d\t\t";
        for (int i=0; i<HISTOGRAM.length; i++) {
            stringBuilder.append(String.format(format, i*HISTOGRAM_BIN_SIZE_MS, (i+1)*HISTOGRAM_BIN_SIZE_MS, HISTOGRAM[i]));
        }

        LOG.debug("{}", stringBuilder.toString());
    }
}
