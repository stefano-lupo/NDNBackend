package com.stefanolupo.ndngame.config;

public class Config {

    private final String playerName;
    private final boolean isMasterView;
    private final boolean isAutomated;
    private final long gameId;

    private Config(String playerName, boolean isAutomated, long gameId, boolean isMasterView) {
        this.playerName = playerName;
        this.isAutomated = isAutomated;
        this.gameId = gameId;
        this.isMasterView = isMasterView;
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

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String playerName;
        private boolean isAutomated = false;
        private long gameId = 0;
        private boolean isMasterView = false;

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

        public Builder isMasterView(boolean isMasterView) {
            this.isMasterView = isMasterView;
            return this;
        }

        public Config build() {
            return new Config(playerName,
                    isAutomated,
                    gameId,
                    isMasterView);
        }

    }
}
