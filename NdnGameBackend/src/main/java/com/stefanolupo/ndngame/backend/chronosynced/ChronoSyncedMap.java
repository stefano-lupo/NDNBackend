package com.stefanolupo.ndngame.backend.chronosynced;

import net.named_data.jndn.Data;
import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A map implementation backed by ChronoSync
 * @param <K> the key to the map (e.g. the corresponding Name to a piece of data)
 * @param <V> the value of the map (the piece of data)
 */
public abstract class ChronoSyncedMap<K, V> extends ChronoSyncedDataStructure
{

    private static final Logger LOG = LoggerFactory.getLogger(ChronoSyncedMap.class);

    private final Map<K, V> map;

    public ChronoSyncedMap(Name broadcastPrefix,
                           Name dataListenPrefix) {
        super(broadcastPrefix, dataListenPrefix);
        map = new HashMap<>();
    }

    protected abstract K interestToKey(Interest interest);
    protected abstract V dataToVal(Data data, K key, V oldVal);

    @Override
    public void onData(Interest interest, Data data) {
        LOG.debug("Got data: {}", interest.toUri());
        K key = interestToKey(interest);
        V oldVal = map.get(key);
        oldVal = dataToVal(data, key, oldVal);
        map.put(key, oldVal);
    }

    public Map<K, V> getMap() {
        return Collections.unmodifiableMap(map);
    }
}
