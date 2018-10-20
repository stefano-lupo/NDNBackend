package com.stefanolupo.websocketbackend;

import com.stefanolupo.websocketbackend.decoders.MessageDecoder;
import com.stefanolupo.websocketbackend.encoders.MessageEncoder;
import com.stefanolupo.websocketbackend.messages.IdInform;
import com.stefanolupo.websocketbackend.messages.Message;
import com.stefanolupo.websocketbackend.messages.NodeUpdate;
import com.stefanolupo.websocketbackend.messages.StateUpdate;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint(value="/game/state",
  decoders = MessageDecoder.class,
  encoders = MessageEncoder.class)
public class GameEndpoint {

  private ConcurrentHashMap<String, GameObject> gameObjectsById = new ConcurrentHashMap<>();
  private static Set<Session> clients = new CopyOnWriteArraySet<>();

  @OnOpen
  public void onOpen(Session session) throws IOException, EncodeException {
    clients.add(session);

    // Send client their ID, don't broadcast yet since client hasn't sent their coordinates, just connected
    IdInform idInform = new IdInform(session.getId());
    sendMessage(idInform, session);
  }

  @OnMessage
  public void onMessage(Session session, NodeUpdate nodeUpdate) throws IOException {
    gameObjectsById.put(session.getId(), nodeUpdate.getGameObject());
    broadcast(session);
  }

  @OnClose
  public void onClose(Session session) throws IOException {
    clients.remove(session);
    gameObjectsById.remove(session.getId());
    broadcast(session);
  }

  @OnError
  public void onError(Session session, Throwable throwable) {
    throw new RuntimeException(throwable);
  }

  private synchronized void broadcast(Session originatingSession) throws IOException {
    StateUpdate stateUpdate = new StateUpdate(gameObjectsById);
    for (Session session : clients) {
      if (session.equals(originatingSession)) {
        continue;
      }

      try {
        session.getBasicRemote().sendObject(stateUpdate);
      } catch (IOException | EncodeException e) {
        e.printStackTrace();
      }
    }
  }

  private void sendMessage(Message message, Session session) throws IOException, EncodeException {
    session.getBasicRemote().sendObject(message);
  }

}
