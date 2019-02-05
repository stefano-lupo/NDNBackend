package com.stefanolupo.ndngame.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hubspot.immutables.validation.InvalidImmutableStateException;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;
import org.immutables.value.Generated;

/**
 * Immutable implementation of {@link ConfigIF}.
 * <p>
 * Use the builder to create immutable instances:
 * {@code Config.builder()}.
 */
@Generated(from = "ConfigIF", generator = "Immutables")
@SuppressWarnings({"all"})
@SuppressFBWarnings
@ParametersAreNonnullByDefault
@javax.annotation.Generated("org.immutables.processor.ProxyProcessor")
@Immutable
public final class Config implements ConfigIF {
  private final String playerName;
  private final boolean isAutomated;
  private final long gameId;
  private final int width;
  private final int height;

  private Config(Config.Builder builder) {
    this.playerName = builder.playerName;
    if (builder.isAutomatedIsSet()) {
      initShim.setIsAutomated(builder.isAutomated);
    }
    if (builder.gameIdIsSet()) {
      initShim.setGameId(builder.gameId);
    }
    if (builder.widthIsSet()) {
      initShim.setWidth(builder.width);
    }
    if (builder.heightIsSet()) {
      initShim.setHeight(builder.height);
    }
    this.isAutomated = initShim.getIsAutomated();
    this.gameId = initShim.getGameId();
    this.width = initShim.getWidth();
    this.height = initShim.getHeight();
    this.initShim = null;
  }

  private Config(String playerName, boolean isAutomated, long gameId, int width, int height) {
    this.playerName = playerName;
    this.isAutomated = isAutomated;
    this.gameId = gameId;
    this.width = width;
    this.height = height;
    this.initShim = null;
  }

  private static final byte STAGE_INITIALIZING = -1;
  private static final byte STAGE_UNINITIALIZED = 0;
  private static final byte STAGE_INITIALIZED = 1;
  private transient volatile InitShim initShim = new InitShim();

  @Generated(from = "ConfigIF", generator = "Immutables")
  private final class InitShim {
    private byte isAutomatedBuildStage = STAGE_UNINITIALIZED;
    private boolean isAutomated;

    boolean getIsAutomated() {
      if (isAutomatedBuildStage == STAGE_INITIALIZING) throw new InvalidImmutableStateException(formatInitCycleMessage());
      if (isAutomatedBuildStage == STAGE_UNINITIALIZED) {
        isAutomatedBuildStage = STAGE_INITIALIZING;
        this.isAutomated = getIsAutomatedInitialize();
        isAutomatedBuildStage = STAGE_INITIALIZED;
      }
      return this.isAutomated;
    }

    void setIsAutomated(boolean isAutomated) {
      this.isAutomated = isAutomated;
      isAutomatedBuildStage = STAGE_INITIALIZED;
    }

    private byte gameIdBuildStage = STAGE_UNINITIALIZED;
    private long gameId;

    long getGameId() {
      if (gameIdBuildStage == STAGE_INITIALIZING) throw new InvalidImmutableStateException(formatInitCycleMessage());
      if (gameIdBuildStage == STAGE_UNINITIALIZED) {
        gameIdBuildStage = STAGE_INITIALIZING;
        this.gameId = getGameIdInitialize();
        gameIdBuildStage = STAGE_INITIALIZED;
      }
      return this.gameId;
    }

    void setGameId(long gameId) {
      this.gameId = gameId;
      gameIdBuildStage = STAGE_INITIALIZED;
    }

    private byte widthBuildStage = STAGE_UNINITIALIZED;
    private int width;

    int getWidth() {
      if (widthBuildStage == STAGE_INITIALIZING) throw new InvalidImmutableStateException(formatInitCycleMessage());
      if (widthBuildStage == STAGE_UNINITIALIZED) {
        widthBuildStage = STAGE_INITIALIZING;
        this.width = getWidthInitialize();
        widthBuildStage = STAGE_INITIALIZED;
      }
      return this.width;
    }

    void setWidth(int width) {
      this.width = width;
      widthBuildStage = STAGE_INITIALIZED;
    }

    private byte heightBuildStage = STAGE_UNINITIALIZED;
    private int height;

    int getHeight() {
      if (heightBuildStage == STAGE_INITIALIZING) throw new InvalidImmutableStateException(formatInitCycleMessage());
      if (heightBuildStage == STAGE_UNINITIALIZED) {
        heightBuildStage = STAGE_INITIALIZING;
        this.height = getHeightInitialize();
        heightBuildStage = STAGE_INITIALIZED;
      }
      return this.height;
    }

