//package com.stefanolupo.ndngame.names;
//
//import com.google.common.base.Preconditions;
//import net.named_data.jndn.Interest;
//import net.named_data.jndn.Name;
//
//import java.util.regex.Pattern;
//
///**
// * Schema: base_name/|game_id|/|name|/blocks/|sequence_number|
// */
//public class BlockName extends BaseName implements HasNameWithSequenceNumber {
//
//    private static final String BLOCK_LISTEN_PREFIX = GAME_BASE_NAME + "/%d/%s/blocks/";
//    private static final Pattern NAME_PATTERN = Pattern.compile("/\\d+/[a-z]+/blocks/\\d+");
//
//    private long gameId;
//    private String playerName;
//    private long sequenceNumber;
//
//    public BlockName(long gameId, String playerName) {
//        super(String.valueOf(gameId), playerName, "blocks");
//        this.gameId = gameId;
//        this.playerName = playerName;
//        this.sequenceNumber = 0;
//    }
//
//    public BlockName(Interest interest) {
//        super(interest.getName());
//        parse();
//    }
//
//    public static Name getListenName(long gameId, String playerName) {
//        return new Name(String.format(BLOCK_LISTEN_PREFIX, gameId, playerName));
//    }
//
//    @Override
//    public Interest toInterest() {
//        return new Interest(getListenName(gameId, playerName).append(String.valueOf(sequenceNumber)));
//    }
//
//    @Override
//    public Name getNameWithSequenceNumber() {
//        // TODO: There is actually a away to use NDN naming convention sequence number
//        // See Name.appendSequenceNumber()
//        return getNameWithoutSequenceNumber().append(String.valueOf(sequenceNumber));
//    }
//
//    @Override
//    public Name getNameWithoutSequenceNumber() {
//        return getListenName(gameId, playerName);
//    }
//
////    @Override
////    public void setSequenceNumber(long sequenceNumber) {
////        this.sequenceNumber = sequenceNumber;
////    }
//
//    @Override
//    public long getSequenceNumber() {
//        return sequenceNumber;
//    }
//
//    private void parse() {
//        Preconditions.checkArgument(tailName.size() == 4);
//        checkMatchesRegex(tailName, NAME_PATTERN);
//
//        gameId = Long.valueOf(tailName.get(0).toEscapedString());
//        playerName = tailName.get(1).toEscapedString();
//        sequenceNumber = Long.valueOf(tailName.get(3).toEscapedString());
//    }
//}
