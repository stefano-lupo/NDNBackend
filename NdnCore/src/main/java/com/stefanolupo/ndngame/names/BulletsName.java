package com.stefanolupo.ndngame.names;

import com.google.common.base.Preconditions;
import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;
import net.named_data.jndn.sync.ChronoSync2013;

import java.util.regex.Pattern;

/**
 * Schema: base_name/|game_id|/bullet/|bullet_id|/|sequence_number|
 */
public class BulletsName extends BaseName {

    private static final int EXPECTED_TAIL_SIZE = 5;
    private static final Pattern NAME_PATTERN = Pattern.compile("/\\d//bullet/\\d\\d");

    private long gameId;
    private long bulletId;
    private long sequenceNumber;

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
                .append("bullet")
                .append(String.valueOf(bulletId));
    }

    @Override
    public Interest toInterest() {
        return null;
    }

    public Name getExpressInterestName() {
        return getListenName()
                .append(String.valueOf(sequenceNumber));
    }

    private void parse() {
        Preconditions.checkArgument(tailName.size() == EXPECTED_TAIL_SIZE,
                "Invalid number of components for a Bullet Name - had %d, expected %d",
                tailName.size(), EXPECTED_TAIL_SIZE);
        checkMatchesRegex(tailName, NAME_PATTERN);
        gameId = getLongFromComponent(tailName.get(0));
        bulletId = getLongFromComponent(tailName.get(2));
        sequenceNumber = getLongFromComponent(tailName.get(3));
    }

    @Override
    public int hashCode() {
        return 31 * Long.hashCode(gameId * bulletId);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BulletsName)) {
            return false;
        }

        BulletsName other = (BulletsName) obj;
        return other.bulletId == bulletId &&
                other.sequenceNumber == sequenceNumber;
    }

    public long getGameId() {
        return gameId;
    }

    public long getBulletId() {
        return bulletId;
    }

    public long getSequenceNumber() {
        return sequenceNumber;
    }
}
