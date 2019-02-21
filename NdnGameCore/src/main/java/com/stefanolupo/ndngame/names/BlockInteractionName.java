package com.stefanolupo.ndngame.names;

import com.google.common.base.Preconditions;
import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;

import java.util.regex.Pattern;

/**
 * Schema: |base_name|/|game_id|/blocks/interact/|block_id|
 */
public class BlockInteractionName extends BaseName {

    private static final String LISTEN_FORMAT = GAME_BASE_NAME + "/%d/blocks/interact";
    private static final Pattern PATTERN = Pattern.compile("\\d/blocks/interact/.*");

    private long gameId;
    private String blockId;

    public BlockInteractionName(long gameId, String blockId) {
        super(String.valueOf(gameId), blockId);
        this.gameId = gameId;
        this.blockId = blockId;
    }

    public BlockInteractionName(long gameId) {
        super(String.valueOf(gameId));
        this.gameId = gameId;
    }

    public BlockInteractionName(Interest interest) {
        super(interest.getName());
        parse();
    }

    public Interest toInterest() {
        return new Interest(getListenPrefix().append(String.valueOf(blockId)));
    }

    public Name getListenPrefix() {
        return new Name(String.format(LISTEN_FORMAT, gameId));
    }

    public String getBlockId() {
        return blockId;
    }

    private void parse() {
        Preconditions.checkArgument(tailName.size() == 4,
               "Invalid tailName: %s, should have 4 components", tailName);

        gameId = Long.valueOf(tailName.get(0).toEscapedString());
        blockId = tailName.get(3).toEscapedString();
    }
}
