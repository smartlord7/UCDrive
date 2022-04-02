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

import server.threads.connections.ServerDataChannelConnection;
import server.struct.ServerUserSessions;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Class that has the server data channel handler methods.
 */

public class ServerDataChannelHandler implements Runnable {

    // region Private properties

    private int number = 0;
    private final int port;
    private final ServerUserSessions sessions;

    // endregion Private properties

    // region Constructors

    /**
     * Constructor method
     * @param port is the server port.
     * @param sessions are the connected sessios.
     */
    public ServerDataChannelHandler(int port, ServerUserSessions sessions) {
        this.port = port;
        this.sessions = sessions;
    }

    // endregion Constructors

    // region Public methods

    /**
     * Method that creates a thread for TCP client connection to the data channel.
     */
    @Override
    public void run() {
        try (ServerSocket listenSocket = new ServerSocket(port)) {
            System.out.println("[DATA CHANNEL] Started at port: " + port);

            while (true) {
                Socket clientSocket = listenSocket.accept();
                System.out.println("[DATA CHANNEL] Client received: " + clientSocket);
                number++;
                String client = clientSocket.getInetAddress().toString();
                new ServerDataChannelConnection(clientSocket, number, sessions.getSession(client));
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    // endregion Public methods

}
