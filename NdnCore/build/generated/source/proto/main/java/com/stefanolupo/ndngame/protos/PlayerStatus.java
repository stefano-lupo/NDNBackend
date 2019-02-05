// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: NdnGame.proto

package com.stefanolupo.ndngame.protos;

/**
 * Protobuf type {@code PlayerStatus}
 */
public  final class PlayerStatus extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:PlayerStatus)
    PlayerStatusOrBuilder {
  // Use PlayerStatus.newBuilder() to construct.
  private PlayerStatus(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private PlayerStatus() {
    x_ = 0;
    y_ = 0;
    velX_ = 0;
    velY_ = 0;
    hp_ = 0;
    mana_ = 0;
    score_ = 0;
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return com.google.protobuf.UnknownFieldSet.getDefaultInstance();
  }
  private PlayerStatus(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    this();
    int mutable_bitField0_ = 0;
    try {
      boolean done = false;
      while (!done) {
        int tag = input.readTag();
        switch (tag) {
          case 0:
            done = true;
            break;
          default: {
            if (!input.skipField(tag)) {
              done = true;
            }
            break;
          }
          case 8: {

            x_ = input.readInt32();
            break;
          }
          case 16: {

            y_ = input.readInt32();
            break;
          }
          case 24: {

            velX_ = input.readInt32();
            break;
          }
          case 32: {

            velY_ = input.readInt32();
            break;
          }
          case 40: {

            hp_ = input.readInt32();
            break;
          }
          case 48: {

            mana_ = input.readInt32();
            break;
          }
          case 56: {

            score_ = input.readInt32();
            break;
          }
        }
      }
    } catch (com.google.protobuf.InvalidProtocolBufferException e) {
      throw e.setUnfinishedMessage(this);
    } catch (java.io.IOException e) {
      throw new com.google.protobuf.InvalidProtocolBufferException(
          e).setUnfinishedMessage(this);
    } finally {
      makeExtensionsImmutable();
    }
  }
  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return com.stefanolupo.ndngame.protos.NDNGameProtos.internal_static_PlayerStatus_descriptor;
  }

  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return com.stefanolupo.ndngame.protos.NDNGameProtos.internal_static_PlayerStatus_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            com.stefanolupo.ndngame.protos.PlayerStatus.class, com.stefanolupo.ndngame.protos.PlayerStatus.Builder.class);
  }

  public static final int X_FIELD_NUMBER = 1;
  private int x_;
  /**
   * <code>optional int32 x = 1;</code>
   */
  public int getX() {
    return x_;
  }

  public static final int Y_FIELD_NUMBER = 2;
  private int y_;
  /**
   * <code>optional int32 y = 2;</code>
   */
  public int getY() {
    return y_;
  }

  public static final int VELX_FIELD_NUMBER = 3;
  private int velX_;
  /**
   * <code>optional int32 velX = 3;</code>
   */
  public int getVelX() {
    return velX_;
  }

  public static final int VELY_FIELD_NUMBER = 4;
  private int velY_;
  /**
   * <code>optional int32 velY = 4;</code>
   */
  public int getVelY() {
    return velY_;
  }

  public static final int HP_FIELD_NUMBER = 5;
  private int hp_;
  /**
   * <code>optional int32 hp = 5;</code>
   */
  public int getHp() {
    return hp_;
  }

  public static final int MANA_FIELD_NUMBER = 6;
  private int mana_;
  /**
   * <code>optional int32 mana = 6;</code>
   */
  public int getMana() {
    return mana_;
  }

  public static final int SCORE_FIELD_NUMBER = 7;
  private int score_;
  /**
   * <code>optional int32 score = 7;</code>
   */
  public int getScore() {
    return score_;
  }

  private byte memoizedIsInitialized = -1;
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    memoizedIsInitialized = 1;
    return true;
  }

  public void writeTo(com.google.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    if (x_ != 0) {
      output.writeInt32(1, x_);
    }
    if (y_ != 0) {
      output.writeInt32(2, y_);
    }
    if (velX_ != 0) {
      output.writeInt32(3, velX_);
    }
    if (velY_ != 0) {
      output.writeInt32(4, velY_);
    }
    if (hp_ != 0) {
      output.writeInt32(5, hp_);
    }
    if (mana_ != 0) {
      output.writeInt32(6, mana_);
    }
    if (score_ != 0) {
      output.writeInt32(7, score_);
    }
  }

  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (x_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(1, x_);
    }
    if (y_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(2, y_);
    }
    if (velX_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(3, velX_);
    }
    if (velY_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(4, velY_);
    }
    if (hp_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(5, hp_);
    }
    if (mana_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(6, mana_);
    }
    if (score_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(7, score_);
    }
    memoizedSize = size;
    return size;
  }

  private static final long serialVersionUID = 0L;
  @java.lang.Override
  public boolean equals(final java.lang.Object obj) {
    if (obj == this) {
     return true;
    }
    if (!(obj instanceof com.stefanolupo.ndngame.protos.PlayerStatus)) {
      return super.equals(obj);
    }
    com.stefanolupo.ndngame.protos.PlayerStatus other = (com.stefanolupo.ndngame.protos.PlayerStatus) obj;

    boolean result = true;
    result = result && (getX()
        == other.getX());
    result = result && (getY()
        == other.getY());
    result = result && (getVelX()
        == other.getVelX());
    result = result && (getVelY()
        == other.getVelY());
    result = result && (getHp()
        == other.getHp());
    result = result && (getMana()
        == other.getMana());
    result = result && (getScore()
        == other.getScore());
    return result;
  }

  @java.lang.Override
  public int hashCode() {
    if (memoizedHashCode != 0) {
      return memoizedHashCode;
    }
    int hash = 41;
    hash = (19 * hash) + getDescriptorForType().hashCode();
    hash = (37 * hash) + X_FIELD_NUMBER;
    hash = (53 * hash) + getX();
    hash = (37 * hash) + Y_FIELD_NUMBER;
    hash = (53 * hash) + getY();
    hash = (37 * hash) + VELX_FIELD_NUMBER;
    hash = (53 * hash) + getVelX();
    hash = (37 * hash) + VELY_FIELD_NUMBER;
    hash = (53 * hash) + getVelY();
    hash = (37 * hash) + HP_FIELD_NUMBER;
    hash = (53 * hash) + getHp();
    hash = (37 * hash) + MANA_FIELD_NUMBER;
    hash = (53 * hash) + getMana();
    hash = (37 * hash) + SCORE_FIELD_NUMBER;
    hash = (53 * hash) + getScore();
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static com.stefanolupo.ndngame.protos.PlayerStatus parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.stefanolupo.ndngame.protos.PlayerStatus parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.stefanolupo.ndngame.protos.PlayerStatus parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.stefanolupo.ndngame.protos.PlayerStatus parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.stefanolupo.ndngame.protos.PlayerStatus parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.stefanolupo.ndngame.protos.PlayerStatus parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.stefanolupo.ndngame.protos.PlayerStatus parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static com.stefanolupo.ndngame.protos.PlayerStatus parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.stefanolupo.ndngame.protos.PlayerStatus parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.stefanolupo.ndngame.protos.PlayerStatus parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(com.stefanolupo.ndngame.protos.PlayerStatus prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }
  public Builder toBuilder() {
    return this == DEFAULT_INSTANCE
        ? new Builder() : new Builder().mergeFrom(this);
  }

  @java.lang.Override
  protected Builder newBuilderForType(
      com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   * Protobuf type {@code PlayerStatus}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:PlayerStatus)
      com.stefanolupo.ndngame.protos.PlayerStatusOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.stefanolupo.ndngame.protos.NDNGameProtos.internal_static_PlayerStatus_descriptor;
    }

    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.stefanolupo.ndngame.protos.NDNGameProtos.internal_static_PlayerStatus_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.stefanolupo.ndngame.protos.PlayerStatus.class, com.stefanolupo.ndngame.protos.PlayerStatus.Builder.class);
    }

    // Construct using com.stefanolupo.ndngame.protos.PlayerStatus.newBuilder()
    private Builder() {
      maybeForceBuilderInitialization();
    }

    private Builder(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);
      maybeForceBuilderInitialization();
    }
    private void maybeForceBuilderInitialization() {
      if (com.google.protobuf.GeneratedMessageV3
              .alwaysUseFieldBuilders) {
      }
    }
    public Builder clear() {
      super.clear();
      x_ = 0;

      y_ = 0;

      velX_ = 0;

      velY_ = 0;

      hp_ = 0;

      mana_ = 0;

      score_ = 0;

      return this;
    }

    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return com.stefanolupo.ndngame.protos.NDNGameProtos.internal_static_PlayerStatus_descriptor;
    }

    public com.stefanolupo.ndngame.protos.PlayerStatus getDefaultInstanceForType() {
      return com.stefanolupo.ndngame.protos.PlayerStatus.getDefaultInstance();
    }

    public com.stefanolupo.ndngame.protos.PlayerStatus build() {
      com.stefanolupo.ndngame.protos.PlayerStatus result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    public com.stefanolupo.ndngame.protos.PlayerStatus buildPartial() {
      com.stefanolupo.ndngame.protos.PlayerStatus result = new com.stefanolupo.ndngame.protos.PlayerStatus(this);
      result.x_ = x_;
      result.y_ = y_;
      result.velX_ = velX_;
      result.velY_ = velY_;
      result.hp_ = hp_;
      result.mana_ = mana_;
      result.score_ = score_;
      onBuilt();
      return result;
    }

    public Builder clone() {
      return (Builder) super.clone();
    }
    public Builder setField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        Object value) {
      return (Builder) super.setField(field, value);
    }
    public Builder clearField(
        com.google.protobuf.Descriptors.FieldDescriptor field) {
      return (Builder) super.clearField(field);
    }
    public Builder clearOneof(
        com.google.protobuf.Descriptors.OneofDescriptor oneof) {
      return (Builder) super.clearOneof(oneof);
    }
    public Builder setRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        int index, Object value) {
      return (Builder) super.setRepeatedField(field, index, value);
    }
    public Builder addRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        Object value) {
      return (Builder) super.addRepeatedField(field, value);
    }
    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof com.stefanolupo.ndngame.protos.PlayerStatus) {
        return mergeFrom((com.stefanolupo.ndngame.protos.PlayerStatus)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(com.stefanolupo.ndngame.protos.PlayerStatus other) {
      if (other == com.stefanolupo.ndngame.protos.PlayerStatus.getDefaultInstance()) return this;
      if (other.getX() != 0) {
        setX(other.getX());
      }
      if (other.getY() != 0) {
        setY(other.getY());
      }
      if (other.getVelX() != 0) {
        setVelX(other.getVelX());
      }
      if (other.getVelY() != 0) {
        setVelY(other.getVelY());
      }
      if (other.getHp() != 0) {
        setHp(other.getHp());
      }
      if (other.getMana() != 0) {
        setMana(other.getMana());
      }
      if (other.getScore() != 0) {
        setScore(other.getScore());
      }
      onChanged();
      return this;
    }

    public final boolean isInitialized() {
      return true;
    }

    public Builder mergeFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      com.stefanolupo.ndngame.protos.PlayerStatus parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (com.stefanolupo.ndngame.protos.PlayerStatus) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }

    private int x_ ;
    /**
     * <code>optional int32 x = 1;</code>
     */
    public int getX() {
      return x_;
    }
    /**
     * <code>optional int32 x = 1;</code>
     */
    public Builder setX(int value) {
      
      x_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>optional int32 x = 1;</code>
     */
    public Builder clearX() {
      
      x_ = 0;
      onChanged();
      return this;
    }

    private int y_ ;
    /**
     * <code>optional int32 y = 2;</code>
     */
    public int getY() {
      return y_;
    }
    /**
     * <code>optional int32 y = 2;</code>
     */
    public Builder setY(int value) {
      
      y_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>optional int32 y = 2;</code>
     */
    public Builder clearY() {
      
      y_ = 0;
      onChanged();
      return this;
    }

    private int velX_ ;
    /**
     * <code>optional int32 velX = 3;</code>
     */
    public int getVelX() {
      return velX_;
    }
    /**
     * <code>optional int32 velX = 3;</code>
     */
    public Builder setVelX(int value) {
      
      velX_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>optional int32 velX = 3;</code>
     */
    public Builder clearVelX() {
      
      velX_ = 0;
      onChanged();
      return this;
    }

    private int velY_ ;
    /**
     * <code>optional int32 velY = 4;</code>
     */
    public int getVelY() {
      return velY_;
    }
    /**
     * <code>optional int32 velY = 4;</code>
     */
    public Builder setVelY(int value) {
      
      velY_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>optional int32 velY = 4;</code>
     */
    public Builder clearVelY() {
      
      velY_ = 0;
      onChanged();
      return this;
    }

    private int hp_ ;
    /**
     * <code>optional int32 hp = 5;</code>
     */
    public int getHp() {
      return hp_;
    }
    /**
     * <code>optional int32 hp = 5;</code>
     */
    public Builder setHp(int value) {
      
      hp_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>optional int32 hp = 5;</code>
     */
    public Builder clearHp() {
      
      hp_ = 0;
      onChanged();
      return this;
    }

    private int mana_ ;
    /**
     * <code>optional int32 mana = 6;</code>
     */
    public int getMana() {
      return mana_;
    }
    /**
     * <code>optional int32 mana = 6;</code>
     */
    public Builder setMana(int value) {
      
      mana_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>optional int32 mana = 6;</code>
     */
    public Builder clearMana() {
      
      mana_ = 0;
      onChanged();
      return this;
    }

    private int score_ ;
    /**
     * <code>optional int32 score = 7;</code>
     */
    public int getScore() {
      return score_;
    }
    /**
     * <code>optional int32 score = 7;</code>
     */
    public Builder setScore(int value) {
      
      score_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>optional int32 score = 7;</code>
     */
    public Builder clearScore() {
      
      score_ = 0;
      onChanged();
      return this;
    }
    public final Builder setUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return this;
    }

    public final Builder mergeUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return this;
    }


    // @@protoc_insertion_point(builder_scope:PlayerStatus)
  }

  // @@protoc_insertion_point(class_scope:PlayerStatus)
  private static final com.stefanolupo.ndngame.protos.PlayerStatus DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new com.stefanolupo.ndngame.protos.PlayerStatus();
  }

  public static com.stefanolupo.ndngame.protos.PlayerStatus getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<PlayerStatus>
      PARSER = new com.google.protobuf.AbstractParser<PlayerStatus>() {
    public PlayerStatus parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
        return new PlayerStatus(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<PlayerStatus> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<PlayerStatus> getParserForType() {
    return PARSER;
  }

  public com.stefanolupo.ndngame.protos.PlayerStatus getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}
