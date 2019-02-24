package com.stefanolupo.ndngame.names.blocks;

import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class BlocksNameTest {


    private static final String REMAINDER_DATA = "/remainder/data";
    private static final Name EXPECTED_PREFIX = new Name("/com/stefanolupo/ndngame/0/test/blocks");

    @Test
    public void itShouldGiveCorrectPrefix() {
        BlocksName blocksName = new BlocksName(0, "test");
        assertEquals(EXPECTED_PREFIX, blocksName.getAsPrefix());
    }

    @Test
    public void itShouldParseCorrectly() {
        Name name = new Name("/com/stefanolupo/ndngame/0/test/blocks" + REMAINDER_DATA);
        BlocksName blocksName = new BlocksName(new Interest(name));

        assertEquals(EXPECTED_PREFIX, blocksName.getAsPrefix());
        assertEquals(REMAINDER_DATA, blocksName.getRemainder());
    }
}