package server.threads.failover;

import protocol.failover.redundancy.FailoverData;
import protocol.failover.redundancy.FailoverDataTypeEnum;
import protocol.failover.redundancy.FailoverFeedback;
import protocol.failover.redundancy.FailoverFeedbackTypeEnum;
import util.Const;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;

public class ServerSynced implements Runnable{
    private final int port;

    /**
     * Constructor method.
     * @param port is the server port.
     */
    public ServerSynced(int port) {
        this.port = port;
        new Thread(this).start();
    }

    /**
     * Method used to await new data from the main server to sync locally.
     */
    //TODO: Multiple instances of this thread
    // could be called with the use of a BlockingQueue in order to enhance the redundancy process efficiency
    @Override
    public void run() {
        int currTotalSize;
        int currSize;
        byte[] buf;
        String content;
        String filePath;
        Path p;
        FailoverData request;
        FailoverFeedback response;
        FailoverData temp;
        DatagramSocket socket;
        DatagramPacket packetRequest;
        ByteArrayInputStream byteReader;
        ObjectInputStream objectReader;
        BufferedWriter fileWriter;

        currTotalSize = 0;
        currSize = 0;
        request = null;
        fileWriter = null;

        try {
            System.out.println("[SYNCED] Started at port: " + port);

            socket = new DatagramSocket(port);
            while (true) {
                buf = new byte[Const.UDP_BUFFER_SIZE];
                packetRequest = new DatagramPacket(buf, buf.length);
                socket.receive(packetRequest);
                byteReader = new ByteArrayInputStream(buf);
                objectReader = new ObjectInputStream(byteReader);
                temp = (FailoverData) objectReader.readObject();

                System.out.println("[SYNCED] Received: " + temp);

                if (!temp.verifyChecksum()) {
                    System.out.println("[SYNCED] Packet: " + temp.getId() + " damaged or corrupted");

                    response = new FailoverFeedback(temp.getId(), FailoverFeedbackTypeEnum.NACK);
                    sendFeedback(response, socket, packetRequest);
                } else {
                    if (temp.getType() == FailoverDataTypeEnum.FILE) {
                        content = temp.getContent();
                        currTotalSize = temp.getTotalSize();
                        currSize += temp.getSize();

                        // if it's a packet for the same file
                        if (request == null || !temp.getName().equals(request.getName())) {
                            // if it's the first file or a different one
                            filePath = System.getProperty("user.dir") + "\\" + temp.getName();
                            p = Paths.get(filePath).getParent();

                            if (!Files.exists(p)) {
                                Files.createDirectory(p);
                            }

                            fileWriter = new BufferedWriter(new FileWriter(filePath));
                            request = temp;

                        }
                        fileWriter.write(content);

                        if (currTotalSize <= currSize) {
                            fileWriter.close();
                            currSize = 0;
                        }
                    } else if (temp.getType() == FailoverDataTypeEnum.DB_DML) {

                    }
                }

                response = new FailoverFeedback(temp.getId(), FailoverFeedbackTypeEnum.ACK);
                sendFeedback(response, socket, packetRequest);
            }
        } catch (IOException | ClassNotFoundException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private void sendFeedback(FailoverFeedback response, DatagramSocket socket, DatagramPacket packetRequest) throws IOException {
        byte[] buf;
        ByteArrayOutputStream byteWriter;
        ObjectOutputStream objectWriter;
        DatagramPacket packetResponse;

        byteWriter = new ByteArrayOutputStream();
        objectWriter = new ObjectOutputStream(byteWriter);
        objectWriter.writeObject(response);
        objectWriter.flush();
        buf = byteWriter.toByteArray();
        packetResponse = new DatagramPacket(buf, buf.length, packetRequest.getSocketAddress());
        socket.send(packetResponse);
    }
}
