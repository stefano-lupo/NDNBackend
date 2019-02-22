package com.stefanolupo.ndngame.names;

import net.named_data.jndn.Data;
import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;

import java.util.regex.Pattern;

/**
 * Schema: base_name/|game_id|/|player_name|/status/sync/|sequence_number|/|next_seqeunce_number|
 */
public class PlayerStatusName
        extends BaseName
        implements SequenceNumberedName {

    private static final Pattern NAME_PATTERN = Pattern.compile("/\\d+/[a-z]+/status/sync/\\d+");

    private long gameId;
    private String playerName;
    private long sequenceNumber;
    private long nextSequenceNumber = sequenceNumber;

    /**
     * Create a PlayerStatusName using the components
     * Initialize both sequence numbers to zero
     * Used on discovery
     */
    public PlayerStatusName(long gameId, String playerName) {
        super(String.valueOf(gameId), playerName, "status", "sync");
        this.gameId = gameId;
        this.playerName = playerName;
        this.sequenceNumber = 0;
        this.nextSequenceNumber = this.sequenceNumber;
    }

    /**
     * Create Name from received interest
     * Used by Producer
     */
    public PlayerStatusName(Interest interest) {
        super(interest.getName());
        parse();
    }

    /**
     * Create Name from data sent back from producer
     * This is the name that contains the updated next sequence number
     */
    public PlayerStatusName(Data data) {
        super(data.getName());
        parse();
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
    @Override
    public Name getListenName() {
        return new Name(GAME_BASE_NAME)
                .append(String.valueOf(gameId))
                .append(playerName)
                .append("status")
                .append("sync");
    }

    /**
     * Used by publishers to name their data packets
     * @return Data packet Name with appropriate sequence numbers
     */
    @Override
    public Name getFullName() {
        return getListenName().append(String.valueOf(sequenceNumber)).append(String.valueOf(nextSequenceNumber));
    }


    /**
     * Engine needs this for comparing its entities
     */
    @Override
    public long getLatestSequenceNumberSeen() {
        return sequenceNumber;
    }

    public long getGameId() {
        return gameId;
    }

    public String getPlayerName() {
        return playerName;
    }

    private void parse() {
//        Preconditions.checkArgument(tailName.size() == 4);
        checkMatchesRegex(tailName, NAME_PATTERN);

        gameId = Long.valueOf(tailName.get(0).toEscapedString());
        playerName = tailName.get(1).toEscapedString();
        sequenceNumber = Long.valueOf(tailName.get(4).toEscapedString());

        if (tailName.size() == 6) {
            nextSequenceNumber = Long.valueOf(tailName.get(5).toEscapedString());
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

        return other.playerName.equals(playerName) &&
                other.gameId == gameId;
    }

    /**
     * hashCode which ignores the sequence number
     */
    @Override
    public int hashCode() {
        return 31 * playerName.hashCode() + Long.hashCode(gameId);
    }
}
