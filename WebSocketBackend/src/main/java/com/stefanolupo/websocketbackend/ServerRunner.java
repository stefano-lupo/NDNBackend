package com.stefanolupo.websocketbackend;

import org.glassfish.tyrus.server.Server;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ServerRunner {

  public static void main(String[] args) {
    ServerRunner serverRunner = new ServerRunner();
    serverRunner.runServer();
  }

  public void runServer() {
    Server server = new Server("localhost", 8025, "/websockets", null, GameEndpoint.class);

    try {
      server.start();
      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
      System.out.println("Please press a key to stop the server.");
      reader.readLine();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      server.stop();
    }
  }
}
