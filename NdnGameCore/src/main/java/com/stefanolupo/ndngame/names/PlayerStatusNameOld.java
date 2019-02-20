//package com.stefanolupo.ndngame.names;
//
//import net.named_data.jndn.Interest;
//import net.named_data.jndn.Name;
//import net.named_data.jndn.sync.ChronoSync2013;
//
//import java.util.regex.Pattern;
//
///**
// * Schema: base_name/|game_id|/|player_name|/status/|sequence_number|/|next_seqeunce_number|
// */
//public class PlayerStatusNameOld
//        extends BaseName
//        implements HasNameWithSequenceNumber {
//
//    private static final Pattern NAME_PATTERN = Pattern.compile("/\\d+/[a-z]+/status/\\d+/\\d+");
//
//    private long gameId;
//    private String playerName;
//    private long sequenceNumber;
//    private long nextSequenceNumber = sequenceNumber;
//
//    /**
//     * Create a PlayerStatusName using the components
//     * Initialize both sequence numbers to zero
//     * Used on discovery
//     */
//    public PlayerStatusName(long gameId, String playerName) {
//        super(String.valueOf(gameId), playerName, "status");
//        this.gameId = gameId;
//        this.playerName = playerName;
//        this.sequenceNumber = 0;
//        this.nextSequenceNumber = this.sequenceNumber;
//    }
//
//
//    // Update sequence number todo
//
//    /**
//     * Used by subscriber when they want to express an interest
//     */
//    @Override
//    public Interest toInterest() {
//        return new Interest(getExpressInterestName());
//    }
//
//    private Name getExpressInterestName() {
//        return getListenName()
//                .append(String.valueOf(sequenceNumber))
//                .append(String.valueOf(nextSequenceNumber));
//    }
//
//
//
//
//
//    /**
//     * Producer
//     */
//    /**
//     * Create Name from received interest
//     * Used by Producer
//     */
//    public PlayerStatusName(Interest interest) {
//        super(interest.getName());
//        parse();
//    }
//
//
//
//    /**
//     * Used by producers to register prefix
//     * @return the name to accept interests for
//     */
//    public Name getListenName() {
//        return new Name(GAME_BASE_NAME)
//                .append(String.valueOf(gameId))
//                .append(playerName)
//                .append("status");
//    }
//
//
//
//
//
//
//
//    @Override
//    public Name getNameWithSequenceNumber() {
//        return getNameWithoutSequenceNumber().append(String.valueOf(sequenceNumber));
//    }
//
//    @Override
//    public Name getNameWithoutSequenceNumber() {
//        return new Name(GAME_BASE_NAME)
//                .append(String.valueOf(gameId))
//                .append(playerName)
//                .append("status");
//    }
//
//    @Override
//    public void setSequenceNumber(long sequenceNumber) {
//        this.sequenceNumber = sequenceNumber;
//    }
//
//    @Override
//    public long getSequenceNumber() {
//        return sequenceNumber;
//    }
//
//
//
//    public long getGameId() {
//        return gameId;
//    }
//
//    public String getPlayerName() {
//        return playerName;
//    }
//
//    private void parse() {
////        Preconditions.checkArgument(tailName.size() == 4);
//        checkMatchesRegex(tailName, NAME_PATTERN);
//
//        gameId = Long.valueOf(tailName.get(0).toEscapedString());
//        playerName = tailName.get(1).toEscapedString();
//        sequenceNumber = Long.valueOf(tailName.get(3).toEscapedString());
//        nextSequenceNumber = Long.valueOf(tailName.get(4).toEscapedString());
//    }
//
//
//    /**
//     * equals which ignores the sequence number
//     */
//    @Override
//    public boolean equals(Object obj) {
//        if (!(obj instanceof PlayerStatusName)) {
//            return false;
//        }
//
//        PlayerStatusName other = (PlayerStatusName) obj;
//
//        return other.playerName.equals(playerName) &&
//                other.gameId == gameId;
//    }
//
//    /**
//     * hashCode which ignores the sequence number
//     */
//    @Override
//    public int hashCode() {
//        return 31 * playerName.hashCode() + Long.hashCode(gameId);
//    }
//}
