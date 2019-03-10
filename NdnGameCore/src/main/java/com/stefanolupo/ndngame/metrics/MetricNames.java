package com.stefanolupo.ndngame.metrics;

import com.stefanolupo.ndngame.names.BaseName;
import com.stefanolupo.ndngame.names.PlayerStatusName;
import com.stefanolupo.ndngame.names.blocks.BlocksSyncName;
import com.stefanolupo.ndngame.names.projectiles.ProjectilesSyncName;
import net.named_data.jndn.Name;

public class MetricNames {

    public static String basePublisherQueueTimer(Name listenName) {
        return String.format("pub-queue-time-%s", trimAndSanitize(listenName));
    }

    public static String basePublisherQueueSize(Name name) {
        return String.format("pub-queue-size-%s", trimAndSanitize(name));
    }

    public static String playerStatusSyncLatency(PlayerStatusName playerName) {
        return String.format("sub-status-latency-%s", playerName.getPlayerName().getName());
    }

    public static String blockNameSyncLatency(BlocksSyncName name) {
        return String.format("sub-block-latency-%s", name.getPlayerName().getName());
    }

    public static String projectileSyncLatency(ProjectilesSyncName name) {
        return String.format("sub-projectile-latency-%s", name.getPlayerName().getName());
    }

    private static String trimAndSanitize(Name name) {
        String s = BaseName.trimBaseName(name).replace("/", "-");
        if (s.charAt(0) == '-') {
            s = s.substring(1);
        }

        return s;
    }
}
