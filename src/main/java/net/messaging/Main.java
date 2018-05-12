package net.messaging;

import java.io.*;
import java.util.logging.Logger;

public class Main {
    private final static Logger log = Logger.getLogger(Main.class.getName());
    private static Writer network;
    private static Writer console;

    public static void setNetwork(Writer network) {
        Main.network = network;
    }

    public static void setConsole(Writer console) {
        Main.console = console;
    }

    public static void main(String... args) {
        String output = String.format("connect smtp\nTo: %s\n\n%s\n\ndisconnect\n", args[0], args[1]);
        try {
            network.write(output);
            network.flush();
        } catch (IOException ioe) {
            log.severe(ioe.getMessage());
        }
    }
}