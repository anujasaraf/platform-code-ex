package net.messaging;

import java.io.*;
import java.util.Arrays;
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
            if (args.length < 2 || args[args.length - 1].isEmpty())
                throw new RuntimeException("Cannot send an email with no body.");

            String message = args[args.length - 1];
            writeToNetwork(Arrays.copyOf(args, args.length - 1), message);
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

    private static void validate(String[] addresses) {
        for (String address: addresses) {
            if (address.indexOf('@') <= 0) {
                throw new RuntimeException("Invalid email address: " + address);
            }
        }
    }

    private static void writeToNetwork(String[] addresses, String message) {
        validate(addresses);
        StringBuilder email = new StringBuilder();
        email.append("connect smtp");

        for(String address: addresses) {
            String[] emails = address.split(",");
            for(String emid: emails) {
                email.append("\nTo: ").append(emid);
            }
        }

        email.append("\n\n").append(message).append("\n\ndisconnect\n");

        try {
            network.write(email.toString());
            network.flush();
        } catch (IOException ioe) {
            log.severe(ioe.getMessage());
        }
    }
}