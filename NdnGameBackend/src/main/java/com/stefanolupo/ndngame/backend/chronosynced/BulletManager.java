//package com.stefanolupo.ndngame.backend.chronosynced;
//
//import com.stefanolupo.ndngame.names.BulletsName;
//import com.stefanolupo.ndngame.protos.Bullet;
//import com.stefanolupo.ndngame.protos.BulletStatus;
//import net.named_data.jndn.Data;
//import net.named_data.jndn.Interest;
//import net.named_data.jndn.Name;
//import net.named_data.jndn.sync.ChronoSync2013;
//import net.named_data.jndn.util.Blob;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//public class BulletManager extends ChronoSyncedDataStructure {
//
//    private final List<BulletStatus> bullets;
//    private int currentSequenceNumber = 0;
//
//    public BulletManager(Name broadcastPrefix, Name dataListenPrefix) {
//        super(broadcastPrefix, dataListenPrefix);
//        bullets = new ArrayList<>();
//    }
//
//    @Override
//    public void onData(Interest interest, Data data) {
//        BulletsName bulletsName = new BulletsName(interest);
//        if (bulletsName.getSequenceNumber() <= currentSequenceNumber) {
//
//        }
//    }
//
////    @Override
////    protected BulletsName interestToKey(Interest interest) {
////        return new BulletsName(interest);
////    }
////
////    @Override
////    protected Bullet dataToVal(Data data, BulletsName key, Bullet oldVal) {
////        try {
////            return Bullet.parseFrom(data.getContent().getImmutableArray());
////        } catch (InvalidProtocolBufferException e) {
////            throw new RuntimeException("Unable to parse incoming bullet from data", e);
////        }
////    }
//
//    @Override
//    protected Optional<Blob> localToBlob(Interest interest) {
//        BulletsName name = new BulletsName(interest);
//        return getMap().containsKey(name) ?
//                Optional.of(new Blob(getMap().get(name).toByteArray())) :
//                Optional.empty();
//    }
//
//    @Override
//    protected Optional<Interest> syncStatesToInterests(List<ChronoSync2013.SyncState> syncStates, boolean isRecovery) {
//        if (isRecovery) {
//            return Optional.empty();
//        }
//
//        ChronoSync2013.SyncState syncState = syncStates.get(syncStates.size() - 1);
//        return Optional.of(new BulletsName(syncState).);
//    }
//}
