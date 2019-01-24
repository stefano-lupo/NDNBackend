package com.stefanolupo.ndn;

import java.util.List;

public class Config {

    public List<Node> nodes;

    public static final class Node {
        public String name;
        public String externalIp;
        public String internalIp;
        public int port;
    }
}
