package com.stefanolupo.ndngame.names.blocks;

import com.stefanolupo.ndngame.names.AsPrefix;
import com.stefanolupo.ndngame.names.BaseName;
import com.stefanolupo.ndngame.names.SequenceNumberedName;
import net.named_data.jndn.Data;
import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Schema: /sync/
 *      - Express: |sequence_number|
 *      - Data: |sequence_number|/\next_sequence_number|
 */
public class BlocksSyncName
        extends BlocksName
        implements SequenceNumberedName, AsPrefix {

    private static final Pattern SYNC_EXPRESS_PATTERN = Pattern.compile("/sync/(\\d+)");
    private static final Pattern SYNC_DATA_PATTERN = Pattern.compile("/sync/(\\d+)/?(\\d+)?$");
    private static final String SYNC = "sync";

    private long sequenceNumber;
    private long nextSequenceNumber;

    public BlocksSyncName(long gameId,
                          String playerName) {
        super(gameId, playerName);
        sequenceNumber = 0;
        nextSequenceNumber = 0;
    }

    public BlocksSyncName(Interest interest) {
        super(interest);
        parse(super.getRemainder());
    }

    public BlocksSyncName(Data data) {
        super(data);
        parse(super.getRemainder());
    }

    @Override
    public Name getAsPrefix() {
        return super.getAsPrefix().append(SYNC);
    }

    @Override
    public Name getFullName() {
        return getAsPrefix()
                .append(String.valueOf(sequenceNumber))
                .append(String.valueOf(nextSequenceNumber));
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

    private void parse(String remainder) {
        Matcher matcher = BaseName.matchOrThrow(remainder, SYNC_DATA_PATTERN);
        sequenceNumber = Long.valueOf(matcher.group(1));

        if (matcher.groupCount() > 2) {
            nextSequenceNumber = Long.valueOf(matcher.group(2));
        }
    }
}
