package com.stefanolupo.ndngame.names.blocks;

import com.stefanolupo.ndngame.names.PlayerName;
import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class BlockInteractNameTest {

    private static final String ID = UUID.randomUUID().toString();
    private static final String BASE_PREFIX = "/com/stefanolupo/ndngame/0/test/blocks/interact";
    private static final Name EXPECTED_INTEREST_NAME = new Name(BASE_PREFIX + "/" + ID);

    @Test
    public void itShouldBuildCorrectNames() {
        BlockInteractName name = new BlockInteractName(0, "test", ID);

        assertEquals(new Name(BASE_PREFIX), name.getAsPrefix());
        assertEquals(EXPECTED_INTEREST_NAME, name.buildInterest().getName());
    }

    @Test
    public void itShouldParseFromInterest() {
        BlockInteractName interactName = new BlockInteractName(new Interest(EXPECTED_INTEREST_NAME));
        assertEquals(ID, interactName.getId());
        assertEquals(new PlayerName(0, "test"), interactName.getPlayerName());
    }
}