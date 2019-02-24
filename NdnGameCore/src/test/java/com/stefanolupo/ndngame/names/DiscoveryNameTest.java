package com.stefanolupo.ndngame.names;

import net.named_data.jndn.Name;
import net.named_data.jndn.sync.ChronoSync2013;
import net.named_data.jndn.util.Blob;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DiscoveryNameTest {

    private static final String BASE_PREFIX = "/com/stefanolupo/ndngame/0/discovery";

    @Test
    public void itShouldBuildCorrectPrefixes() {
        assertEquals(new Name(BASE_PREFIX).append("broadcast"), DiscoveryName.getBroadcastName(0));

        DiscoveryName discoveryName = new DiscoveryName(0, "test");
        assertPrefixesMatch(discoveryName);
    }

    @Test
    public void itShouldParseSyncState() {
        ChronoSync2013.SyncState syncState = new ChronoSync2013.SyncState(
                String.format("%s/%s", BASE_PREFIX, "test"),
                0,
                0,
                new Blob());
        DiscoveryName discoveryName = new DiscoveryName(syncState);

        assertPrefixesMatch(discoveryName);
    }

    private void assertPrefixesMatch(DiscoveryName discoveryName) {
        Name discoveryPrefix = new Name(BASE_PREFIX).append("test");
        assertEquals(discoveryPrefix, discoveryName.getAsPrefix());
        assertEquals(discoveryPrefix.append("0"), discoveryName.toInterest().getName());
    }

}