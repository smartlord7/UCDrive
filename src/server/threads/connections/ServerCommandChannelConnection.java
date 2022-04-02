package server.threads.connections;

import protocol.clientserver.Request;
import protocol.clientserver.RequestMethodEnum;
import protocol.clientserver.Response;
import protocol.failover.redundancy.FailoverData;
import server.ServerController;
import server.struct.ServerUserSession;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;

/**
 * Class that has the most methods to handle the server command channel connection.
 */
public class ServerCommandChannelConnection extends Thread {

    // region Private properties

    private final int connectionId;
    private final ServerUserSession session;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private final BlockingQueue<FailoverData> dataToSync;
    private Socket clientSocket;

    // endregion Private properties

    // region Public methods

    /**
     * Constructor method.
     * @param socket is the socket.
     * @param id is the connection id.
     * @param session is the current session.
     * @param dataToSync is the data to sync.
     */
    public ServerCommandChannelConnection(Socket socket, int id, ServerUserSession session, BlockingQueue<FailoverData> dataToSync) {
        this.connectionId = id;
        this.session = session;
        this.dataToSync = dataToSync;
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

    /**
     * Method that sends a request and writes the response to the client.
     */
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

    // endregion Public methods

    // region Private methods

    /**
     *  Method that handles the request.
     * @param req is the request sent to the server.
     * @return the server response.
     * @throws SQLException - whenever a database related error occurs.
     * @throws NoSuchAlgorithmException - when a particular cryptographic algorithm is requested but is not available in the environment.
     * @throws IOException - whenever an input or output operation is failed or interpreted.
     * @throws InterruptedException - if the method is interrupted (i.e. manually stopping the program)
     */
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

    // endregion Private methods

}
