package server.threads.failover;

import util.Const;

import java.io.*;
import java.net.*;

public class ServerListener implements Runnable {
    private final InetSocketAddress listenedHost;
    private final int heartbeatInterval;
    private final int maxFailedHeartbeats;
    private final int timeout;

    /**
     * Constructor method.
     * @param listenedHostIp is the listened host ip
     * @param listenedHostPort is the listened host port
     * @param heartbeatInterval is the heartbeat interval.
     * @param maxFailedHeartbeats is number of heartbeats before total failure.
     * @param timeout is the timeout used to verify if a heartbeat has failed.
     * @throws InterruptedException - if the method is interrupted (i.e. manually stopping the program)
     */
    public ServerListener(String listenedHostIp, int listenedHostPort, int heartbeatInterval, int maxFailedHeartbeats, int timeout) throws InterruptedException {
        listenedHost = new InetSocketAddress(listenedHostIp, listenedHostPort);
        this.heartbeatInterval = heartbeatInterval;
        this.maxFailedHeartbeats = maxFailedHeartbeats;
        this.timeout = timeout;

        Thread thisThread = new Thread(this);
        thisThread.start();
        thisThread.join();
    }

    /**
     * Method used to listen to the main server heartbeats.
     */
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

                    resp = new byte[Const.UDP_BUFFER_SIZE];
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
