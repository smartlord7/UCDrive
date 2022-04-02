package server.threads.failover;

import protocol.failover.redundancy.FailoverData;
import protocol.failover.redundancy.FailoverFeedback;
import protocol.failover.redundancy.FailoverFeedbackTypeEnum;
import util.Const;
import util.Hasher;
import util.StringUtil;

import java.io.*;
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
        FailoverData request;
        FailoverFeedback response;
        DatagramSocket socket;
        DatagramPacket packetRequest;
        DatagramPacket packetResponse;
        ByteArrayOutputStream byteWriter;
        ObjectOutputStream objWriter;
        ByteArrayInputStream byteReader;
        ObjectInputStream objectReader;

        try {
            socket = new DatagramSocket();
            resp = new byte[Const.UDP_BUFFER_SIZE];

            System.out.println("[SYNCER] Started");

                while (true) {
                    request = dataToSync.take();
                    request.setChecksum(Hasher.hashBytes(StringUtil.bytesToHex(request.getContent()), Const.FILE_CONTENT_CHECKSUM_ALGORITHM));
                    byteWriter = new ByteArrayOutputStream();
                    objWriter = new ObjectOutputStream(byteWriter);
                    objWriter.writeObject(request);
                    objWriter.flush();
                    buf = byteWriter.toByteArray();

                    packetRequest = new DatagramPacket(buf, buf.length, syncedHost.getAddress(), syncedHost.getPort());
                    socket.send(packetRequest);

                    packetResponse = new DatagramPacket(resp, resp.length, syncedHost.getAddress(), syncedHost.getPort());
                    socket.setSoTimeout(10);

                    try {
                        socket.receive(packetResponse);
                        byteReader = new ByteArrayInputStream(resp);
                        objectReader = new ObjectInputStream(byteReader);
                        response = (FailoverFeedback) objectReader.readObject();

                        if (response.getFeedback() == FailoverFeedbackTypeEnum.NACK) {
                            System.out.println("[SYNCER] Packet " + request.getId() + " NACK.");

                            socket.send(packetRequest);
                        }
                    } catch (SocketTimeoutException e) {
                        System.out.println("[SYNCER] Packet " + request.getId() + " timeout.");
                    }

                    socket.setSoTimeout(0);
                }
        } catch (IOException | InterruptedException | NoSuchAlgorithmException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
