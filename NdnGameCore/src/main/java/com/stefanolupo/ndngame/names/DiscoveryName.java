package com.stefanolupo.ndngame.names;

import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;
import net.named_data.jndn.sync.ChronoSync2013;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Schema: /discovery/
 *      - broadcast: /broadcast
 *      - interest/data: |name|/|sequence_number|
 */
public class DiscoveryName implements AsPrefix {

    private static final Pattern NAME_PATTERN = Pattern.compile("^/discovery/" + PlayerName.PLAYER_NAME_REGEX);
    private static final String DISCOVERY = "discovery";
    private static final String BROADCAST = "broadcast";

    private BaseName baseName;
    // This name scheme is the exception to the rule
    // So better off just managing the name here rather than using PlayerName
    private String playerName;
    private long sequenceNumber;

    public DiscoveryName(ChronoSync2013.SyncState syncState) {
        String name = syncState.getDataPrefix();
        baseName = BaseName.parseFrom(name);
        sequenceNumber = syncState.getSequenceNo();
        parse(baseName.getRemainder());
    }

    public DiscoveryName(long gameId, String playerName) {
        baseName = new BaseName(gameId);
        this.playerName = playerName;
    }

    @Override
    public Name getAsPrefix() {
        return baseName.getAsPrefix()
                .append(DISCOVERY)
                .append(playerName);
    }

    public static Name getBroadcastName(long gameId) {
        return new BaseName(gameId).getAsPrefix()
                .append(DISCOVERY)
                .append(BROADCAST);
    }

    public Interest toInterest() {
        return new Interest(getAsPrefix().append(String.valueOf(sequenceNumber)));
    }

    private void parse(String remainder) {
        Matcher matcher = BaseName.matchOrThrow(remainder, NAME_PATTERN);
        playerName = matcher.group(1);
    }
}
