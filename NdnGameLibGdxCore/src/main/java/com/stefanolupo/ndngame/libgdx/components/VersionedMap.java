package com.stefanolupo.ndngame.libgdx.components;

import java.util.HashMap;
import java.util.Map;

public class VersionedMap<K, V> {

    private final Map<VersionedK, V> map;

    public VersionedMap() {
        map = new HashMap<>();
    }



    private class VersionedK {
        K key;
        long version = 0;

        @Override
        public int hashCode() {
            return 31 * key.hashCode() * Long.hashCode(version);
        }


    }
}
