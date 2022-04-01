package server.threads.handlers;

import server.threads.connections.ServerCommandChannelConnection;
import server.struct.ServerUserSessions;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerCommandChannelHandler implements Runnable {
    private int number = 0;
    private final int port;
    private final ServerUserSessions sessions;

    public ServerCommandChannelHandler(int port, ServerUserSessions sessions) {
        this.port = port;
        this.sessions = sessions;
    }

    @Override
    public void run() {
        try (ServerSocket listenSocket = new ServerSocket(port)) {
            System.out.println("[CMD CHANNEL] Started at port: " + port);
            while(true) {
                Socket clientSocket = listenSocket.accept();
                System.out.println("[CMD CHANNEL] Client received: " + clientSocket.getInetAddress());
                number++;
                String client = clientSocket.getInetAddress().toString();
                new ServerCommandChannelConnection(clientSocket, number, sessions.addSession(client));
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
