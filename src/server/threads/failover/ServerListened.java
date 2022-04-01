package server.threads.failover;

import util.Const;
import java.io.*;
import java.net.*;

public class ServerListened implements Runnable {
    private final int port;

    /**
     * Constructor method.
     * @param port is the server port.
     */
    public ServerListened(int port) {
        this.port = port;
        new Thread(this).start();
    }

    /**
     * Method associated to the thread that sends the heartbeats relative to the main server.
     */
    @Override
    public void run() {
        byte[] buf;
        byte[] resp;
        DatagramSocket socket;
        DatagramPacket packetRequest;
        DatagramPacket packetResponse;
        ByteArrayOutputStream byteWriter;
        ObjectOutputStream objWriter;

        try {
            System.out.println("[HEARTBEAT] Started at port: " + port);
            socket = new DatagramSocket(port);
            while (true) {
                buf = new byte[Const.UDP_BUFFER_SIZE];
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
