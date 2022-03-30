package server;

import sync.SyncObj;
import util.Const;
import util.FileUtil;

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
            out.flush();
            out.reset();
            this.start();
        } catch (IOException e) {
            System.out.println("Connection:" + e.getMessage());
        }
    }

    public void run() {
        try {
            FileUtil.receiveFileByChunks(in, syncObj.getFileInfo(), Const.UPLOAD_FILE_CHUNK_SIZE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
