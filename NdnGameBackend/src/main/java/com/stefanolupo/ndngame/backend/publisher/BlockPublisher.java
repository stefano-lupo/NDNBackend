//package com.stefanolupo.ndngame.backend.publisher;
//
//import com.google.inject.Inject;
//import com.stefanolupo.ndngame.config.Config;
//import com.stefanolupo.ndngame.protos.Block;
//import net.named_data.jndn.Name;
//
//import java.util.Map;
//
//public class BlockPublisher{
//
//    private static final String BLOCK_SYNC_NAME = "/com/stefanolupo/ndngame/%d/%s/blocks";
//
//    @Inject
//    public BlockPublisher(Config config) {
//        super(syncName, entity);
//    }
//
//    private static Name getSyncName(Config config) {
//        return new Name(String.format(BLOCK_SYNC_NAME, config.getGameId(), config.getPlayerName()));
//    }
//}
