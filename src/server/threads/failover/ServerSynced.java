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

import businesslayer.base.DAOResult;
import protocol.failover.redundancy.FailoverData;
import protocol.failover.redundancy.FailoverDataTypeEnum;
import protocol.failover.redundancy.FailoverFeedback;
import protocol.failover.redundancy.FailoverFeedbackTypeEnum;
import util.Const;

import java.io.*;
import java.lang.reflect.Method;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Class that has the server synced methods.
 */

public class ServerSynced implements Runnable{

    // region Private properties

    private final int port;

    // endregion Private properties

    // region Constructors

    /**
     * Constructor method.
     * @param port is the server port.
     */
    public ServerSynced(int port) {
        this.port = port;
        new Thread(this).start();
    }

    // endregion Constructors

    // region Public methods

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
        byte[] content;
        String filePath;
        Path p;
        FailoverFeedback response;
        DAOResult daoResult;
        FailoverData request;
        FailoverData temp;
        DatagramSocket socket;
        DatagramPacket packetRequest;
        ByteArrayInputStream byteReader;
        ObjectInputStream objectReader;
        FileOutputStream fileWriter;

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

                            fileWriter = new FileOutputStream(filePath);
                            request = temp;

                        }
                        fileWriter.write(content);

                        if (currTotalSize <= currSize) {
                            fileWriter.close();
                            currSize = 0;
                        }
                    } else if (temp.getType() == FailoverDataTypeEnum.DB_DML) {
                        buf = temp.getContent();
                        byteReader = new ByteArrayInputStream(buf);
                        objectReader = new ObjectInputStream(byteReader);
                        daoResult = (DAOResult) objectReader.readObject();

                        Method method = daoResult.getDaoClass().getMethod(daoResult.getDaoMethod(), daoResult.getEntityClass());
                        Object dao = daoResult.getDaoClass().getDeclaredConstructor().newInstance();
                        method.invoke(dao, daoResult.getEntity());
                    }
                }

                response = new FailoverFeedback(temp.getId(), FailoverFeedbackTypeEnum.ACK);
                sendFeedback(response, socket, packetRequest);
            }
        } catch (Exception e) {
            System.out.println("Error: could not receive sync data.");
            e.printStackTrace();
        }
    }

    // endregion Public methods

    // region Private methods

    /**
     * Method that sends the feedback on the packet transmission.
     * @param response is the response status.
     * @param socket is the handler socket.
     * @param packetRequest is the packet request.
     * @throws IOException - whenever an input or output operation is failed or interrupted.
     */
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

    // endregion Private methods

}
