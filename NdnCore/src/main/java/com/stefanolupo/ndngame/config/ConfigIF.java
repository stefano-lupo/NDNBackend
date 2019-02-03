package com.stefanolupo.ndngame.config;

import com.hubspot.immutables.style.HubSpotStyle;
import org.immutables.value.Value;

@Value.Immutable
@HubSpotStyle
public interface ConfigIF {
   String getPlayerName();

   @Value.Default
   default boolean getIsAutomated() {
      return false;
   }

   @Value.Default
   default long getGameId() {
      return 0;
   }

   @Value.Default
   default int getWidth() {
      return 500;
   }

   @Value.Default
   default int getHeight() {
      return 500;
   }
}