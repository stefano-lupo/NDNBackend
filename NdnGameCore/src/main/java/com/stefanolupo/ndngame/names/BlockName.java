package com.stefanolupo.ndngame.names;

import net.named_data.jndn.Data;
import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;

import java.util.regex.Pattern;

/**
 * Schema: base_name/|game_id|/|name|/blocks/sync/|sequence_number|/\next_sequence_number|
 */
public class BlockName extends BaseName implements SequenceNumberedName {

    private static final Pattern NAME_PATTERN = Pattern.compile("/\\d+/[a-z]+/blocks/sync/\\d+");

    private long gameId;
    private String playerName;
    private long sequenceNumber;
    private long nextSequenceNumber = sequenceNumber;

    // Eek
    private String id = null;

    public BlockName(long gameId, String playerName) {
        super(String.valueOf(gameId), playerName, "blocks", "sync");
        this.gameId = gameId;
        this.playerName = playerName;
        this.sequenceNumber = 0;
        this.nextSequenceNumber = sequenceNumber;
    }

    public BlockName(Interest interest) {
        super(interest.getName());
        parse();
    }

    public BlockName(Data data) {
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
                .append("blocks")
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


    public void setId(String id) {
        this.id = id;
    }

    //TODO: Need to implement
    public String getId() {
        return id;
    }
}
