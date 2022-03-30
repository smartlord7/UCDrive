package server;

import sync.UserSessions;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientCommandHandler implements Runnable {
    private int number = 0;
    private final int port;
    private final UserSessions sessions;

    public ClientCommandHandler(int port, UserSessions sessions) {
        this.port = port;
        this.sessions = sessions;
    }

    @Override
    public void run() {
        try (ServerSocket listenSocket = new ServerSocket(port)) {
            System.out.println("[CMD THREAD] Port: " + port);
            System.out.println("[CMD THREAD] Socket: " + listenSocket);
            while(true) {
                Socket clientSocket = listenSocket.accept();
                System.out.println("[CMD THREAD] Client: " + clientSocket);
                number++;
                String client = clientSocket.getInetAddress().toString();
                new ClientCommandConnection(clientSocket, number, sessions.addSession(client));
            }
        } catch(IOException e) {
            e.printStackTrace();;
        }
    }
}
