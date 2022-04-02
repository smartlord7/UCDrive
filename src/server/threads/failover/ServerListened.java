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

import util.Const;
import java.io.*;
import java.net.*;

/**
 * Class that has the server listened methods.
 */

public class ServerListened implements Runnable {

    // region Private properties

    private final int port;

    // endregion Private properties

    // region Public methods

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
            System.out.println("Error: could not send heartbeat");
            e.printStackTrace();
        }
    }

    // endregion Public methods

}
