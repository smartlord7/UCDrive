package server.threads.failover;

import protocol.failover.redundancy.FailoverData;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ServerSyncer implements Runnable {

    private InetSocketAddress syncedHost;
    private BlockingQueue<FailoverData> dataToSync = new LinkedBlockingQueue<>();

    @Override
    public void run() {
        byte[] buf;
        byte[] resp;
        FailoverData data;
        DatagramSocket socket;
        DatagramPacket packetRequest;
        ByteArrayOutputStream byteWriter;
        ObjectOutputStream objWriter;

        try {
            socket = new DatagramSocket();

            System.out.println("[SYNCER] Started");
            while ((data = dataToSync.poll()) != null) {
                while (true) {
                    byteWriter = new ByteArrayOutputStream();
                    objWriter = new ObjectOutputStream(byteWriter);
                    objWriter.writeObject(data);
                    objWriter.flush();
                    buf = byteWriter.toByteArray();

                    //packetRequest = new DatagramPacket(buf, buf.length, syncedHost.getAddress(), syncedHost.getPort());
                    //socket.send(packetRequest);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
