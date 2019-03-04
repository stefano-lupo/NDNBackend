package com.stefanolupo.ndngame.names.projectiles;

import com.stefanolupo.ndngame.names.AsPrefix;
import com.stefanolupo.ndngame.names.BaseName;
import com.stefanolupo.ndngame.names.PlayerName;
import net.named_data.jndn.Data;
import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Schema
 * Prefix: /|name|/projectiles

 *      - Interact: /interact/
 *          - Express: |projectile_id|
 */
public class ProjectilesName implements AsPrefix {

    protected static final String PROJECTILES_BASE_NAME = "^/projectiles(.*)";
    private static final Pattern PROJECTILES_NAME_PATTERN = Pattern.compile(PROJECTILES_BASE_NAME);
    private static final String PROJECTILES = "projectiles";

    protected String remainder;
    private final PlayerName playerName;

    protected ProjectilesName(long gameId, String playerName) {
        this.playerName = new PlayerName(gameId, playerName);
    }

    protected ProjectilesName(PlayerName playerName) {
        this.playerName = playerName;
    }

    protected ProjectilesName(Interest interest) {
        playerName = new PlayerName(interest);
        parse(playerName.getRemainder());
    }

    protected ProjectilesName(Data data) {
        playerName = new PlayerName(data);
        parse(playerName.getRemainder());
    }

    public PlayerName getPlayerName() {
        return playerName;
    }

    @Override
    public Name getAsPrefix() {
        return playerName.getAsPrefix().append(PROJECTILES);
    }

    private void parse(String postfix) {
        Matcher matcher = BaseName.matchOrThrow(postfix, PROJECTILES_NAME_PATTERN);
        remainder = matcher.group(1);
    }


    @Override
    public int hashCode() {
        return 31 * playerName.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ProjectilesName)) {
            return false;
        }

        ProjectilesName other = (ProjectilesName) obj;
        return other.playerName.equals(playerName);
    }
}
