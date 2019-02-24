package com.stefanolupo.ndngame.names;

import com.google.common.base.Preconditions;
import net.named_data.jndn.Data;
import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BaseName implements AsPrefix {

    private static final Name GAME_BASE_NAME = new Name("/com/stefanolupo/ndngame");

    private static final Pattern BASE_REGEX = Pattern.compile("^/com/stefanolupo/ndngame/(\\d+)(.*)");

    private long gameId;
    private String remainder;

    public BaseName(long gameId) {
        this.gameId = gameId;
    }

    private BaseName(long gameId, String remainder) {
        this.gameId = gameId;
        this.remainder = remainder;
    }

    private BaseName(String gameId, String remainder) {
        this(Long.valueOf(gameId), remainder);
    }

    public String getRemainder() {
        Preconditions.checkNotNull(remainder, "Remainder was requested and was null");
        return remainder;
    }

    @Override
    public Name getAsPrefix() {
        return getBaseName().append(String.valueOf(gameId));
    }

    public static Name getBaseName() {
        return new Name(GAME_BASE_NAME);
    }

    public static BaseName parseFrom(Data data) {
        return parseFrom(data.getName());
    }

    public static BaseName parseFrom(Interest interest) {
        return parseFrom(interest.getName());
    }

    public static BaseName parseFrom(Name name) {
        return parseFrom(name.toUri());
    }

    public static BaseName parseFrom(String name) {
        Matcher matcher = matchOrThrow(name, BASE_REGEX);
        return new BaseName(matcher.group(1), matcher.group(2));
    }

//    public static Name buildPrefixedName(String... args) {
//        return GAME_BASE_NAME.append(buildFromComponents(args));
//    }
//
//    public static Name buildNonPrefixedName(String... args) {
//        return buildFromComponents(args);
//    }
//
//    public static long getLongFromComponent(Name.Component component) {
//        return Long.valueOf(component.toEscapedString());
//    }


    public static Matcher matchOrThrow(Name name, Pattern regex) {
        return matchOrThrow(name.toUri(), regex);
    }

    public static Matcher matchOrThrow(String name, Pattern regex) {
        Matcher matcher = regex.matcher(name);
        Preconditions.checkArgument(matcher.matches(),
                "Encountered interest which didn't match provided regex '%s': %s", regex, name);
        return matcher;
    }

//    public static BaseName buildBaseName(String name, Pattern regex) {
//        Matcher matcher = regex.matcher(name);
//        Preconditions.checkArgument(matcher.matches(),
//                "%s didn't match provided regex '%s'", name, regex);
//        return new BaseName(matcher.group(1), matcher.group(2));
//    }
//
//
//    private static Name buildFromComponents(String... args) {
//        Name name = new Name();
//        for (String arg : args) {
//            name = name.append(arg);
//        }
//        return name;
//    }
//
//
//    private static Name getTailNameFromFullName(Name fullName) {
//        return fullName.getSubName(GAME_BASE_NAME.size());
//    }


    @Override
    public int hashCode() {
        return 31 * Long.hashCode(gameId);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BaseName)) {
            return false;
        }

        BaseName baseName = (BaseName) obj;

        return baseName.gameId == gameId;
    }
}
