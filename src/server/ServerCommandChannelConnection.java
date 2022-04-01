package server;

import protocol.Request;
import protocol.RequestMethodEnum;
import protocol.Response;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public class ServerCommandChannelConnection extends Thread {
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Socket clientSocket;
    private final ServerUserSession session;
    private final int connectionId;

    public ServerCommandChannelConnection(Socket socket, int id, ServerUserSession session) {
        this.connectionId = id;
        this.session = session;
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
            } catch (IOException | NoSuchAlgorithmException | SQLException | ClassNotFoundException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private Response handleRequest(Request req) throws SQLException, NoSuchAlgorithmException, IOException, InterruptedException {
        Response resp = null;
        RequestMethodEnum method = req.getMethod();

        switch (method) {
            case USER_CREATE -> resp = ServerController.createUser(req, session);
            case USER_AUTHENTICATION -> resp = ServerController.authUser(req, session);
            case USER_LOGOUT -> resp = ServerController.logoutUser(req, session);
            case USER_CHANGE_PASSWORD -> resp = ServerController.changeUserPassword(req);
            case USER_LIST_SERVER_FILES -> resp = ServerController.listDirFiles(req, session);
            case USER_CHANGE_CWD -> resp = ServerController.changeWorkingDir(req, session);
            case USER_UPLOAD_FILE -> resp = ServerController.uploadFiles(req, session);
            case USER_DOWNLOAD_FILE -> resp = ServerController.downloadFiles(req, session);
        }

        return resp;
    }
}
