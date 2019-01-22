package com.stefanolupo.ndntesting;

import net.named_data.jndn.*;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Pinger implements OnData, OnTimeout {

  private AtomicBoolean outstandingRequest = new AtomicBoolean(false);
  private AtomicInteger nextPing = new AtomicInteger(0);

  private final String pingLocation;
  private final Face face;
  private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

  Pinger(String pingLocation) {
    this.pingLocation = pingLocation;
    this.face = new Face();
    scheduler.scheduleAtFixedRate(this::checkForEvents, 0, 1, TimeUnit.SECONDS);
  }

  @Override
  public void onData(Interest interest, Data data) {
    String name = data.getName().toUri();
    System.out.println("DATA: " + name + ": " + data.getContent().toString() + "\n");
    nextPing.incrementAndGet();
    outstandingRequest.set(false);
  }

  @Override
  public void onTimeout(Interest interest) {
    System.out.println("Timeout for interest: " + interest.getName().toUri() + "\n");
    nextPing.incrementAndGet();
    outstandingRequest.set(false);
  }

  private void checkForEvents() {
    try {
      face.processEvents();
    } catch (Exception e) {
      System.err.println("Failed to process events");
      e.printStackTrace();
    }
  }

  private Name getNextPing() {
    return new Name(pingLocation + "/" + nextPing.get());
  }

  public void endlessPing(long sleepMs) {
    while (true) {
      if (!outstandingRequest.get()) {
        ping();
      } else {
        try {
          Thread.sleep(sleepMs);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }

    }
  }

  public void ping() {
    try {
      Name nextPing = getNextPing();
      System.out.println("Expressing interest for: " + nextPing.toUri());
      face.expressInterest(nextPing, this, this);
      face.processEvents();
      outstandingRequest.set(true);
    } catch (Exception e) {
      System.err.println("Could not express interest");
      e.printStackTrace();
    }
  }

  public static void main(String[] args) throws Exception {
    System.out.println("Starting pinger!");
    Pinger pinger = new Pinger("ndngateway:/com/stefanolupo/desktop/ping");
    pinger.endlessPing(5000);
  }

}
