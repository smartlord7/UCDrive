package server.threads.failover;

import java.io.*;
import java.net.*;

public class ServerListener implements Runnable {
    private final InetSocketAddress listenedHost;
    private final int heartbeatInterval;
    private final int maxFailedHeartbeats;
    private final int timeout;
    private final int BUF_SIZE = 4096;

    public ServerListener(String watchedHostIp, int watchedHostPort, int heartbeatInterval, int maxFailedHeartbeats, int timeout) throws SocketException, InterruptedException {
        listenedHost = new InetSocketAddress(watchedHostIp, watchedHostPort);
        this.heartbeatInterval = heartbeatInterval;
        this.maxFailedHeartbeats = maxFailedHeartbeats;
        this.timeout = timeout;

        Thread thisThread = new Thread(this);
        thisThread.start();
        thisThread.join();
    }

    @Override
    public void run() {
        int failedHeartBeats;
        byte[] buf;
        byte[] resp;
        DatagramSocket socket;
        DatagramPacket packetRequest;
        DatagramPacket packetResponse;
        ByteArrayOutputStream byteWriter;

        failedHeartBeats = 0;

        try {
            socket = new DatagramSocket();
            System.out.println("[HEARTBEAT] Started");
            socket.setSoTimeout(timeout);
            while (failedHeartBeats < maxFailedHeartbeats) {
                try {
                    byteWriter = new ByteArrayOutputStream();
                    buf = byteWriter.toByteArray();

                    packetRequest = new DatagramPacket(buf, buf.length, listenedHost.getAddress(), listenedHost.getPort());
                    socket.send(packetRequest);

                    resp = new byte[BUF_SIZE];
                    packetResponse = new DatagramPacket(resp, resp.length, listenedHost.getAddress(), listenedHost.getPort());

                    socket.receive(packetResponse);
                    failedHeartBeats = 0;
                    System.out.println("[HEARTBEAT] Server at " + listenedHost + " is still alive");
                }
                catch (SocketTimeoutException ste) {
                    failedHeartBeats++;
                    System.out.println("[HEARTBEAT] Server at " + listenedHost + " failed " + failedHeartBeats + " heartbeats");
                }

                Thread.sleep(heartbeatInterval);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("[HEARTBEAT] Server at " + listenedHost + " is down.");
    }
}
