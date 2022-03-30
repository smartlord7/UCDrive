package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientDataHandler implements Runnable {
    private int port;
    private int number = 0;

    public ClientDataHandler(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try (ServerSocket listenSocket = new ServerSocket(port)) {
            System.out.println("[DATA THREAD] Port: " + port);
            System.out.println("[DATA THREAD] Socket: " + listenSocket);
            while (true) {
                Socket clientSocket = listenSocket.accept();
                System.out.println("[DATA THREAD] Client: " + clientSocket);
                number++;
                new ClientDataConnection(clientSocket, number);
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
