package com.stefanolupo.ndngame.backend.chronosynced;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.protobuf.InvalidProtocolBufferException;
import com.stefanolupo.ndngame.config.Config;
import com.stefanolupo.ndngame.names.AttackName;
import com.stefanolupo.ndngame.protos.Attack;
import com.stefanolupo.ndngame.protos.ID;
import net.named_data.jndn.Data;
import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;
import net.named_data.jndn.sync.ChronoSync2013;
import net.named_data.jndn.util.Blob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

@Singleton
public class AttackManager extends ChronoSyncedDataStructure {

    private static final Logger LOG = LoggerFactory.getLogger(AttackManager.class);
    private static final Name BROADCAST_PREFIX = new Name("/com/stefanolupo/ndngame/%d/attack/broadcast");

    private final ArrayListMultimap<AttackName, Attack> unprocessedAttacks = ArrayListMultimap.create();
    private final Map<ID, Attack> localRecentAttacks = new HashMap<>();

    private final Config config;

    @Inject
    public AttackManager(Config config) {
        super(BROADCAST_PREFIX, new AttackName(config.getGameId(), config.getPlayerName()).getListenName());
        this.config = config;
    }

    @Override
    protected Optional<Blob> localToBlob(Interest interest) {
        AttackName attackName = new AttackName(interest);
        if (!localRecentAttacks.containsKey(attackName)) {
            return Optional.empty();
        }

        return Optional.of(new Blob(localRecentAttacks.get(attackName).toByteArray()));

    }

    @Override
    protected Collection<Interest> syncStatesToInterests(List<ChronoSync2013.SyncState> syncStates, boolean isRecovery) {
        return syncStates.stream()
                .map(AttackName::new)
                .filter(name -> !name.getPlayerName().equals(config.getPlayerName()))
                .map(AttackName::toInterest)
                .collect(Collectors.toList());
    }

    @Override
    public void onData(Interest interest, Data data) {
        try {
            Attack attack = Attack.parseFrom(data.getContent().getImmutableArray());
            AttackName attackName = new AttackName(interest);
            unprocessedAttacks.put(attackName, attack);
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }
    }

    public void addLocalAttack(Attack attack) {
        localRecentAttacks.put(attack.getId(), attack);
    }

    public Multimap<AttackName, Attack> getUnprocessedAttacks() {
        return unprocessedAttacks;
    }

    public List<Attack> getUnprocessedAttacks(AttackName attackName) {
        return Collections.unmodifiableList(unprocessedAttacks.get(attackName));
    }
}
