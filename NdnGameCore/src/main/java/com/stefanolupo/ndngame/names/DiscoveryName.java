package com.stefanolupo.ndngame.names;

import com.google.common.base.Preconditions;
import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;
import net.named_data.jndn.sync.ChronoSync2013;

import java.util.regex.Pattern;

/**
 * Schema: base_name/|game_id|/discovery/|name|/|sequence_number|
 */
public class DiscoveryName extends BaseName {

    private static final String DISCOVERY_BROADCAST = GAME_BASE_NAME + "/%d/discovery/broadcast";
    private static final String DISCOVERY_DATA = GAME_BASE_NAME + "/%d/discovery/%s/%d";
    private static final Pattern NAME_PATTERN = Pattern.compile("/\\d+/discovery/[a-z]+/\\d+");

    private long gameId;
    private String playerName;
    private long sequenceNumber;

    public DiscoveryName(Interest interest) {
        super(interest.getName());
        parse();
    }

    public DiscoveryName(ChronoSync2013.SyncState syncState) {
        super(new Name(syncState.getDataPrefix()));
        parse();
    }

    public DiscoveryName(long gameId, String playerName) {
        super(String.valueOf(gameId), playerName, "discovery");
        this.gameId = gameId;
        this.playerName = playerName;
        this.sequenceNumber = 0;
    }

    public Interest toInterest() {
        return new Interest(getDataListenPrefix(gameId, playerName).append(String.valueOf(sequenceNumber)));
    }

    public static Name getDataListenPrefix(long gameId, String playerName) {
        return new Name(String.format(DISCOVERY_DATA, gameId, playerName, 0));
    }

    public static Name getBroadcastPrefix(long gameId) {
        return new Name(String.format(DISCOVERY_BROADCAST, gameId));
    }

    private void parse() {
        Preconditions.checkArgument(tailName.size() == 4);
        checkMatchesRegex(tailName, NAME_PATTERN);

        gameId = Long.valueOf(tailName.get(0).toEscapedString());
        playerName = tailName.get(2).toEscapedString();
        sequenceNumber = Long.valueOf(tailName.get(3).toEscapedString());
    }
}
