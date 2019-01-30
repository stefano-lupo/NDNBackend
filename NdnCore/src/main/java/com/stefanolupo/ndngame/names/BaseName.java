package com.stefanolupo.ndngame.names;

import com.google.common.base.Preconditions;
import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;

import java.util.regex.Pattern;

public class BaseName {

    public static final Name GAME_BASE_NAME = new Name("/com/stefanolupo/ndngame");

    protected static final Pattern BASE_REGEX = Pattern.compile("^/com/stefanolupo/ndngame");

    protected final Name fullName;
    protected final Name tailName;

    protected BaseName(Interest interest) {
        this(interest, BASE_REGEX);
    }

    protected BaseName(Interest interest, Pattern regex) {
        Preconditions.checkArgument(regex.matcher(interest.getName().toUri()).find(),
                "Encountered interest which didn't match provided regex '%s': %s", regex, interest.getName().toUri());
        fullName = interest.getName();
        tailName = getTailName(fullName);
    }

    protected BaseName(String... args) {
        tailName = buildFromComponents(args);
        fullName = new Name(GAME_BASE_NAME).append(tailName);
    }

    protected Name buildFromComponents(String... args) {
        Name name = new Name();
        for (String arg : args) {
            name = name.append(arg);
        }
        return name;
    }

    private Name getTailName(Name fullName) {
        return fullName.getSubName(GAME_BASE_NAME.size());
    }

    public Name getTailName() {
        return tailName;
    }

    public Name getFullName() {
        return fullName;
    }

    public static void main(String[] args) {
        System.out.println(GAME_BASE_NAME.size());
    }
}
