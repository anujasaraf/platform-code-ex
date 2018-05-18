package net.messaging;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class Main {
    private static class CodeExException extends Exception {
        public CodeExException(String message) {
            super(message);
        }
    }

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
            if (args.length < 2 || args[0].indexOf('-') == 0 && args.length < 3 || args[args.length - 1].isEmpty())
                throw new CodeExException("Cannot send an email with no body.");

            boolean isIm = args[0].contains("-im");
            String addresses[];
            if (!isIm) {
                addresses = Arrays.copyOf(args, args.length - 1);
            } else {
                addresses = Arrays.copyOfRange(args, 1 ,args.length - 1);
            }

            String message = args[args.length - 1];
            writeToNetwork(addresses, message, isIm);
        } catch (CodeExException e) {
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

    private static List<String> validate(String[] addresses) throws CodeExException {
        List<String> validAddresses = new ArrayList<>();
        StringBuilder invalidAddress = new StringBuilder();
        int count = 0;
        for (String address : addresses) {
            String[] emails = address.split(",");
            for (String email : emails) {
                if (email.indexOf('@') <= 0) {
                    invalidAddress.append(" ").append(email);
                    count++;
                    continue;
                }

                validAddresses.add(email);
            }
        }

        if (count != 0) {
            throw new CodeExException("Invalid email address" + (count == 1 ? ":" : "es:") + invalidAddress);
        }

        return validAddresses;
    }

    private static void writeToNetwork(String[] addresses, String message, boolean isIm) throws CodeExException {
        List<String> emailIds = validate(addresses);
        StringBuilder email = new StringBuilder();
        email.append("connect ").append(isIm ? "chat" : "smtp");
        for (String emid : emailIds) {
            if (!isIm)
                email.append("\nTo: ").append(emid);
            else
                email.append("\n<" + emid+ ">").append("(" + message + ")");
        }

        if (!isIm) {
            email.append("\n\n").append(message).append("\n\ndisconnect\n");
        } else {
            email.append("\ndisconnect\n");
        }

        try {
            network.write(email.toString());
            network.flush();
        } catch (IOException ioe) {
            writeToConsole("Connection error. Please try again.");
        }
    }
}
