package com.stefanolupo.ndngame.backend;

import com.stefanolupo.ndngame.protos.Player;
import net.named_data.jndn.*;
import net.named_data.jndn.security.KeyChain;
import net.named_data.jndn.transport.AsyncTcpTransport;
import net.named_data.jndn.util.Blob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class NfdPingBenchmark implements OnData, OnTimeout {

    private static final Logger LOG = LoggerFactory.getLogger(NfdPingBenchmark.class);

    private Face face;
    private final Name pingName;

    private int numPings;
    private int numNormalPings = 0;
    private int numAsyncPings = 0;

    private long startTime = 0;

    public NfdPingBenchmark(Name pingName, int numPings) throws Exception {
        this.pingName = pingName;
        this.numPings = numPings;

        face = new Face();
        KeyChain keyChain = new KeyChain();
        face.setCommandSigningInfo(keyChain, keyChain.getDefaultCertificateName());

        pingWithNormalFace();

        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        face = new ThreadPoolFace(service, new AsyncTcpTransport(service), new AsyncTcpTransport.ConnectionInfo("localhost"));
        pingWithAsyncFace();
    }

    private void pingWithNormalFace() throws Exception {
        LOG.debug("Pinging with normal face");
        startTime = System.currentTimeMillis();
        expressSafe();

        while (numNormalPings < numPings) {
            face.processEvents();
            Thread.sleep(1);
        }
    }

    private void pingWithAsyncFace() throws Exception {
        LOG.debug("Pinging with async face");
        expressSafe();
    }

    @Override
    public void onData(Interest interest, Data data) {
        long stopTime = System.currentTimeMillis();
        LOG.info("Got data after {}ms: {}", stopTime - startTime, data.getName());

        if (numNormalPings < numPings) {
            numNormalPings++;
        } else if (numAsyncPings < numPings) {
            numAsyncPings++;
        } else {
            return;
        }

        expressSafe();

    }

    private void expressSafe() {
        LOG.debug("Expressing interest for {}", pingName);
        startTime = System.currentTimeMillis();
        try {
            face.expressInterest(new Name(pingName).append(String.valueOf(numNormalPings + numAsyncPings)), this, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTimeout(Interest interest) {
        LOG.debug("Timeout");

        if (numNormalPings < numPings) {
            numNormalPings++;
        } else if (numAsyncPings < numPings) {
            numNormalPings++;
        } else {
            return;
        }

        expressSafe();

    }

    static class Test implements OnInterestCallback {
        @Override
        public void onInterest(Name prefix, Interest interest, Face face, long interestFilterId, InterestFilter filter) {
            System.out.println("Received interest..sleeping for 30s");
            System.out.println(interest.toUri());
            try {
                Thread.sleep(20000);

                System.out.println("sending datas");

                Data data = new Data(interest.getName());
                data.setContent(new Blob(Player.newBuilder().setName("Hello world!").build().toByteArray()));
                face.putData(data);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }


    public static void main(String[] args) throws Exception {
//        NfdPingBenchmark nfdPingBenchmark = new NfdPingBenchmark(new Name("/com/test/laptop/ping"), 10);
        Face face = new Face();
        face.setCommandSigningInfo(new KeyChain(), new KeyChain().getDefaultCertificateName());
        Test test = new Test();
        face.registerPrefix(new Name("/com/desktop"), test, p -> System.out.println(p));

        while (true) {
            face.processEvents();
            Thread.sleep(5);
        }
    }
}