    void setHeight(int height) {
      this.height = height;
      heightBuildStage = STAGE_INITIALIZED;
    }

    private String formatInitCycleMessage() {
      List<String> attributes = new ArrayList<>();
      if (isAutomatedBuildStage == STAGE_INITIALIZING) attributes.add("isAutomated");
      if (gameIdBuildStage == STAGE_INITIALIZING) attributes.add("gameId");
      if (widthBuildStage == STAGE_INITIALIZING) attributes.add("width");
      if (heightBuildStage == STAGE_INITIALIZING) attributes.add("height");
      return "Cannot build Config, attribute initializers form cycle " + attributes;
    }
  }

  private boolean getIsAutomatedInitialize() {
    return ConfigIF.super.getIsAutomated();
  }

  private long getGameIdInitialize() {
    return ConfigIF.super.getGameId();
  }

  private int getWidthInitialize() {
    return ConfigIF.super.getWidth();
  }

  private int getHeightInitialize() {
    return ConfigIF.super.getHeight();
  }

  /**
   * @return The value of the {@code playerName} attribute
   */
  @JsonProperty
  @Override
  public String getPlayerName() {
    return playerName;
  }

  /**
   * @return The value of the {@code isAutomated} attribute
   */
  @JsonProperty
  @Override
  public boolean getIsAutomated() {
    InitShim shim = this.initShim;
    return shim != null
        ? shim.getIsAutomated()
        : this.isAutomated;
  }

  /**
   * @return The value of the {@code gameId} attribute
   */
  @JsonProperty
  @Override
  public long getGameId() {
    InitShim shim = this.initShim;
    return shim != null
        ? shim.getGameId()
        : this.gameId;
  }

  /**
   * @return The value of the {@code width} attribute
   */
  @JsonProperty
  @Override
  public int getWidth() {
    InitShim shim = this.initShim;
    return shim != null
        ? shim.getWidth()
        : this.width;
  }

  /**
   * @return The value of the {@code height} attribute
   */
  @JsonProperty
  @Override
  public int getHeight() {
    InitShim shim = this.initShim;
    return shim != null
        ? shim.getHeight()
        : this.height;
  }

