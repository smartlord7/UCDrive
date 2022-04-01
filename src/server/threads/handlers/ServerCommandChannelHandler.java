package server.threads.handlers;

import protocol.failover.redundancy.FailoverData;
import server.threads.connections.ServerCommandChannelConnection;
import server.struct.ServerUserSessions;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

public class ServerCommandChannelHandler implements Runnable {
    private int number = 0;
    private final int port;
    private final ServerUserSessions sessions;
    private final BlockingQueue<FailoverData> dataToSync;

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
        try (ServerSocket listenSocket = new ServerSocket(port)) {
            System.out.println("[CMD CHANNEL] Started at port: " + port);
            while(true) {
                Socket clientSocket = listenSocket.accept();
                System.out.println("[CMD CHANNEL] Client received: " + clientSocket.getInetAddress());
                number++;
                String client = clientSocket.getInetAddress().toString();
                new ServerCommandChannelConnection(clientSocket, number, sessions.addSession(client), dataToSync);
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
