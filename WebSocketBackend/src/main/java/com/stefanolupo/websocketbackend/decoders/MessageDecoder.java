package com.stefanolupo.websocketbackend.decoders;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.stefanolupo.websocketbackend.messages.NodeUpdate;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

public class MessageDecoder implements Decoder.Text<NodeUpdate> {

  private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  @Override
  public NodeUpdate decode(String s) throws DecodeException {
    try {
      return OBJECT_MAPPER.readValue(s, NodeUpdate.class);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public boolean willDecode(String s) {
    return s != null;
  }

  @Override
  public void init(EndpointConfig endpointConfig) {

  }

  @Override
  public void destroy() {

  }
}
