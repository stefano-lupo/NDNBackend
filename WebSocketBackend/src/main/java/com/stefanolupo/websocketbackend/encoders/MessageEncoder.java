package com.stefanolupo.websocketbackend.encoders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stefanolupo.websocketbackend.messages.Message;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public class MessageEncoder implements Encoder.Text<Message> {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  @Override
  public String encode(Message message) throws EncodeException {
    String s;
    try {
      s = OBJECT_MAPPER.writeValueAsString(message);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    return s;
  }

  @Override
  public void init(EndpointConfig endpointConfig) {

  }

  @Override
  public void destroy() {

  }
}
