package com.stefanolupo.ndngame.names;

import com.google.common.base.Preconditions;
import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;
import net.named_data.jndn.sync.ChronoSync2013;

import java.util.regex.Pattern;

/**
 * Schema: base_name/|game_id|/|player_name|/bullets/|sequence_number|
 */
public class BulletsName extends BaseName {

    private static final int EXPECTED_TAIL_SIZE = 4;
    private static final Pattern NAME_PATTERN = Pattern.compile("/\\d/[a-z]+/bullets/\\d");

    private long gameId;
    private String playerName;
    private long sequenceNumber;

    public BulletsName(long gameId, String playerName) {
        this.gameId = gameId;
        this.playerName = playerName;
        this.sequenceNumber = 0;
    }

    public BulletsName(Interest interest) {
        super(interest.getName());
        parse();
    }

    public BulletsName(ChronoSync2013.SyncState syncState) {
        super(new Name(syncState.getDataPrefix()).append(String.valueOf(syncState.getSequenceNo())));
        parse();
    }

    public Name getListenName() {
        return new Name(GAME_BASE_NAME)
                .append(String.valueOf(gameId))
                .append(playerName)
                .append("bullets");
    }

    @Override
    public Interest toInterest() {
        return new Interest(getListenName().append(String.valueOf(sequenceNumber)));
    }


    private void parse() {
        Preconditions.checkArgument(tailName.size() == EXPECTED_TAIL_SIZE,
                "Invalid number of com.stefanolupo.ndngame.libgdx.components for a Bullet Name %s - had %d, expected %d",
                tailName, tailName.size(), EXPECTED_TAIL_SIZE);
        checkMatchesRegex(tailName, NAME_PATTERN);
        gameId = getLongFromComponent(tailName.get(0));
        playerName = tailName.get(1).toEscapedString();
        sequenceNumber = getLongFromComponent(tailName.get(3));
    }

    @Override
    public int hashCode() {
        return 31 * playerName.hashCode() * Long.hashCode(gameId);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BulletsName)) {
            return false;
        }

        BulletsName other = (BulletsName) obj;
        return other.playerName == playerName &&
                other.gameId == gameId &&
                other.sequenceNumber == sequenceNumber;
    }

    public long getGameId() {
        return gameId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public long getSequenceNumber() {
        return sequenceNumber;
    }
}
