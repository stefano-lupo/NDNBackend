package com.stefanolupo.websocketbackend.messages;


public abstract class Message {

  enum MessageType {
    ID_INFORM,
    STATE_UPDATE,   // Publish update of all nodes
    NODE_UPDATE     // Node informing of an update
  }

  public MessageType messageType;

  public abstract MessageType getMessageType();

}
