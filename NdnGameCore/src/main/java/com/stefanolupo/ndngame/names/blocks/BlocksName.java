package com.stefanolupo.ndngame.names.blocks;

import com.google.common.base.Preconditions;
import com.stefanolupo.ndngame.names.AsPrefix;
import com.stefanolupo.ndngame.names.BaseName;
import com.stefanolupo.ndngame.names.PlayerName;
import net.named_data.jndn.Data;
import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Schema
 * Prefix: /|name|/blocks

 *      - Interact: /interact/
 *          - Express: |block_id|
 */
class BlocksName implements AsPrefix {

    protected static final String BLOCK_BASE_NAME = "^/blocks(.*)";
    private static final Pattern BLOCK_NAME_PATTERN = Pattern.compile(BLOCK_BASE_NAME);
    private static final String BLOCKS = "blocks";

    protected String remainder;

    private PlayerName playerName;

    protected BlocksName(long gameId, String playerName) {
        this.playerName = new PlayerName(gameId, playerName);
    }

    protected BlocksName(PlayerName playerName) {
        this.playerName = playerName;
    }

    protected BlocksName(Interest interest) {
        playerName = new PlayerName(interest);
        parse(playerName.getRemainder());
    }

    protected BlocksName(Data data) {
        playerName = new PlayerName(data);
        parse(playerName.getRemainder());
    }

    @Override
    public Name getAsPrefix() {
        return playerName.getAsPrefix().append(BLOCKS);
    }

    public PlayerName getPlayerName() {
        return playerName;
    }

    public String getRemainder() {
        return Preconditions.checkNotNull(remainder, "Remainder was null and requested for");
    }

    private void parse(String postfix) {
        Matcher matcher = BaseName.matchOrThrow(postfix, BLOCK_NAME_PATTERN);
        remainder = matcher.group(1);
    }


    @Override
    public int hashCode() {
        return 31 * playerName.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BlocksName)) {
            return false;
        }

        BlocksName other = (BlocksName) obj;
        return other.playerName.equals(playerName);
    }

}
