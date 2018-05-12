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
        try {
            writeToNetwork(args[0], args[1]);
        } catch (Exception e) {
            writeToConsole(e.getMessage());
        }
    }

    private static void writeToConsole(String errorMessage) {
        try {
            console.write(errorMessage + "\n");
            console.flush();
        } catch (Exception ioe) {
            log.severe(ioe.getMessage());
        }
    }

    private static void validate(String address) {
        if (address.indexOf('@') <= 0) {
            throw new RuntimeException("Invalid email address: " + address);
        }
    }

    private static void writeToNetwork(String address, String message) {
        validate(address);
        String output = String.format("connect smtp\nTo: %s\n\n%s\n\ndisconnect\n", address, message);
        try {
            network.write(output);
            network.flush();
        } catch (IOException ioe) {
            log.severe(ioe.getMessage());
        }
    }
}