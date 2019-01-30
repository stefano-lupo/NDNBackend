package com.stefanolupo.ndngame.names;

import com.google.common.base.Preconditions;
import net.named_data.jndn.Interest;

import java.util.regex.Pattern;

public class PlayerStatusName extends BaseName {

    private static final Pattern NAME_PATTERN = Pattern.compile("/\\d+/[a-z]+/status/\\d+");

    private final long gameId;
    private final String playerName;

    /**
     * Schema: base_name/|game_id|/|player_name|/status/|sequence_number|
     * @param interest
     */
    public PlayerStatusName(Interest interest) {
        super(interest, NAME_PATTERN);
        Preconditions.checkArgument(tailName.size() == 4);

        gameId = Long.valueOf(tailName.get(0).toEscapedString());
        playerName = tailName.get(1).toEscapedString();

    }

    public PlayerStatusName(long gameId, String playerName) {
        super(String.valueOf(gameId), playerName, "status");
        this.gameId = gameId;
        this.playerName = playerName;
    }

    public long getGameId() {
        return gameId;
    }

    public String getPlayerName() {
        return playerName;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PlayerStatusName)) {
            return false;
        }

        PlayerStatusName other = (PlayerStatusName) obj;

        return other.playerName.equals(playerName) &&
                other.gameId == gameId;
    }

    @Override
    public int hashCode() {
        return 31 * playerName.hashCode() + Long.hashCode(gameId);
    }
}
