package com.stefanolupo.ndngame.config;

public class LocalConfig {

    private static final int DEFAULT_SCREEN_WIDTH = 400;
    private static final int DEFAULT_SCREEN_HEIGHT = DEFAULT_SCREEN_WIDTH;

    private final String playerName;
    private final boolean isMasterView;
    private final boolean isAutomated;
    private final boolean isHeadless;
    private final long gameId;
    private final int screenWidth;
    private final int screenHeight;

    private LocalConfig(String playerName,
                        boolean isAutomated,
                        boolean isHeadless,
                        long gameId,
                        boolean isMasterView,
                        int screenWidth,
                        int screenHeight) {
        this.playerName = playerName;
        this.isAutomated = isAutomated;
        this.isHeadless = isHeadless;
        this.gameId = gameId;
        this.isMasterView = isMasterView;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    public String getPlayerName() {
        return playerName;
    }

    public boolean isAutomated() {
        return isAutomated;
    }

    public boolean isMasterView() {
        return isMasterView;
    }

    public long getGameId() {
        return gameId;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public boolean isHeadless() {
        return isHeadless;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String playerName;
        private boolean isAutomated = false;
        private boolean isHeadless = false;
        private long gameId = 0;
        private boolean isMasterView = false;
        private int screenWidth = DEFAULT_SCREEN_WIDTH;
        private int screenHeight = DEFAULT_SCREEN_HEIGHT;

        public Builder setPlayerName(String playerName) {
            this.playerName = playerName;
            return this;
        }

        public Builder setIsAutomated(boolean isAutomated) {
            this.isAutomated = isAutomated;
            return this;
        }

        public Builder setIsHeadless(boolean isHeadless) {
            this.isHeadless = isHeadless;
            return this;
        }

        public Builder setGameId(long gameId) {
            this.gameId = gameId;
            return this;
        }

        public Builder isMasterView(boolean isMasterView) {
            this.isMasterView = isMasterView;
            return this;
        }

        public void setScreenWidth(int screenWidth) {
            this.screenWidth = screenWidth;
        }

        public void setScreenHeight(int screenHeight) {
            this.screenHeight = screenHeight;
        }

        public LocalConfig build() {
            return new LocalConfig(playerName,
                    isAutomated,
                    isHeadless,
                    gameId,
                    isMasterView,
                    screenWidth,
                    screenHeight);
        }

    }
}
