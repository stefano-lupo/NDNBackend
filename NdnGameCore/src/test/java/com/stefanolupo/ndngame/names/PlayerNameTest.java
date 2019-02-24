package com.stefanolupo.ndngame.names;

import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class PlayerNameTest {

    private static final String REMAINDER_DATA = "/remainder/data";
    private static final Name EXPECTED_PREFIX = new Name("/com/stefanolupo/ndngame/0/test");

    @Test
    public void itShouldReturnRightPrefix() {
        PlayerName playerName = new PlayerName(0, "test");
        assertEquals(EXPECTED_PREFIX, playerName.getAsPrefix());
    }

    @Test
    public void itShouldParseFromNames() {
        Interest interest = new Interest(new Name("/com/stefanolupo/ndngame/0/test" + REMAINDER_DATA));
        PlayerName playerName = new PlayerName(interest);

        assertEquals(EXPECTED_PREFIX, playerName.getAsPrefix());
        assertEquals(REMAINDER_DATA, playerName.getRemainder());
    }
}