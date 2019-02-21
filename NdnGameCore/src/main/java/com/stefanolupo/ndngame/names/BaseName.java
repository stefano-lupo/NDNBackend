package com.stefanolupo.ndngame.names;

import com.google.common.base.Preconditions;
import net.named_data.jndn.Name;

import java.util.regex.Pattern;

public abstract class BaseName {

    public static final Name GAME_BASE_NAME = new Name("/com/stefanolupo/ndngame");

    protected static final Pattern BASE_REGEX = Pattern.compile("^/com/stefanolupo/ndngame");

    protected final Name fullName;
    protected final Name tailName;

    protected BaseName(String name) {
        this(new Name(GAME_BASE_NAME).append(name));
    }

    protected BaseName(Name name) {
        checkMatchesRegex(name, BASE_REGEX);
        fullName = new Name(name);
        tailName = getTailNameFromFullName(fullName);
    }

    protected BaseName(String... args) {
        tailName = buildFromComponents(args);
        fullName = new Name(GAME_BASE_NAME).append(tailName);
        checkMatchesRegex(fullName, BASE_REGEX);
    }

    protected Name buildFromComponents(String... args) {
        Name name = new Name();
        for (String arg : args) {
            name = name.append(arg);
        }
        return name;
    }

    protected void checkMatchesRegex(Name name, Pattern regex) {
        Preconditions.checkArgument(regex.matcher(name.toUri()).find(),
                "Encountered interest which didn't match provided regex '%s': %s", regex, name);
    }

    protected long getLongFromComponent(Name.Component component) {
        return Long.valueOf(component.toEscapedString());
    }

    private Name getTailNameFromFullName(Name fullName) {
        return fullName.getSubName(GAME_BASE_NAME.size());
    }
}
