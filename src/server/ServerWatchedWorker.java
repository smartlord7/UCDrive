package server;

import java.io.*;
import java.net.*;

public class ServerWatchedWorker implements Runnable {
    private final int watchedHostPort;
    private final int BUF_SIZE = 4096;


    public ServerWatchedWorker(int watchedHostPort) throws SocketException, UnknownHostException {
        this.watchedHostPort = watchedHostPort;
        new Thread(this).start();
    }

    @Override
    public void run() {
        byte[] buf;
        byte[] resp;
        DatagramSocket socket;
        DatagramPacket packetRequest;
        DatagramPacket packetResponse;
        ByteArrayOutputStream byteWriter;

        try {
            System.out.println("[HEARTBEAT THREAD] Port: " + watchedHostPort);
            socket = new DatagramSocket(watchedHostPort);
            while (true) {
                buf = new byte[BUF_SIZE];
                packetRequest = new DatagramPacket(buf, buf.length);
                socket.receive(packetRequest);
                byteWriter = new ByteArrayOutputStream();
                resp = byteWriter.toByteArray();
                packetResponse = new DatagramPacket(resp, resp.length, packetRequest.getSocketAddress());
                socket.send(packetResponse);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
