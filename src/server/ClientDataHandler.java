package server;

import sync.ClientChannelSync;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientDataHandler implements Runnable {
    private int number = 0;
    private final int port;
    private final ClientChannelSync channelSync;

    public ClientDataHandler(int port, ClientChannelSync channelSync) {
        this.port = port;
        this.channelSync = channelSync;
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
                String client = clientSocket.getInetAddress() + ":" + clientSocket.getLocalPort();
                new ClientDataConnection(clientSocket, number, channelSync.getClientSyncObj(client));
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
