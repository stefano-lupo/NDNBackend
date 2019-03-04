package com.stefanolupo.ndngame.backend.publisher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SequenceNumberedCache<T> {

    private static final Logger LOG = LoggerFactory.getLogger(SequenceNumberedCache.class);

    private final List<T> list;
    private final int listSize;

    // Increases monotonically once cache is filled
    private int minVal = 0;

    // Increases monotonically on inserting
    private int maxVal = -1;

    private SequenceNumberedCache(int listSize) {
        list = Arrays.asList((T[])new Object[listSize]);
        this.listSize = listSize;
    }


    public void insert(T projectile) {
        if (maxVal >= listSize - 1) {
            minVal++;
        }
        int nextIndex = (++maxVal) % listSize;
        synchronized (list) {
            list.set(nextIndex, projectile);
        }

    }
    public boolean removeIfPresent(long index) {
        int i = Math.toIntExact(index);
        if (i >= minVal && i <= maxVal) {
            list.set(i, null);
            return true;
        }

        return false;
    }

    public List<T> getFrom(long index) {
        int i = Math.toIntExact(index);
        if (i <= minVal) {
            return getFrom(minVal % listSize, (maxVal % listSize)+1);
        }

        if (i > maxVal) {
            return Collections.emptyList();
        }

        return getFrom(i % listSize, (maxVal + 1) % listSize);
    }

    public int getMaxVal() {
        return maxVal;
    }

    public static <T> SequenceNumberedCache<T> getInstance(int listSize) {
        return new SequenceNumberedCache<>(listSize);
    }

    private List<T> getFrom(int min, int max) {
        if (min > max) {
            List<T> list = new ArrayList<>();
            list.addAll(this.list.subList(min, this.list.size()));
            list.addAll(this.list.subList(0, max));
            return list;
        }

        return new ArrayList<>(list.subList(min, max));
    }
}
