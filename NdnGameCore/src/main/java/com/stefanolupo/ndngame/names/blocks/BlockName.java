//package com.stefanolupo.ndngame.names.blocks;
//
//import com.stefanolupo.ndngame.names.SequenceNumberedName;
//import net.named_data.jndn.Data;
//import net.named_data.jndn.Interest;
//import net.named_data.jndn.Name;
//
//import java.util.regex.Pattern;
//
//import static com.stefanolupo.ndngame.names.BaseName.*;
//
///**
// * Schema
// * Prefix: base_name/|game_id|/|name|/blocks
// *      - Sync: /sync/
// *          - Express: |sequence_number|
// *          - Data: |sequence_number|/\next_sequence_number|
// *      - Interact: /interact/|block_id|
// */
//public class BlockName implements SequenceNumberedName {
//
//    private static final String BASE_NAME = "^/(\\d)/([a-zA-Z0-9]+)/blocks";
//    private static final Pattern SYNC_EXPRESS_PATTERN = Pattern.compile(BASE_NAME + "/sync/(\\d)");
//    private static final Pattern SYNC_DATA_PATTERN = Pattern.compile(BASE_NAME + "/sync/(\\d)/(\\d)$");
//    private static final Pattern INTERACT_EXPRESS_PATTERN = Pattern.compile(BASE_NAME + "/interact/(\\d)$");
//
//    private static final String BLOCKS = "blocks";
//    private static final String SYNC = "sync";
//    private static final String INTERACT = "interact";
//
//    private long gameId;
//    private String playerName;
//    private long sequenceNumber;
//    private long nextSequenceNumber;
//
//    // Eek
//    private String id = null;
//
//    public BlockName(long gameId, String playerName) {
//        buildNonPrefixedName(String.valueOf(gameId), playerName, "blocks", "sync");
//        this.gameId = gameId;
//        this.playerName = playerName;
//        this.sequenceNumber = 0;
//        this.nextSequenceNumber = sequenceNumber;
//    }
//
//    public BlockName(Interest interest) {
//        super(interest.getName());
//        parse();
//    }
//
//    public BlockName(Data data) {
//        super(data.getName());
//        parse();
//    }
//
//    @Override
//    public void setNextSequenceNumber(long nextSequenceNumber) {
//        this.nextSequenceNumber = nextSequenceNumber;
//    }
//
//    /**
//     * Used by subscribers to create interests for the latest sequence number
//     */
//    @Override
//    public Interest buildInterest() {
//
//        // This must NOT contain the nextSequenceNumber
//        // Otherwise we will only get data back for when they match!!
//        Name name = getListenName().append(String.valueOf(nextSequenceNumber));
//        return new Interest(name);
//    }
//
//    /**
//     * Used by producers to register prefix
//     * @return the name to accept interests for
//     */
//    @Override
//    public Name getListenName() {
//        return new Name(GAME_BASE_NAME)
//                .append(String.valueOf(gameId))
//                .append(playerName)
//                .append("blocks")
//                .append("sync");
//    }
//
//    /**
//     * Used by publishers to name their data packets
//     * @return Data packet Name with appropriate sequence numbers
//     */
//    @Override
//    public Name getAsPrefix() {
//        return getListenName().append(String.valueOf(sequenceNumber)).append(String.valueOf(nextSequenceNumber));
//    }
//
//
//    /**
//     * Engine needs this for comparing its entities
//     */
//    @Override
//    public long getLatestSequenceNumberSeen() {
//        return sequenceNumber;
//    }
//
//    public long getGameId() {
//        return gameId;
//    }
//
//    public String getPlayerName() {
//        return playerName;
//    }
//
//
//    private void parse() {
////        Preconditions.checkArgument(tailName.size() == 4);
//        matchOrThrow(tailName, SYNC_DATA_PATTERN);
//
//        gameId = Long.valueOf(tailName.get(0).toEscapedString());
//        playerName = tailName.get(1).toEscapedString();
//        sequenceNumber = Long.valueOf(tailName.get(4).toEscapedString());
//
//        if (tailName.size() == 6) {
//            nextSequenceNumber = Long.valueOf(tailName.get(5).toEscapedString());
//        }
//    }
//
//
//    public void setId(String id) {
//        this.id = id;
//    }
//
//    //TODO: Need to implement
//    public String getId() {
//        return id;
//    }
//
//    @Override
//    public int hashCode() {
//        int hash = 31 * Long.hashCode(gameId) * playerName.hashCode();
//        return id == null ? hash : hash * id.hashCode();
//    }
//
//    @Override
//    public boolean equals(Object obj) {
//        if (!(obj instanceof BlockName)) {
//            return false;
//        }
//
//        BlockName other = (BlockName) obj;
//        return other.playerName.equals(playerName) &&
//                other.gameId == gameId &&
//                stringCompareWithNull(other.getId(), id);
//    }
//
//    private boolean stringCompareWithNull(String s1, String s2) {
//
//        if (s1 == null && s2 == null) return true;
//
//        if (s1 == null || s2 == null) return false;
//
//        return s1.equals(s2);
//    }
//}
