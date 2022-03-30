package server;

import sync.ClientChannelSync;
import sync.SyncObj;

import java.io.*;
import java.net.Socket;

class ClientDataConnection extends Thread {
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Socket clientSocket;
    private int number;
    private final SyncObj syncObj;

    public ClientDataConnection(Socket aClientSocket, int number, SyncObj syncObj) {
        this.number = number;
        this.syncObj = syncObj;
        try {
            clientSocket = aClientSocket;
            in = new ObjectInputStream(new DataInputStream(clientSocket.getInputStream()));
            out = new ObjectOutputStream(new DataOutputStream(clientSocket.getOutputStream()));
            this.start();
        } catch (IOException e) {
            System.out.println("Connection:" + e.getMessage());
        }
    }

    public void run() {
        try {
            while(true){
               Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
