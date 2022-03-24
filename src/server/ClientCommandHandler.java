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
            System.out.println("[CMD THREAD] Port: " + port);
            System.out.println("[CMD THREAD] Socket: " + listenSocket);
            while(true) {
                Socket clientSocket = listenSocket.accept();
                System.out.println("[CMD THREAD] Client: " + clientSocket);
                number++;
                new ClientCommandConnection(clientSocket, number);
            }
        } catch(IOException e) {
            e.printStackTrace();;
        }
    }
}
