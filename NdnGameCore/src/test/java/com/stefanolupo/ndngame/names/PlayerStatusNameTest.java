package com.stefanolupo.ndngame.names;

import net.named_data.jndn.Data;
import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PlayerStatusNameTest {

    private static final String EXPECTED_PREFIX = "/com/stefanolupo/ndngame/0/test/status/sync";

    @Test
    public void itShouldBuildCorrectPrefixes() {
        PlayerStatusName playerStatusName = new PlayerStatusName(0, "test");
        assertPrefixesMatch(playerStatusName);
    }

    @Test
    public void itShouldParseFromInterest() {
        // Parse from interests on receiving interests, these only have the current sequence number
        Name name = new Name(EXPECTED_PREFIX).append("0");
        PlayerStatusName playerStatusName = new PlayerStatusName(new Interest(name));
        assertPrefixesMatch(playerStatusName);
    }

    @Test
    public void itShouldParseFromData() {
        // Parse from data on receiving data back which should have the next sequence number too
        Name name = new Name(EXPECTED_PREFIX).append("0").append("1");
        PlayerStatusName playerStatusName = new PlayerStatusName(new Data(name));

        assertEquals(new Name(EXPECTED_PREFIX).append("1"), playerStatusName.buildInterest().getName());
    }

    private void assertPrefixesMatch(PlayerStatusName playerStatusName) {
        assertEquals(new Name(EXPECTED_PREFIX), playerStatusName.getListenName());
        assertEquals(new Name(EXPECTED_PREFIX).append("0"), playerStatusName.buildInterest().getName());
        playerStatusName.setNextSequenceNumber(1);
        assertEquals(new Name(EXPECTED_PREFIX).append("0").append("1"), playerStatusName.getFullName());
    }

}