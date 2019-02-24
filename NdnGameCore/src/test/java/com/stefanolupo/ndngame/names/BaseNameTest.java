package com.stefanolupo.ndngame.names;

import net.named_data.jndn.Name;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class BaseNameTest {

    private static final Name BASE_NAME = new Name("/com/stefanolupo/ndngame/0");

    @Test
    public void itShouldReturnPrefixWithId() {
        assertEquals(BASE_NAME, new BaseName(0).getAsPrefix());
    }

    @Test
    public void itShouldParseFromName() {
        Name name = new Name("/com/stefanolupo/ndngame/0/remainder/data");
        BaseName baseName = BaseName.parseFrom(name);
        assertEquals(baseName.getAsPrefix(), BASE_NAME);
        assertEquals(baseName.getRemainder(), "/remainder/data");
    }
}