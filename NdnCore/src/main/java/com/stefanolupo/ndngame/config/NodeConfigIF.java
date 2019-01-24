package com.stefanolupo.ndngame.config;

import com.hubspot.immutables.style.HubSpotStyle;
import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable
@HubSpotStyle
public interface NodeConfigIF {
    String getName();
    String getExternalIp();
    Optional<String> getInternalIp();
    int getPort();
}
