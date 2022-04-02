/**------------ucDrive: REPOSITÓRIO DE FICHEIROS NA UC------------
 University of Coimbra
 Degree in Computer Science and Engineering
 Sistemas Distribuidos
 3rd year, 2nd semester
 Authors:
 Sancho Amaral Simões, 2019217590, uc2019217590@student.uc.pt
 Tiago Filipe Santa Ventura, 2019243695, uc2019243695@student.uc.pt
 Coimbra, 2nd April 2022
 ---------------------------------------------------------------------------*/

package server.threads.failover;

import protocol.failover.redundancy.FailoverData;
import protocol.failover.redundancy.FailoverFeedback;
import protocol.failover.redundancy.FailoverFeedbackTypeEnum;
import util.Const;
import util.Hasher;
import util.StringUtil;
import java.io.*;
import java.net.*;
import java.util.concurrent.BlockingQueue;

/**
 * Class that has the server syncer methods.
 */

public class ServerSyncer implements Runnable {

    // region Private properties

    private final InetSocketAddress syncedHost;
    private final BlockingQueue<FailoverData> dataToSync;

    // endregion Private properties

    // region Public methods

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
        } catch (Exception e) {
            System.out.println("Error: could not send sync data");
            e.printStackTrace();
        }
    }

    // endregion Public methods

}
