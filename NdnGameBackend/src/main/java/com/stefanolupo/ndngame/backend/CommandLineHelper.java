package com.stefanolupo.ndngame.backend;

import org.apache.commons.cli.*;

public class CommandLineHelper {

    public final Option NAME_OF_PLAYER;
    public final Option AUTOMATED_MODE;

    private final Options options;

    public CommandLineHelper() {
        options = new Options();

        NAME_OF_PLAYER = new Option("n", "name", true, "name of player");
        NAME_OF_PLAYER.setRequired(true);

        AUTOMATED_MODE = new Option("a", "automate", false, "use automated mode");
        AUTOMATED_MODE.setRequired(false);

        options.addOption(NAME_OF_PLAYER);
        options.addOption(AUTOMATED_MODE);
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

            return builder
                    .playerName(playerName)
                    .automatePlayer(automate);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("ndngame", options);
            throw new RuntimeException("Unable to parse command line arguments", e);
        }
    }
}
