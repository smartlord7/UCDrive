/**------------ucDrive: REPOSITÓRIO DE FICHEIROS NA UC------------
 University of Coimbra
 Degree in Computer Science and Engineering
 Sistemas Distribuidos
 3rd year, 2nd semester
 Authors:
 Sancho Amaral Simões, 2019217590, uc2019217590@student.uc.pt
 Tiago Filipe Santa Ventura, 2019243695, uc2019243695@student.uc.pt
 Coimbra, 2nd April 2022
 ---------------------------------------------------------------------------*/

package server.threads.connections;

import businesslayer.Exception.ExceptionDAO;
import businesslayer.base.DAOResult;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import datalayer.model.Exception.Exception;
import protocol.clientserver.Request;
import protocol.clientserver.RequestMethodEnum;
import protocol.clientserver.Response;
import protocol.clientserver.ResponseStatusEnum;
import server.ServerController;
import server.struct.ServerUserSession;
import server.threads.failover.FailoverDataHelper;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * Class that has the most methods to handle the server command channel connection.
 */
public class ServerCommandChannelConnection extends Thread {

    // region Private properties

    private final int connectionId;
    private final ServerUserSession session;
    private Request req;
    private Response resp;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Socket clientSocket;

    // endregion Private properties

    // region Private methods

    private void logException(java.lang.Exception e) {
        DAOResult result = null;
        try {
            result = ExceptionDAO.create(new Exception(e, session.getUserId(),
                    "CMD CHANNEL @" + clientSocket.getLocalSocketAddress(),
                    clientSocket.getInetAddress().toString()));
        } catch (SQLException | NoSuchMethodException ex) {
            System.out.println("Error: could not log exception.");
            ex.printStackTrace();
        }

        try {
            FailoverDataHelper.sendDMLFailoverData(session, result);
        } catch (IOException ex) {
            System.out.println("Error: Exception failed to be sent to secondary server.");
            ex.printStackTrace();
        }
    }

    private void sendResponse() throws IOException {
        out.writeObject(resp);
        out.flush();
        out.reset();
    }

    private void sendError(java.lang.Exception e) throws IOException {
        resp = new Response();
        HashMap<String, String> errors = new HashMap<>();
        errors.put("Internal Server Error", e.getMessage());
        resp.setStatus(ResponseStatusEnum.ERROR);
        resp.setErrors(errors);
        sendResponse();
    }

    // endregion Private methods

    // region Public methods

    /**
     * Constructor method.
     * @param socket is the socket.
     * @param id is the connection id.
     * @param session is the current session.
     */
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
        } catch (java.lang.Exception e) {
            logException(e);
        }
    }

    /**
     * Method that sends a request and writes the response to the client.
     */
    @Override
    public void run() {
        while (true) {
            try {
                handleRequest();
                sendResponse();
            }  catch (java.lang.Exception e) {
                Class<? extends java.lang.Exception> eClass = e.getClass();
                logException(e);

                if (eClass == SocketException.class || eClass == EOFException.class) {
                    return;
                } else {
                    try {
                        sendError(e);
                    } catch (IOException ex) {
                        logException(ex);
                    }
                }
            }
        }
    }

    // endregion Public methods

    // region Private methods

    /**
     *  Method that handles the request.
     * @throws SQLException - whenever a database related error occurs.
     * @throws NoSuchAlgorithmException - when a particular cryptographic algorithm is requested but is not available in the environment.
     * @throws IOException - whenever an input or output operation is failed or interrupted.
     */
    private void handleRequest() throws SQLException, NoSuchAlgorithmException, IOException, NoSuchMethodException, ClassNotFoundException {
        req = (Request) in.readObject();
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
    }

    // endregion Private methods

}
