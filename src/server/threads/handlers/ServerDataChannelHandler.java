package server.threads.handlers;

import server.threads.connections.ServerDataChannelConnection;
import sync.UserSessions;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerDataChannelHandler implements Runnable {
    private int number = 0;
    private final int port;
    private final UserSessions sessions;

    public ServerDataChannelHandler(int port, UserSessions sessions) {
        this.port = port;
        this.sessions = sessions;
    }

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
}
