package com.stefanolupo.ndngame.backend.publisher;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class SequenceNumberedCacheTest {

    private static final int SIZE = 5;

    private SequenceNumberedCache<Integer> cache;

    @Before
    public void setup() {
        cache = SequenceNumberedCache.getInstance(SIZE);
    }

    @Test
    public void itShouldHandleEmptyCache() {
        assertEquals(Collections.emptyList(), cache.getFrom(10));
        assertEquals(Collections.emptyList(), cache.getFrom(0));
    }

    @Test
    public void itShouldOperateBeforeFullyFilled() {
        cache.insert(0);
        cache.insert(1);
        cache.insert(2);

        assertEquals(Collections.emptyList(), cache.getFrom(10));
        assertEquals(Arrays.asList(0, 1, 2), cache.getFrom(0));
        assertEquals(Arrays.asList(1, 2), cache.getFrom(1));
    }

    @Test
    public void itShouldOperateOnceUnsymmetricallyFilled() {
        for (int i=0; i<8; i++) {
            cache.insert(i);
        }

        // At this point: [5, 6, 7, 3, 4], minVal = 3, maxVal = 7

        assertEquals(Collections.emptyList(), cache.getFrom(10));

        List<Integer> expectedFull = new ArrayList<>(Arrays.asList(3, 4, 5, 6, 7));
        for (int i = 0; i < 3; i++) {
            assertEquals(expectedFull, cache.getFrom(i));
        }

        assertEquals(Arrays.asList(3, 4, 5, 6, 7), cache.getFrom(3));
        assertEquals(Arrays.asList(4, 5, 6, 7), cache.getFrom(4));
        assertEquals(Arrays.asList(5, 6, 7), cache.getFrom(5));
        assertEquals(Arrays.asList(6, 7), cache.getFrom(6));
        assertEquals(Arrays.asList(7), cache.getFrom(7));

    }

    @Test
    public void itShouldOperateOnceSymmetricallyFilled() {
        for (int i=0; i<10; i++) {
            cache.insert(i);
        }

        // At this point: [5, 6, 7, 8, 9], minVal = 3, maxVal = 7

        assertEquals(Collections.emptyList(), cache.getFrom(10));

        List<Integer> expectedFull = new ArrayList<>(Arrays.asList(5, 6, 7, 8, 9));
        for (int i = 0; i < 5; i++) {
            assertEquals(expectedFull, cache.getFrom(i));
        }

        assertEquals(Arrays.asList(5, 6, 7, 8, 9), cache.getFrom(5));
        assertEquals(Arrays.asList(6, 7, 8, 9), cache.getFrom(6));
        assertEquals(Arrays.asList(7, 8, 9), cache.getFrom(7));
        assertEquals(Arrays.asList(8, 9), cache.getFrom(8));
        assertEquals(Arrays.asList(9), cache.getFrom(9));
    }
}
