package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientCommandHandler implements Runnable{
    private int port;
    private int number = 0;

    public ClientCommandHandler(int port) {
        this.port = port;
    }
    @Override
    public void run() {
        try (ServerSocket listenSocket = new ServerSocket(port)) {
            System.out.println("Listening to port "+port);
            System.out.println("LISTEN SOCKET=" + listenSocket);
            while(true) {
                Socket clientSocket = listenSocket.accept(); // BLOQUEANTE
                System.out.println("CLIENT_SOCKET (created at accept())="+clientSocket);
                number++;
                new ClientCommandConnection(clientSocket, number);
            }
        } catch(IOException e) {
            System.out.println("Listen:" + e.getMessage());
        }
    }
}
