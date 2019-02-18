package com.stefanolupo.ndngame.config;

public class Config {

    private final String playerName;
    private final boolean isAutomated;
    private final long gameId;
    private final int width;
    private final int height;

    private Config(String playerName, boolean isAutomated, long gameId, int width, int height) {
        this.playerName = playerName;
        this.isAutomated = isAutomated;
        this.gameId = gameId;
        this.width = width;
        this.height = height;
    }

    public String getPlayerName() {
        return playerName;
    }

    public boolean isAutomated() {
        return isAutomated;
    }

    public long getGameId() {
        return gameId;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String playerName;
        private boolean isAutomated = false;
        private long gameId = 0;
        private int width = 500;
        private int height = 500;

        public Builder setPlayerName(String playerName) {
            this.playerName = playerName;
            return this;
        }

        public Builder setIsAutomated(boolean automated) {
            isAutomated = automated;
            return this;
        }

        public Builder setGameId(long gameId) {
            this.gameId = gameId;
            return this;
        }

        public Builder setWidth(int width) {
            this.width = width;
            return this;
        }

        public Builder setHeight(int height) {
            this.height = height;
            return this;
        }

        public Config build() {
            return new Config(playerName,
                    isAutomated,
                    gameId,
                    width,
                    height);
        }

    }
}
