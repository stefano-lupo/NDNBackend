package com.stefanolupo.ndngame.names.projectiles;


import com.stefanolupo.ndngame.names.AsPrefix;
import com.stefanolupo.ndngame.names.BaseName;
import com.stefanolupo.ndngame.names.SequenceNumberedName;
import net.named_data.jndn.Data;
import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Schema: /sync/
 *      - Express: |sequence_number|
 *      - Data: |sequence_number|/\next_sequence_number|
 */
public class ProjectilesSyncName
        extends ProjectilesName
        implements SequenceNumberedName, AsPrefix {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectilesSyncName.class);

    private static final Pattern SYNC_DATA_PATTERN = Pattern.compile("/sync/(\\d+)/?(\\d+)?/?(\\d+)?$");
    private static final String SYNC = "sync";

    private long sequenceNumber;
    private long nextSequenceNumber;
    private long sentTimestamp;

    public ProjectilesSyncName(long gameId,
                          String playerName) {
        super(gameId, playerName);
        sequenceNumber = 0;
        nextSequenceNumber = 0;
        sentTimestamp = 0;
    }

    public ProjectilesSyncName(Interest interest) {
        super(interest);
        parse(super.remainder);
    }

    public ProjectilesSyncName(Data data) {
        super(data);
        parse(super.remainder);
    }



    @Override
    public Name getAsPrefix() {
        return super.getAsPrefix().append(SYNC);
    }

    @Override
    public Name getFullName() {
        return getAsPrefix()
                .append(String.valueOf(sequenceNumber))
                .append(String.valueOf(nextSequenceNumber))
                .append(String.valueOf(sentTimestamp));
    }

    @Override
    public Interest buildInterest() {
        return new Interest(getAsPrefix().append(String.valueOf(nextSequenceNumber)));
    }

    @Override
    public long getLatestSequenceNumberSeen() {
        return sequenceNumber;
    }

    @Override
    public void setNextSequenceNumber(long nextSequenceNumber) {
        this.nextSequenceNumber = nextSequenceNumber;
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
        Matcher matcher = BaseName.matchOrThrow(remainder, SYNC_DATA_PATTERN);
        sequenceNumber = Long.valueOf(matcher.group(1));

        if (matcher.group(2) != null) {
            nextSequenceNumber = Long.valueOf(matcher.group(2));
        }

        if (matcher.group(3) != null) {
            sentTimestamp = Long.valueOf(matcher.group(3));
        }
    }
}
