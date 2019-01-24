package com.stefanolupo.ndngame.config;

import com.hubspot.immutables.style.HubSpotStyle;
import org.immutables.value.Value;

import java.util.Collection;

@Value.Immutable
@HubSpotStyle
public interface ConfigIF {
   Collection<com.stefanolupo.ndngame.config.NodeConfig> getNodeConfigs();
}