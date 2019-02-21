package com.stefanolupo.ndngame.backend.chronosynced;

import com.stefanolupo.ndngame.protos.Player;

import java.util.Set;

public interface OnPlayersDiscovered {
    void onPlayersDiscovered(Set<Player> players);
}
