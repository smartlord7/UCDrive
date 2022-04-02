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

package server.threads.handlers;

import protocol.failover.redundancy.FailoverData;
import server.struct.ServerUserSession;
import server.threads.connections.ServerCommandChannelConnection;
import server.struct.ServerUserSessions;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

/**
 * Class that has the server command channel handler methods.
 */

public class ServerCommandChannelHandler implements Runnable {

    // region Private properties

    private int number = 0;
    private final int port;
    private final ServerUserSessions sessions;
    private final BlockingQueue<FailoverData> dataToSync;

    // endregion Private properties

    // region Public methods

    /**
     * Constructor method.
     * @param port is the server port.
     * @param sessions are the current sessions.
     * @param dataToSync is the data to sync.
     */
    public ServerCommandChannelHandler(int port, ServerUserSessions sessions, BlockingQueue<FailoverData> dataToSync) {
        this.port = port;
        this.sessions = sessions;
        this.dataToSync = dataToSync;
    }

    /**
     * Method that creates a thread for TCP client connection to the command channel.
     */
    @Override
    public void run() {
        String client;
        ServerSocket listenSocket;
        Socket clientSocket;
        ServerUserSession session;

        try {
            listenSocket = new ServerSocket(port);
            System.out.println("[CMD CHANNEL] Started at port: " + port);
            while(true) {
                clientSocket = listenSocket.accept();
                System.out.println("[CMD CHANNEL] Client received: " + clientSocket.getInetAddress());
                number++;
                client = clientSocket.getInetAddress().toString();
                session = sessions.addSession(client);
                session.setDataToSync(dataToSync);
                new ServerCommandChannelConnection(clientSocket, number, session);
            }
        } catch (IOException e) {
            System.out.println("Error: could not listen for new connections");
            e.printStackTrace();
        }
    }

    // endregion Public methods

}
