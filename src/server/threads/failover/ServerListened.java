package server.threads.failover;

import java.io.*;
import java.net.*;

public class ServerListened implements Runnable {
    private final int port;
    private final int BUF_SIZE = 4096;


    public ServerListened(int port) {
        this.port = port;
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
            System.out.println("[HEARTBEAT] Started at port: " + port);
            socket = new DatagramSocket(port);
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
