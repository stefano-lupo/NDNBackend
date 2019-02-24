package com.stefanolupo.ndngame.names;

import net.named_data.jndn.Data;
import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Schema
 * Prefix: |player_name|/status
 *  - Express interest: /sync/|sequence_number|
 *  - Reply to interest: /sync/|sequence_number|/|next_sequence_number|
 */
public class PlayerStatusName implements SequenceNumberedName {

    private static final String BASE_PATTERN = "^/([a-zA-Z0-9]+)/status";
    private static final Pattern EXPRESS_PATTERN = Pattern.compile(BASE_PATTERN + "/sync/(\\d)$");
    private static final Pattern DATA_NAME_PATTERN = Pattern.compile(BASE_PATTERN + "/sync/\\d/(\\d)$");

    private static final String STATUS = "status";
    private static final String SYNC = "sync";

    private BaseName baseName;
    private String playerName;
    private long sequenceNumber = 0;
    private long nextSequenceNumber = 0;

    /**
     * Create a PlayerStatusName using the components
     * Initialize both sequence numbers to zero
     * Used on discovery
     */
    public PlayerStatusName(long gameId, String playerName) {
//        buildNonPrefixedName(String.valueOf(gameId), playerName, STATUS, SYNC);
        this.baseName = new BaseName(gameId);
        this.playerName = playerName;
    }

    /**
     * Create Name from received interest
     * Used by Producer
     */
    public PlayerStatusName(Interest interest) {
        baseName = BaseName.parseFrom(interest);
        parse(baseName.getRemainder());
    }

    /**
     * Create Name from data sent back from producer
     * This is the name that contains the updated next sequence number
     */
    public PlayerStatusName(Data data) {
        baseName = BaseName.parseFrom(data);
        parse(baseName.getRemainder());
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
        return baseName.getAsPrefix()
                .append(playerName)
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
                .append(String.valueOf(nextSequenceNumber));
    }


    /**
     * Engine needs this for comparing its entities
     */
    @Override
    public long getLatestSequenceNumberSeen() {
        return sequenceNumber;
    }

//    public long getGameId() {
//        return gameId;
//    }

    public String getPlayerName() {
        return playerName;
    }

    private void parse(String remainder) {
        Matcher matcher = BaseName.matchOrThrow(remainder, EXPRESS_PATTERN);
        playerName = matcher.group(1);
        sequenceNumber = Long.valueOf(matcher.group(2));

        Matcher dataNameMatcher = DATA_NAME_PATTERN.matcher(remainder);
        if (dataNameMatcher.matches()) {
            nextSequenceNumber = Long.valueOf(dataNameMatcher.group(1));
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

        return other.playerName.equals(playerName);
    }

    /**
     * hashCode which ignores the sequence number
     */
    @Override
    public int hashCode() {
        return 31 * playerName.hashCode();
    }
}
