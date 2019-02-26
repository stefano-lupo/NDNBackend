package com.stefanolupo.ndngame.backend.setup;

import com.stefanolupo.ndngame.config.LocalConfig;
import org.apache.commons.cli.*;

public class CommandLineHelper {

    private final Option NAME_OF_PLAYER;
    private final Option AUTOMATED_MODE;
    private final Option IS_MASTER_VIEW;
    private final Option GAME_ID;
    private final Option SCREEN_WIDTH;
    private final Option SCREEN_HEIGHT;

    private final Options options;

    public CommandLineHelper() {
        options = new Options();

        NAME_OF_PLAYER = new Option("n", "name", true, "name of player");
        NAME_OF_PLAYER.setRequired(true);
        NAME_OF_PLAYER.setType(String.class);
        options.addOption(NAME_OF_PLAYER);

        AUTOMATED_MODE = new Option("a", "automate", false, "use automated mode");
        AUTOMATED_MODE.setRequired(false);
        AUTOMATED_MODE.setType(Boolean.class);
        options.addOption(AUTOMATED_MODE);


        IS_MASTER_VIEW = new Option("m", "masterview", false, "master view switch");
        IS_MASTER_VIEW.setRequired(false);
        IS_MASTER_VIEW.setType(Boolean.class);
        options.addOption(IS_MASTER_VIEW);

        GAME_ID = new Option("g", "gameid", true, "id of game to join");
        GAME_ID.setRequired(false);
        GAME_ID.setType(Long.class);
        options.addOption(GAME_ID);

        SCREEN_WIDTH = new Option("w", "width", true, "screen width");
        SCREEN_WIDTH.setRequired(false);
        SCREEN_WIDTH.setType(Integer.class);
        options.addOption(SCREEN_WIDTH);

        SCREEN_HEIGHT = new Option("h", "height", true, "screen height");
        SCREEN_HEIGHT.setRequired(false);
        SCREEN_HEIGHT.setType(Integer.class);
        options.addOption(SCREEN_HEIGHT);

    }

    public LocalConfig getConfig(String[] args) {
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);

            String playerName = cmd.getOptionValue(NAME_OF_PLAYER.getLongOpt());

            LocalConfig.Builder builder = LocalConfig.builder()
                    .setPlayerName(playerName)
                    .isMasterView(cmd.hasOption(IS_MASTER_VIEW.getOpt()))
                    .setIsAutomated(cmd.hasOption(AUTOMATED_MODE.getOpt()));

            if (cmd.hasOption(GAME_ID.getLongOpt())) {
                builder.setGameId(Long.valueOf(cmd.getOptionValue(GAME_ID.getLongOpt())));
            }

            if (cmd.hasOption(SCREEN_WIDTH.getLongOpt())) {
                builder.setScreenWidth(Integer.valueOf(cmd.getOptionValue(SCREEN_WIDTH.getLongOpt())));
            }

            if (cmd.hasOption(SCREEN_HEIGHT.getLongOpt())) {
                builder.setScreenHeight(Integer.valueOf(cmd.getOptionValue(SCREEN_HEIGHT.getLongOpt())));
            }

            return builder.build();
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("ndngame", options);
            throw new RuntimeException("Unable to parse command line arguments", e);
        }
    }
}
