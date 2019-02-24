package com.stefanolupo.ndngame.names.blocks;

import com.stefanolupo.ndngame.names.AsPrefix;
import com.stefanolupo.ndngame.names.BaseName;
import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BlockInteractName
        extends BlockNameChildren
        implements AsPrefix {

//    public static final String UUID_REGEX = "([a-f0-9]{8}-[a-f0-9]{4}{3}-[a-f0-9]{11})";
    public static final String UUID_REGEX = "(.*)";
    private static final Pattern PATTERN = Pattern.compile("/interact/" + UUID_REGEX + "$");
    private static final String INTERACT = "interact";

    private String id;

    public BlockInteractName(long gameId, String playerName, String id) {
        super(gameId, playerName);
        this.id = id;
    }

    public BlockInteractName(Interest interest) {
        super(interest);
        parse(getRemainder());
    }

    public String getId() {
        return id;
    }

    @Override
    public Name getAsPrefix() {
        return super.getAsPrefix().append(INTERACT);
    }

    public Interest buildInterest() {
        return new Interest(getAsPrefix().append(id));
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
}
