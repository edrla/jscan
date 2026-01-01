package io.github.edrla;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.Callable;

class ScannerCallable implements Callable<PortResult> {

    final private String host;
    final private int port;
    final private int timeout;

    ScannerCallable(String host, int port, int timeout) {
        this.host = host;
        this.port = port;
        this.timeout = timeout;
    }

    @Override
    public PortResult call() {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeout);
            return new PortResult(port, true);
        } catch (IOException ioe) {
            return new PortResult(port, false);
        }
    }
}