  /**
   * Copy the current immutable object by setting a value for the {@link ConfigIF#getPlayerName() playerName} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for playerName
   * @return A modified copy of the {@code this} object
   */
  public final Config withPlayerName(String value) {
    String newValue = Objects.requireNonNull(value, "playerName");
    if (this.playerName.equals(newValue)) return this;
    return new Config(newValue, this.isAutomated, this.gameId, this.width, this.height);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link ConfigIF#getIsAutomated() isAutomated} attribute.
   * A value equality check is used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for isAutomated
   * @return A modified copy of the {@code this} object
   */
  public final Config withIsAutomated(boolean value) {
    if (this.isAutomated == value) return this;
    return new Config(this.playerName, value, this.gameId, this.width, this.height);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link ConfigIF#getGameId() gameId} attribute.
   * A value equality check is used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for gameId
   * @return A modified copy of the {@code this} object
   */
  public final Config withGameId(long value) {
    if (this.gameId == value) return this;
    return new Config(this.playerName, this.isAutomated, value, this.width, this.height);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link ConfigIF#getWidth() width} attribute.
   * A value equality check is used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for width
   * @return A modified copy of the {@code this} object
   */
  public final Config withWidth(int value) {
    if (this.width == value) return this;
    return new Config(this.playerName, this.isAutomated, this.gameId, value, this.height);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link ConfigIF#getHeight() height} attribute.
   * A value equality check is used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for height
   * @return A modified copy of the {@code this} object
   */
  public final Config withHeight(int value) {
    if (this.height == value) return this;
    return new Config(this.playerName, this.isAutomated, this.gameId, this.width, value);
  }

  /**
   * This instance is equal to all instances of {@code Config} that have equal attribute values.
   * @return {@code true} if {@code this} is equal to {@code another} instance
   */
  @Override
  public boolean equals(@Nullable Object another) {
    if (this == another) return true;
    return another instanceof Config
        && equalTo((Config) another);
  }

  private boolean equalTo(Config another) {
    return playerName.equals(another.playerName)
        && isAutomated == another.isAutomated
        && gameId == another.gameId
        && width == another.width
        && height == another.height;
  }

  /**
   * Computes a hash code from attributes: {@code playerName}, {@code isAutomated}, {@code gameId}, {@code width}, {@code height}.
   * @return hashCode value
   */
  @Override
  public int hashCode() {
    int h = 5381;
    h += (h << 5) + playerName.hashCode();
    h += (h << 5) + Boolean.hashCode(isAutomated);
    h += (h << 5) + Long.hashCode(gameId);
    h += (h << 5) + width;
    h += (h << 5) + height;
    return h;
  }

  /**
   * Prints the immutable value {@code Config} with attribute values.
   * @return A string representation of the value
   */
  @Override
  public String toString() {
    return "Config{"
        + "playerName=" + playerName
        + ", isAutomated=" + isAutomated
        + ", gameId=" + gameId
        + ", width=" + width
        + ", height=" + height
        + "}";
  }

  /**
   * Utility type used to correctly read immutable object from JSON representation.
   * @deprecated Do not use this type directly, it exists only for the <em>Jackson</em>-binding infrastructure
   */
  @Generated(from = "ConfigIF", generator = "Immutables")
  @Deprecated
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE)
  static final class Json implements ConfigIF {
    @Nullable String playerName;
    boolean isAutomated;
    boolean isAutomatedIsSet;
    long gameId;
    boolean gameIdIsSet;
    int width;
    boolean widthIsSet;
    int height;
    boolean heightIsSet;
    @JsonProperty
    public void setPlayerName(String playerName) {
      this.playerName = playerName;
    }
    @JsonProperty
    public void setIsAutomated(boolean isAutomated) {
      this.isAutomated = isAutomated;
      this.isAutomatedIsSet = true;
    }
    @JsonProperty
    public void setGameId(long gameId) {
      this.gameId = gameId;
      this.gameIdIsSet = true;
    }
    @JsonProperty
    public void setWidth(int width) {
      this.width = width;
      this.widthIsSet = true;
    }
    @JsonProperty
    public void setHeight(int height) {
      this.height = height;
      this.heightIsSet = true;
    }
    @Override
    public String getPlayerName() { throw new UnsupportedOperationException(); }
    @Override
    public boolean getIsAutomated() { throw new UnsupportedOperationException(); }
    @Override
    public long getGameId() { throw new UnsupportedOperationException(); }
    @Override
    public int getWidth() { throw new UnsupportedOperationException(); }
    @Override
    public int getHeight() { throw new UnsupportedOperationException(); }
  }

  /**
   * @param json A JSON-bindable data structure
   * @return An immutable value type
   * @deprecated Do not use this method directly, it exists only for the <em>Jackson</em>-binding infrastructure
   */
  @Deprecated
  @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
  static Config fromJson(Json json) {
    Config.Builder builder = Config.builder();
    if (json.playerName != null) {
      builder.setPlayerName(json.playerName);
    }
    if (json.isAutomatedIsSet) {
      builder.setIsAutomated(json.isAutomated);
    }
    if (json.gameIdIsSet) {
      builder.setGameId(json.gameId);
    }
    if (json.widthIsSet) {
      builder.setWidth(json.width);
    }
    if (json.heightIsSet) {
      builder.setHeight(json.height);
    }
    return builder.build();
  }

  /**
   * Creates an immutable copy of a {@link ConfigIF} value.
   * Uses accessors to get values to initialize the new immutable instance.
   * If an instance is already immutable, it is returned as is.
   * @param instance The instance to copy
   * @return A copied immutable Config instance
   */
  public static Config copyOf(ConfigIF instance) {
    if (instance instanceof Config) {
      return (Config) instance;
    }
    return Config.builder()
        .from(instance)
        .build();
  }

  /**
   * Creates a builder for {@link Config Config}.
   * <pre>
   * Config.builder()
   *    .setPlayerName(String) // required {@link ConfigIF#getPlayerName() playerName}
   *    .setIsAutomated(boolean) // optional {@link ConfigIF#getIsAutomated() isAutomated}
   *    .setGameId(long) // optional {@link ConfigIF#getGameId() gameId}
   *    .setWidth(int) // optional {@link ConfigIF#getWidth() width}
   *    .setHeight(int) // optional {@link ConfigIF#getHeight() height}
   *    .build();
   * </pre>
   * @return A new Config builder
   */
  public static Config.Builder builder() {
    return new Config.Builder();
  }

  /**
   * Builds instances of type {@link Config Config}.
   * Initialize attributes and then invoke the {@link #build()} method to create an
   * immutable instance.
   * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
   * but instead used immediately to create instances.</em>
   */
  @Generated(from = "ConfigIF", generator = "Immutables")
  @NotThreadSafe
  public static final class Builder {
    private static final long INIT_BIT_PLAYER_NAME = 0x1L;
    private static final long OPT_BIT_IS_AUTOMATED = 0x1L;
    private static final long OPT_BIT_GAME_ID = 0x2L;
    private static final long OPT_BIT_WIDTH = 0x4L;
    private static final long OPT_BIT_HEIGHT = 0x8L;
    private long initBits = 0x1L;
    private long optBits;

    private @Nullable String playerName;
    private boolean isAutomated;
    private long gameId;
    private int width;
    private int height;

    private Builder() {
    }

    /**
     * Fill a builder with attribute values from the provided {@code ConfigIF} instance.
     * Regular attribute values will be replaced with those from the given instance.
     * Absent optional values will not replace present values.
     * @param instance The instance from which to copy values
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder from(ConfigIF instance) {
      Objects.requireNonNull(instance, "instance");
      setPlayerName(instance.getPlayerName());
      setIsAutomated(instance.getIsAutomated());
      setGameId(instance.getGameId());
      setWidth(instance.getWidth());
      setHeight(instance.getHeight());
      return this;
    }

    /**
     * Initializes the value for the {@link ConfigIF#getPlayerName() playerName} attribute.
     * @param playerName The value for playerName 
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder setPlayerName(String playerName) {
      this.playerName = Objects.requireNonNull(playerName, "playerName");
      initBits &= ~INIT_BIT_PLAYER_NAME;
      return this;
    }

    /**
     * Initializes the value for the {@link ConfigIF#getIsAutomated() isAutomated} attribute.
     * <p><em>If not set, this attribute will have a default value as returned by the initializer of {@link ConfigIF#getIsAutomated() isAutomated}.</em>
     * @param isAutomated The value for isAutomated 
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder setIsAutomated(boolean isAutomated) {
      this.isAutomated = isAutomated;
      optBits |= OPT_BIT_IS_AUTOMATED;
      return this;
    }

    /**
     * Initializes the value for the {@link ConfigIF#getGameId() gameId} attribute.
     * <p><em>If not set, this attribute will have a default value as returned by the initializer of {@link ConfigIF#getGameId() gameId}.</em>
     * @param gameId The value for gameId 
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder setGameId(long gameId) {
      this.gameId = gameId;
      optBits |= OPT_BIT_GAME_ID;
      return this;
    }

    /**
     * Initializes the value for the {@link ConfigIF#getWidth() width} attribute.
     * <p><em>If not set, this attribute will have a default value as returned by the initializer of {@link ConfigIF#getWidth() width}.</em>
     * @param width The value for width 
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder setWidth(int width) {
      this.width = width;
      optBits |= OPT_BIT_WIDTH;
      return this;
    }

    /**
     * Initializes the value for the {@link ConfigIF#getHeight() height} attribute.
     * <p><em>If not set, this attribute will have a default value as returned by the initializer of {@link ConfigIF#getHeight() height}.</em>
     * @param height The value for height 
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder setHeight(int height) {
      this.height = height;
      optBits |= OPT_BIT_HEIGHT;
      return this;
    }

    /**
     * Builds a new {@link Config Config}.
     * @return An immutable instance of Config
     * @throws com.hubspot.immutables.validation.InvalidImmutableStateException if any required attributes are missing
     */
    public Config build() {
      checkRequiredAttributes();
      return new Config(this);
    }

    private boolean isAutomatedIsSet() {
      return (optBits & OPT_BIT_IS_AUTOMATED) != 0;
    }

    private boolean gameIdIsSet() {
      return (optBits & OPT_BIT_GAME_ID) != 0;
    }

    private boolean widthIsSet() {
      return (optBits & OPT_BIT_WIDTH) != 0;
    }

    private boolean heightIsSet() {
      return (optBits & OPT_BIT_HEIGHT) != 0;
    }

    private boolean playerNameIsSet() {
      return (initBits & INIT_BIT_PLAYER_NAME) == 0;
    }

    private void checkRequiredAttributes() {
      if (initBits != 0) {
        throw new InvalidImmutableStateException(formatRequiredAttributesMessage());
      }
    }

    private String formatRequiredAttributesMessage() {
      List<String> attributes = new ArrayList<>();
      if (!playerNameIsSet()) attributes.add("playerName");
      return "Cannot build Config, some of required attributes are not set " + attributes;
    }
  }
}
