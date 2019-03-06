package com.stefanolupo.ndngame.config;

public class LocalConfig {

    private static final int DEFAULT_SCREEN_WIDTH = 600;
    private static final int DEFAULT_SCREEN_HEIGHT = DEFAULT_SCREEN_WIDTH;

    private final String playerName;
    private final boolean isMasterView;
    private final boolean isAutomated;
    private final String automationType;
    private final boolean isHeadless;
    private final long gameId;
    private final int screenWidth;
    private final int screenHeight;
    private final int targetFrameRate;

    private LocalConfig(String playerName,
                        boolean isAutomated,
                        String automationType,
                        boolean isHeadless,
                        long gameId,
                        boolean isMasterView,
                        int screenWidth,
                        int screenHeight,
                        int targetFrameRate) {
        this.playerName = playerName;
        this.isAutomated = isAutomated;
        this.automationType = automationType;
        this.isHeadless = isHeadless;
        this.gameId = gameId;
        this.isMasterView = isMasterView;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.targetFrameRate = targetFrameRate;
    }

    public String getPlayerName() {
        return playerName;
    }

    public boolean isAutomated() {
        return isAutomated;
    }

    public String getAutomationType() {
        return automationType;
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

    public int getTargetFrameRate() {
        return targetFrameRate;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return "LocalConfig{" +
                "playerName='" + playerName + '\'' +
                ", isMasterView=" + isMasterView +
                ", isAutomated=" + isAutomated +
                ", automationType='" + automationType + '\'' +
                ", isHeadless=" + isHeadless +
                ", gameId=" + gameId +
                ", screenWidth=" + screenWidth +
                ", screenHeight=" + screenHeight +
                ", targetFrameRate=" + targetFrameRate +
                '}';
    }

    public static class Builder {
        private String playerName;
        private boolean isAutomated = false;
        private String automationType = "wsp";
        private boolean isHeadless = false;
        private long gameId = 0;
        private boolean isMasterView = false;
        private int screenWidth = DEFAULT_SCREEN_WIDTH;
        private int screenHeight = DEFAULT_SCREEN_HEIGHT;
        private int targetFrameRate = 60;

        public Builder setPlayerName(String playerName) {
            this.playerName = playerName;
            return this;
        }

        public Builder setIsAutomated(boolean isAutomated) {
            this.isAutomated = isAutomated;
            return this;
        }

        public Builder setAutomationType(String automationType) {
            this.automationType = automationType;
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

        public Builder setScreenWidth(int screenWidth) {
            this.screenWidth = screenWidth;
            return this;
        }

        public Builder setScreenHeight(int screenHeight) {
            this.screenHeight = screenHeight;
            return this;
        }

        public Builder setTargetFrameRate(int targetFrameRate) {
            this.targetFrameRate = targetFrameRate;
            return this;
        }

        public LocalConfig build() {
            return new LocalConfig(playerName,
                    isAutomated,
                    automationType,
                    isHeadless,
                    gameId,
                    isMasterView,
                    screenWidth,
                    screenHeight,
                    targetFrameRate);
        }

    }
}
