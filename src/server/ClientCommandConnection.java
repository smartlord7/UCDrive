package server;

import presentationlayer.Server;
import protocol.Request;
import protocol.RequestMethodEnum;
import protocol.Response;

import java.io.*;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

class ClientCommandConnection extends Thread {
    ObjectInputStream in;
    ObjectOutputStream out;
    Socket clientSocket;
    int thread_number;

    public ClientCommandConnection(Socket aClientSocket, int number) {
        thread_number = number;
        try {
            clientSocket = aClientSocket;
            in = new ObjectInputStream(new DataInputStream(clientSocket.getInputStream()));
            out = new ObjectOutputStream(new DataOutputStream(clientSocket.getOutputStream()));
            this.start();
        } catch (IOException e) {
            System.out.println("Connection:" + e.getMessage());
        }
    }

    @Override
    public void run() {
        while(true) {
            Response resp;
            Request req;
            try {
                req = (Request) in.readObject();
                resp = handleRequest(req);
                out.writeObject(resp);
            } catch (IOException | NoSuchAlgorithmException | SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private Response handleRequest(Request req) throws SQLException, NoSuchAlgorithmException {
        Response resp = null;
        RequestMethodEnum method = req.getMethod();

        switch (method) {
            case USER_AUTHENTICATION -> resp = Server.loginUser(req);
            case USER_CHANGE_PASSWORD -> {
            }
            case USER_LIST_SERVER_FILES -> {
            }
            case USER_CHANGE_CWD -> {
            }
            case USER_DOWNLOAD_FILE -> {
            }
            case USER_UPLOAD_FILE -> {
            }
        }

        return resp;
    }
}
