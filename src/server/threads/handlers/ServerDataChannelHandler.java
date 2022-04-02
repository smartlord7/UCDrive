package server.threads.handlers;

import protocol.failover.redundancy.FailoverData;
import server.threads.connections.ServerDataChannelConnection;
import server.struct.ServerUserSessions;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

/**
 * Class that has the server data channel handler methods.
 */
public class ServerDataChannelHandler implements Runnable {

    // region Private properties

    private int number = 0;
    private final int port;
    private final ServerUserSessions sessions;
    private final BlockingQueue<FailoverData> dataToSync;

    // endregion Private properties

    // region Public methods

    /**
     * Constructor method
     * @param port is the server port.
     * @param sessions are the connected sessios.
     * @param dataToSync1 is the data to sync.
     */
    public ServerDataChannelHandler(int port, ServerUserSessions sessions, BlockingQueue<FailoverData> dataToSync1) {
        this.port = port;
        this.sessions = sessions;
        this.dataToSync = dataToSync1;
    }

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
                new ServerDataChannelConnection(clientSocket, number, sessions.getSession(client), dataToSync);
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    // endregion Public methods

}
