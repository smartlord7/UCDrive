package server.threads.failover;

import protocol.failover.redundancy.FailoverData;
import util.Const;
import util.Hasher;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.BlockingQueue;

public class ServerSyncer implements Runnable {

    private final InetSocketAddress syncedHost;
    private final BlockingQueue<FailoverData> dataToSync;

    /**
     * Constructor method.
     * @param syncedHostIp is the host ip.
     * @param syncedHostPort is the host port.
     * @param dataToSync is the data to sync.
     */
    public ServerSyncer(String syncedHostIp, int syncedHostPort, BlockingQueue<FailoverData> dataToSync) {
        this.syncedHost = new InetSocketAddress(syncedHostIp, syncedHostPort);
        this.dataToSync = dataToSync;

        new Thread(this).start();
    }

    /**
     * Method used to await new data from threads to sync with the secondary server.
     */
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
                while (true) {
                    data = dataToSync.take();
                    data.setChecksum(Hasher.hashBytes(data.getContent(), Const.CONTENT_CHECKSUM_ALGORITHM));
                    byteWriter = new ByteArrayOutputStream();
                    objWriter = new ObjectOutputStream(byteWriter);
                    objWriter.writeObject(data);
                    objWriter.flush();
                    buf = byteWriter.toByteArray();

                    packetRequest = new DatagramPacket(buf, buf.length, syncedHost.getAddress(), syncedHost.getPort());
                    socket.send(packetRequest);
                }
        } catch (IOException | InterruptedException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
