package server.threads.failover;

import protocol.failover.redundancy.FailoverData;
import protocol.failover.redundancy.FailoverDataTypeEnum;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ServerSynced implements Runnable{
    private final int port;
    private final int BUF_SIZE = 4096;

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
        byte[] resp;
        String content;
        String filePath;
        Path p;
        FailoverData data;
        FailoverData temp;
        DatagramSocket socket;
        DatagramPacket packetRequest;
        DatagramPacket packetResponse;
        ByteArrayOutputStream byteWriter;
        ByteArrayInputStream byteReader;
        ObjectInputStream objectReader;
        BufferedWriter fileWriter;

        currTotalSize = 0;
        currSize = 0;
        data = null;
        fileWriter = null;

        try {
            System.out.println("[SYNCED] Started at port: " + port);
            socket = new DatagramSocket(port);
            while (true) {
                buf = new byte[BUF_SIZE];
                packetRequest = new DatagramPacket(buf, buf.length);
                socket.receive(packetRequest);
                byteReader = new ByteArrayInputStream(buf);
                objectReader = new ObjectInputStream(byteReader);
                temp = (FailoverData) objectReader.readObject();
                System.out.println("[SYNCED] Received: " + temp);

                if (temp.getType() == FailoverDataTypeEnum.FILE) {
                    content = temp.getContent();
                    currTotalSize = temp.getTotalSize();
                    currSize += temp.getSize();

                    // if it's a packet for the same file
                    if (data == null || !temp.getName().equals(data.getName())) {
                        // if it's the first file or a different one
                        filePath = System.getProperty("user.dir") + "\\" + temp.getName();
                        p = Paths.get(filePath).getParent();

                        if (!Files.exists(p)) {
                            Files.createDirectory(p);
                        }

                        fileWriter = new BufferedWriter(new FileWriter(filePath));
                        data = temp;

                    }
                    fileWriter.write(content);

                    if (currTotalSize >= currSize) {
                        fileWriter.close();
                        currSize = 0;
                    }
                } else if (temp.getType() == FailoverDataTypeEnum.DB_DML) {

                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
