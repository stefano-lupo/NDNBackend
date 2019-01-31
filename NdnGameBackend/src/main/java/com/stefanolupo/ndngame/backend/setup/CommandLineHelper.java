package com.stefanolupo.ndngame.backend.setup;

import com.stefanolupo.ndngame.backend.Backend;
import org.apache.commons.cli.*;

public class CommandLineHelper {

    public final Option NAME_OF_PLAYER;
    public final Option AUTOMATED_MODE;
    public final Option GAME_ID;

    private final Options options;

    public CommandLineHelper() {
        options = new Options();

        NAME_OF_PLAYER = new Option("n", "name", true, "name of player");
        NAME_OF_PLAYER.setRequired(true);
        options.addOption(NAME_OF_PLAYER);

        AUTOMATED_MODE = new Option("a", "automate", false, "use automated mode");
        AUTOMATED_MODE.setRequired(false);
        options.addOption(AUTOMATED_MODE);

        GAME_ID = new Option("g", "gameid", true, "id of game to join");
        GAME_ID.setRequired(false);
        options.addOption(GAME_ID);
    }

    public Backend.Builder getBackendBuilder(String[] args) {
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);

            Backend.Builder builder = new Backend.Builder();
            String playerName = cmd.getOptionValue(NAME_OF_PLAYER.getLongOpt());
            boolean automate = cmd.hasOption(AUTOMATED_MODE.getOpt());
            long gameId = cmd.hasOption(GAME_ID.getLongOpt()) ?
                    Long.valueOf(cmd.getOptionValue(GAME_ID.getLongOpt()))
                    : 0;

            return builder
                    .playerName(playerName)
                    .automatePlayer(automate)
                    .gameId(gameId);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("ndngame", options);
            throw new RuntimeException("Unable to parse command line arguments", e);
        }
    }
}
