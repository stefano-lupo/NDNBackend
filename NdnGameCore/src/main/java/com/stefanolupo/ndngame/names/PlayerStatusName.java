package com.stefanolupo.ndngame.names;

import com.google.common.annotations.VisibleForTesting;
import net.named_data.jndn.Data;
import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Schema
 * Prefix: /status
 *  - Express interest: /sync/|sequence_number|
 *  - Reply to interest: /sync/|sequence_number|/|next_sequence_number|
 */
public class PlayerStatusName implements SequenceNumberedName {

    private static final String BASE_PATTERN = "^/status/sync";

    @VisibleForTesting
    static final Pattern PATTERN = Pattern.compile(BASE_PATTERN + "/(\\d+)/?(\\d+)?/?(\\d+)?");

    private static final String STATUS = "status";
    private static final String SYNC = "sync";

    private PlayerName playerName;
    private long sequenceNumber = 0;
    private long nextSequenceNumber = 0;
    private long sentTimestamp = 0;

    /**
     * Create a PlayerStatusName using the components
     * Initialize both sequence numbers to zero
     * Used on discovery
     */
    public PlayerStatusName(long gameId, String playerName) {
        this.playerName = new PlayerName(gameId, playerName);
    }

    /**
     * Create Name from received interest
     * Used by Producer
     */
    public PlayerStatusName(Interest interest) {
        playerName = new PlayerName(interest);
        parse(playerName.getRemainder());
    }

    /**
     * Create Name from data sent back from producer
     * This is the name that contains the updated next sequence number
     */
    public PlayerStatusName(Data data) {
        playerName = new PlayerName(data);
        parse(playerName.getRemainder());
    }

    @Override
    public void setNextSequenceNumber(long nextSequenceNumber) {
        this.nextSequenceNumber = nextSequenceNumber;
    }

    /**
     * Used by subscribers to create interests for the latest sequence number
     */
    @Override
    public Interest buildInterest() {

        // This must NOT contain the nextSequenceNumber
        // Otherwise we will only get data back for when they match!!
        Name name = getListenName().append(String.valueOf(nextSequenceNumber));
        return new Interest(name);
    }

    /**
     * Used by producers to register prefix
     * @return the name to accept interests for
     */
    public Name getListenName() {
        return playerName.getAsPrefix()
                .append(STATUS)
                .append(SYNC);
    }

    /**
     * Used by publishers to name their data packets
     * @return Data packet Name with appropriate sequence numbers
     */
    @Override
    public Name getFullName() {
        return getListenName()
                .append(String.valueOf(sequenceNumber))
                .append(String.valueOf(nextSequenceNumber))
                .append(String.valueOf(sentTimestamp));
    }


    /**
     * Engine needs this for comparing its entitys
     */
    @Override
    public long getLatestSequenceNumberSeen() {
        return sequenceNumber;
    }

    public PlayerName getPlayerName() {
        return playerName;
    }

    @Override
    public void setUpdateTimestamp(long updateTimestamp) {
        this.sentTimestamp = updateTimestamp;
    }

    @Override
    public long getUpdateTimestamp() {
        return sentTimestamp;
    }

    private void parse(String remainder) {
        Matcher matcher = BaseName.matchOrThrow(remainder, PATTERN);
        sequenceNumber = Long.valueOf(matcher.group(1));

        String nextSequenceNumber = matcher.group(2);

        if (nextSequenceNumber != null) {
            this.nextSequenceNumber = Long.valueOf(nextSequenceNumber);
        }

        if (matcher.group(3) != null) {
            sentTimestamp = Long.valueOf(matcher.group(3));
        }
    }


    /**
     * equals which ignores the sequence number
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PlayerStatusName)) {
            return false;
        }

        PlayerStatusName other = (PlayerStatusName) obj;

        return other.playerName.equals(playerName) && other.sequenceNumber == sequenceNumber && other.nextSequenceNumber == nextSequenceNumber;
    }

    /**
     * hashCode which ignores the sequence number
     */
    @Override
    public int hashCode() {
        return 31 * playerName.hashCode();
    }

    @Override
    public String toString() {
        return "PlayerStatusName{" +
                "playerName=" + playerName +
                '}';
    }
}
