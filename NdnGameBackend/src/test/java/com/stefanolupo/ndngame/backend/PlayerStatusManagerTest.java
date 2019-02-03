package com.stefanolupo.ndngame.backend;

import com.stefanolupo.ndngame.backend.chronosynced.PlayerStatusManager;
import com.stefanolupo.ndngame.backend.entities.players.LocalPlayer;
import net.named_data.jndn.sync.ChronoSync2013;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.spy;

// TODO: I need dependency injection to test this.
// Once PlayerStatusManager useses DI, just spy on the face and assert method calls with params
// Just debug viewing for now..
@RunWith(MockitoJUnitRunner.class)
public class PlayerStatusManagerTest {

    private static final long GAME_ID = 0;
    private static final String SELF_NAME = "test";
    private static final String DATA_PREFIX_FORMAT = "/com/stefanolupo/ndngame/0/%s/status";
    private static final String SELF_DATA_PREFIX = String.format(DATA_PREFIX_FORMAT, SELF_NAME);
    private static final long SESSION = 0;

    private  ChronoSync2013.SyncState selfSyncState = new ChronoSync2013.SyncState(SELF_DATA_PREFIX, SESSION, 0, null);

    private PlayerStatusManager playerStatusManager;

    @Before
    public void setup() {
        spy(playerStatusManager = new PlayerStatusManager(new LocalPlayer(SELF_NAME),  GAME_ID));
    }

    @Test
    public void itShouldIngoreSelf() {
        List<ChronoSync2013.SyncState> syncStates = new ArrayList<>();
        syncStates.add(selfSyncState);
        playerStatusManager.onReceivedSyncState(syncStates, false);
    }

    @Test
    public void itShouldChooseSyncStateWithLatestSequenceNumber() {
        List<ChronoSync2013.SyncState> syncStates = new ArrayList<>();
        syncStates.add(new ChronoSync2013.SyncState(String.format(DATA_PREFIX_FORMAT, "testtwo"), SESSION, 0, null));
        syncStates.add(new ChronoSync2013.SyncState(String.format(DATA_PREFIX_FORMAT, "testtwo"), SESSION, 1, null));

        playerStatusManager.onReceivedSyncState(syncStates, false);
    }

    @Test
    public void itShouldExpressNewestInterestForEachNode() {
        List<ChronoSync2013.SyncState> syncStates = new ArrayList<>();
        syncStates.add(new ChronoSync2013.SyncState(String.format(DATA_PREFIX_FORMAT, "testtwo"), SESSION, 0, null));
        syncStates.add(new ChronoSync2013.SyncState(String.format(DATA_PREFIX_FORMAT, "testtwo"), SESSION, 1, null));
        syncStates.add(new ChronoSync2013.SyncState(String.format(DATA_PREFIX_FORMAT, "testthree"), SESSION, 0, null));
        syncStates.add(new ChronoSync2013.SyncState(String.format(DATA_PREFIX_FORMAT, "testthree"), SESSION, 1, null));

        playerStatusManager.onReceivedSyncState(syncStates, false);
    }
}
