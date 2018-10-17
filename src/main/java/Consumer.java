import net.named_data.jndn.*;

public class Consumer implements OnData, OnTimeout {

  static boolean dataReceived = false;

  public static void main(String[] args) {
    System.out.println("Hello world!");



    try {

      Face face = new Face("spurs.cs.ucla.edu");

      Consumer consumer = new Consumer();

      String pingName = "/ndn/org/caida/ping/"
        + Math.floor(Math.random() * 100000);

      Name name = new Name(pingName);

      System.out.println("Express name " + name.toUri());

      face.expressInterest(name, consumer, consumer);

      // The main event loop.

      while (!dataReceived) {

        face.processEvents();

        // We need to sleep for a few milliseconds so we don't use
        // 100% of

        // the CPU.

        Thread.sleep(5);

      }

    }

    catch (Exception e) {

      System.err.println("exception: " + e.getMessage());
      e.printStackTrace();

    }

  }

  public void onData(Interest interest, Data data) {
    System.out.println("Got data packet with name " + data.getName().toUri());
    dataReceived = true;
  }

  public void onTimeout(Interest interest) {
    System.out.println("Got a timeout for " + interest.toUri());
  }
}
