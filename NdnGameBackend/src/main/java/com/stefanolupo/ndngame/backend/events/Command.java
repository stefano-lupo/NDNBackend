package com.stefanolupo.ndngame.backend.events;

import java.util.Arrays;

public enum Command {
    MOVE_RIGHT('d', CommandType.MOVE),
    MOVE_LEFT('a', CommandType.MOVE),
    MOVE_UP('w', CommandType.MOVE),
    MOVE_DOWN('s', CommandType.MOVE),
    SHOOT(' ', CommandType.INTERACT),
    UNSUPPORTED('\\', CommandType.UNSUPPORTED);

    private final char c;
    private final CommandType commandType;

    Command(char c, CommandType commandType) {
        this.c = c;
        this.commandType = commandType;
    }

    public char getChar() {
        return c;
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public static Command fromChar(char c) {
        return Arrays.stream(values())
            .filter(command -> c == command.getChar())
            .findFirst()
            .orElse(Command.UNSUPPORTED);
    }


    @Override
    public String toString() {
        return String.format("%s - %s - %s", name(), commandType, c);
    }
}
