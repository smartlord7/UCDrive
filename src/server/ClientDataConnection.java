package server;

import util.Const;
import util.FileUtil;

import java.io.*;
import java.net.Socket;

class ClientDataConnection extends Thread {
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Socket clientSocket;
    private int number;
    private final UserSession session;

    public ClientDataConnection(Socket aClientSocket, int number, UserSession session) {
        this.number = number;
        this.session = session;
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
            FileUtil.receiveFileByChunks(in, session, Const.UPLOAD_FILE_CHUNK_SIZE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
