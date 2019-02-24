package com.stefanolupo.ndngame.names;

import net.named_data.jndn.Data;
import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlayerName implements AsPrefix {
    public static final String PLAYER_NAME_REGEX = "/([a-zA-Z0-9]+)";
    private static final Pattern PATTERN = Pattern.compile("^" + PLAYER_NAME_REGEX + "(.*)");

    private String remainder;
    private String playerName;
    private BaseName baseName;

    public PlayerName(long gameId, String playerName) {
        this.baseName = new BaseName(gameId);
        this.playerName = playerName;
    }

    public PlayerName(Interest interest) {
        this.baseName = BaseName.parseFrom(interest);
        parse(baseName.getRemainder());
    }

    public PlayerName(Data data) {
        baseName = BaseName.parseFrom(data);
        parse(baseName.getRemainder());
    }

    @Override
    public Name getAsPrefix() {
        return baseName.getAsPrefix().append(playerName);
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getRemainder() {
        return remainder;
    }

    private void parse(String remainder) {
        Matcher matcher = BaseName.matchOrThrow(remainder, PATTERN);
        playerName = matcher.group(1);
        this.remainder = matcher.group(2);
    }

    @Override
    public int hashCode() {
        return 31 * playerName.hashCode() * baseName.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PlayerName)) {
            return false;
        }

        PlayerName name = (PlayerName) obj;
        return getAsPrefix().equals(name.getAsPrefix());
    }
}
