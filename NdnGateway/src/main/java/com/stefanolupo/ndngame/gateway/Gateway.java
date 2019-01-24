package com.stefanolupo.ndngame.gateway;

import com.stefanolupo.ndngame.Names;
import net.named_data.jndn.*;
import net.named_data.jndn.security.KeyChain;

public class Gateway implements OnInterestCallback {

    private final Face face;
    private final KeyChain keyChain;


    Gateway() throws Exception {
        keyChain = new KeyChain();
        face = new Face();
        Name gatewayNamePrefix = new Name(Names.REGISTER.toString());
        face.setCommandSigningInfo(keyChain, keyChain.getDefaultCertificateName());
        face.registerPrefix(gatewayNamePrefix, this,  name -> System.err.println("Could not register prefix: " + name.toString()));
    }

    @Override
    public void onInterest(Name prefix, Interest interest, Face face, long interestFilterId, InterestFilter filter) {
    }

    public static void main(String[] args) throws Exception {

    }
}
