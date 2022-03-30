package server;

import datalayer.model.User.UserSession;
import presentationlayer.Server;
import protocol.Request;
import protocol.RequestMethodEnum;
import protocol.Response;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public class ClientCommandConnection extends Thread {
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Socket clientSocket;
    private UserSession session;
    private int connectionId;

    public ClientCommandConnection(Socket socket, int id) {
        session = new UserSession();
        connectionId = id;
        try {
            clientSocket = socket;
            in = new ObjectInputStream(new DataInputStream(clientSocket.getInputStream()));
            out = new ObjectOutputStream(new DataOutputStream(clientSocket.getOutputStream()));
            out.flush();
            out.reset();
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
                out.flush();
                out.reset();
            } catch (SocketException | EOFException e) {
                System.out.println("[ERROR] Client " + clientSocket.getInetAddress() + ":" + clientSocket.getPort() + " disconnected!");
                return;
            } catch (IOException | NoSuchAlgorithmException | SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private Response handleRequest(Request req) throws SQLException, NoSuchAlgorithmException, IOException {
        Response resp = null;
        RequestMethodEnum method = req.getMethod();

        switch (method) {
            case USER_AUTHENTICATION -> resp = Server.authUser(req, session);
            case USER_CHANGE_PASSWORD -> resp = Server.changePassword(req);
            case USER_LIST_SERVER_FILES -> resp = Server.listDirFiles(req);
            case USER_CHANGE_CWD -> resp = Server.changeWorkingDir(req, session);
            case USER_DOWNLOAD_FILE -> {
            }
            case USER_UPLOAD_FILE -> {
            }
        }

        return resp;
    }
}
