package io.github.edrla.jscan;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

class Main {

    static void main(String[] args) {
        if (invalidUsage(args)) {
            printUsage();
            return;
        }

        String host = args[0];
        if (!isValidAddress(host)) {
            System.out.println("Provided ipv4-address is invalid!");
            return;
        }

        int startPort, endPort;
        if (args.length == 3) {
            try {
                startPort = Integer.parseInt(args[1]);
                endPort = Integer.parseInt(args[2]);
            } catch (NumberFormatException nfe) {
                System.out.println("Start or end port is not a valid integer.");
                return;
            }
            boolean valid = true;
            if (!isValidPort(startPort)) {
                System.out.printf("Start port %d is invalid, must be between 0 and 65535.%n", startPort);
                valid = false;
            }
            if (!isValidPort(endPort)) {
                System.out.printf("End port %d is invalid, must be between 0 and 65535.%n", endPort);
                valid = false;
            }
            if (isValidPort(startPort) && isValidPort(endPort) && startPort > endPort) {
                System.out.printf("Start port %d is larger than end port %d.%n", startPort, endPort);
                valid = false;
            }
            if (!valid) return;
        } else {
            startPort = 1;
            endPort = 1024;
        }

        List<Future<PortResult>> futures = new ArrayList<>();
        int range = endPort - startPort + 1;
        int threadCount = Math.min(range, 1024);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        System.out.printf("Scanning open ports on %s from port %d to port %d.%n", host, startPort, endPort);
        long startTime = System.currentTimeMillis();
        for (int port = startPort; port <= endPort; port++) {
            futures.add(executor.submit(new ScannerCallable(host, port, 500)));
        }
        executor.shutdown();

        int nOfPorts = endPort - startPort + 1;
        int nOfCheckedPorts = 0;
        List<PortResult> openPorts = new ArrayList<>();
        try {
            for (Future<PortResult> future : futures) {
                PortResult result = future.get();
                if (result.isOpen()) openPorts.add(result);
                nOfCheckedPorts++;
                System.out.print("\r");
                double progress = ((double) nOfCheckedPorts) / nOfPorts;
                printProgressBar(progress);
            }
        } catch (InterruptedException | ExecutionException e) {
            // cannot happen
        }
        long endTime = System.currentTimeMillis();

        double duration = (endTime - startTime) / 1_000.0;
        System.out.println();
        System.out.println("Port scanning complete.");
        System.out.printf("Scanning took %.2f seconds.%n", duration);
        System.out.println("Open ports:");
        for (PortResult result : openPorts) {
            System.out.printf("\t--Port %d (%s) %s.%n", result.port(), result.serviceName(), result.openStatus());
        }
    }

    private static void printProgressBar(double progress) {
        int progressChars = (int) (20 * progress);
        int whiteSpaces = 20 - progressChars;
        StringBuilder sb = new StringBuilder();
        sb.append("Progress: %.0f%% ".formatted(progress * 100));
        sb.append('[');
        sb.append("=".repeat(progressChars));
        sb.append(" ".repeat(whiteSpaces));
        sb.append(']');
        System.out.print(sb);
    }

    private static boolean invalidUsage(String[] args) {
        return args.length != 1 && args.length != 3 ||
                args.length == 1 && args[0].equalsIgnoreCase("--help") ||
                args.length == 1 && args[0].equalsIgnoreCase("-h");
    }

    private static void printUsage() {
        System.out.println("Usage:");
        System.out.println("Quick Scan (ports 1 - 1024): jscan <ipv4-address>");
        System.out.println("Custom port range: jscan <ipv4-address> <start-port> <end-port>");
    }

    private static boolean isValidAddress(String ip) {
        try {
            if (ip == null || ip.isBlank()) {
                return false;
            }
            InetAddress address = InetAddress.getByName(ip);
            return address instanceof Inet4Address;
        } catch (UnknownHostException uhe) {
            return false;
        }
    }

    private static boolean isValidPort(int port) {
        return port >= 0 && port <= 65535;
    }
}
