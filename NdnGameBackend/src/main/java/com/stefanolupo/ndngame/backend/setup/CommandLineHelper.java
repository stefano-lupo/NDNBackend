package com.stefanolupo.ndngame.backend.setup;

import com.stefanolupo.ndngame.config.Config;
import org.apache.commons.cli.*;

public class CommandLineHelper {

    private final Option NAME_OF_PLAYER;
    private final Option AUTOMATED_MODE;
    private final Option GAME_ID;

    private final Options options;

    public CommandLineHelper() {
        options = new Options();

        NAME_OF_PLAYER = new Option("n", "name", true, "name of player");
        NAME_OF_PLAYER.setRequired(true);
        NAME_OF_PLAYER.setType(String.class);
        options.addOption(NAME_OF_PLAYER);

        AUTOMATED_MODE = new Option("a", "automate", false, "use automated mode");
        AUTOMATED_MODE.setRequired(false);
        options.addOption(AUTOMATED_MODE);

        GAME_ID = new Option("g", "gameid", true, "id of game to join");
        GAME_ID.setRequired(false);
        GAME_ID.setType(Long.class);
        options.addOption(GAME_ID);
    }

    public Config getConfig(String[] args) {
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);

            String playerName = cmd.getOptionValue(NAME_OF_PLAYER.getLongOpt());

            Config.Builder builder = Config.builder()
                    .setPlayerName(playerName)
                    .setIsAutomated(cmd.hasOption(AUTOMATED_MODE.getOpt()));

            if (cmd.hasOption(GAME_ID.getLongOpt())) {
                builder.setGameId(Long.valueOf(cmd.getOptionValue(GAME_ID.getLongOpt())));
            }

            return builder.build();
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("ndngame", options);
            throw new RuntimeException("Unable to parse command line arguments", e);
        }
    }
}
