package com.stefanolupo.ndngame.names.projectiles;

import com.google.common.base.Preconditions;
import com.stefanolupo.ndngame.names.AsPrefix;
import com.stefanolupo.ndngame.names.BaseName;
import com.stefanolupo.ndngame.names.PlayerName;
import net.named_data.jndn.Data;
import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProjectileName extends ProjectilesName implements AsPrefix  {

    //    public static final String UUID_REGEX = "([a-f0-9]{8}-[a-f0-9]{4}{3}-[a-f0-9]{11})";
    public static final String UUID_REGEX = "(.*)";
    private static final Pattern PATTERN = Pattern.compile("/interact/" + UUID_REGEX + "$");
    private static final String INTERACT = "interact";

    private String id;

    public ProjectileName(PlayerName playerName, String id) {
        super(playerName);
        this.id = id;
    }

    public ProjectileName(long gameId, String playerName) {
        this(gameId, playerName, null);
    }

    public ProjectileName(long gameId, String playerName, String id) {
        super(gameId, playerName);
        this.id = id;
    }


    public ProjectileName(Interest interest) {
        super(interest);
        parse(super.remainder);
    }

    public ProjectileName(Data data, String id) {
        super(data);
        this.id = id;
        parse(super.remainder);
    }

    public String getId() {
        Preconditions.checkNotNull(id, "Id was requested when it was null");
        return id;
    }

    @Override
    public Name getAsPrefix() {
        return super.getAsPrefix().append(INTERACT);
    }

    public Interest buildInterest() {
        return new Interest(getAsPrefix()
                .append(id));
    }

    public static ProjectileName fromProjectileSyncNameAndId(ProjectilesSyncName syncName, String id) {
        return new ProjectileName(syncName.getPlayerName(), id);
    }

    private void parse(String remainder) {
        Matcher matcher = BaseName.matchOrThrow(remainder, PATTERN);
        id = matcher.group(1);
    }


    private boolean stringCompareWithNull(String s1, String s2) {
        if (s1 == null && s2 == null) return true;
        if (s1 == null || s2 == null) return false;
        return s1.equals(s2);
    }

    @Override
    public int hashCode() {
        return 31 * (id == null ? 1 : id.hashCode()) * super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ProjectileName)) return false;

        ProjectileName blockName = (ProjectileName) obj;
        return super.equals(blockName) && stringCompareWithNull(blockName.getId(), id);
    }
}
