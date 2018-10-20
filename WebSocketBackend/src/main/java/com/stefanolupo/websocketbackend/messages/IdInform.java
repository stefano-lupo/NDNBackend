package com.stefanolupo.websocketbackend.messages;

import com.stefanolupo.websocketbackend.GameObject;

public class IdInform extends Message {

  private String id;

  @Override
  public MessageType getMessageType() {
    return MessageType.ID_INFORM;
  }

  public IdInform() {}

  public IdInform(String id) {
    this.id = id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }
}
