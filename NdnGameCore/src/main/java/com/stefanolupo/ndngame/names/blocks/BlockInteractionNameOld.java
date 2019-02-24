//package com.stefanolupo.ndngame.names;
//
//import com.google.common.base.Preconditions;
//import net.named_data.jndn.Interest;
//import net.named_data.jndn.Name;
//
//import java.util.regex.Pattern;
//
///**
// * Schema: |base_name|/|game_id|/|name|/blocks/interact/|block_id|
// */
//public class BlockInteractionName extends BaseName {
//
//    private static final String LISTEN_FORMAT = GAME_BASE_NAME + "/%d/%s/blocks/interact";
//    private static final Pattern PATTERN = Pattern.compile("\\d/[a-z]+/blocks/interact/.*");
//
//    private String playerName;
//    private long gameId;
//    private String blockId;
//
//    public BlockInteractionName(long gameId, String playerName, String blockId) {
//        super(String.valueOf(gameId), playerName, blockId);
//        this.playerName = playerName;
//        this.gameId = gameId;
//        this.blockId = blockId;
//    }
//
//    public BlockInteractionName(long gameId, String playerName) {
//        super(String.valueOf(gameId), playerName);
//        this.gameId = gameId;
//        this.playerName = playerName;
//    }
//
//    public BlockInteractionName(Interest interest) {
//        super(interest.getName());
//        parse();
//    }
//
//    public Interest toInterest() {
//        return new Interest(getListenPrefix().append(String.valueOf(blockId)));
//    }
//
//    public Name getListenPrefix() {
//        return new Name(String.format(LISTEN_FORMAT, gameId, playerName));
//    }
//
//    public String getBlockId() {
//        return blockId;
//    }
//
//    private void parse() {
//        int numTailComponents = 5;
//        Preconditions.checkArgument(tailName.size() == numTailComponents,
//               "Invalid tailName: %s, should have {} components", tailName, numTailComponents);
//        matchOrThrow(tailName, PATTERN);
//
//        gameId = Long.valueOf(tailName.get(0).toEscapedString());
//        playerName = tailName.getSubName(1).toUri();
//        blockId = tailName.get(4).toEscapedString();
//    }
//}
