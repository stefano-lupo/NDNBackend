package com.stefanolupo.ndntesting;

import net.named_data.jndn.*;
import net.named_data.jndn.security.KeyChain;
import net.named_data.jndn.transport.Transport;
import net.named_data.jndn.util.Blob;

public class Ponger implements OnInterest {

  private final KeyChain keyChain;
  private final Face face;
  private final Name certificateName;

  public static void main(String[] args) throws Exception {
    Ponger ponger = new Ponger();
    ponger.processEvents();
  }

  public Ponger() throws Exception {
    keyChain = new KeyChain();
    certificateName =  keyChain.getIdentityManager().getDefaultCertificateNameForIdentity(new Name("/com/stefanolupo/lupos"));

    face = new Face();
    face.setCommandSigningInfo(keyChain, certificateName);

    Name prefix = new Name("ndn:/com/stefanolupo/lupos");
    face.registerPrefix(prefix, this,  this::handleFailureToRegisterName);
  }

  public void processEvents() throws Exception {
    while (true) {
      face.processEvents();
      Thread.sleep(100);
    }
  }

  @Override
  public void onInterest(Name prefix, Interest interest, Transport transport, long registeredPrefixId)  {
    System.out.println("Got interest for: " + prefix.toUri() + " - " + interest.getName().toUri());

    Name dataName = new Name(interest.getName());
    Data data = new Data(dataName).setContent(new Blob("derp"));

    try {
      keyChain.sign(data, certificateName);
      transport.send(data.getDefaultWireEncoding().buf());
      System.out.println("Sent: " + data.getName().toUri() + "\n");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void handleFailureToRegisterName(Name failedPrefix) {
    System.err.println("Failed to register prefix " + failedPrefix.toUri());
  }
}
