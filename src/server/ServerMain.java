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

package server;

import businesslayer.Exception.ExceptionDAO;
import businesslayer.FilePermission.FilePermissionDAO;
import businesslayer.SessionLog.SessionLogDAO;
import businesslayer.User.UserDAO;
import datalayer.model.Exception.Exception;
import protocol.failover.redundancy.FailoverData;
import server.threads.failover.*;
import server.struct.ServerConfig;
import server.threads.handlers.ServerCommandChannelHandler;
import server.threads.handlers.ServerDataChannelHandler;
import server.struct.ServerUserSessions;
import util.Const;
import util.StringUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Class that has all the main server methods related to the database.
 */
public class ServerMain {

    // region Private properties

    private ServerConfig config = new ServerConfig();

    // endregion Private properties

    // region Private methods

    private void logException(java.lang.Exception e, Connection c) {
        if (c != null) {
            try {
                ExceptionDAO.create(new Exception(e, -1,
                        "SERVER INIT", null));
            } catch (SQLException | NoSuchMethodException ex) {
                System.out.println("Error: could not log exception.");
                ex.printStackTrace();
            }
        } else {
            e.printStackTrace();
        }
    }

    /**
     * Method used to establish the Database connection.
     */
    private void setDBConnection() {
        try {
            Connection conn = ConnectionFactory.getConnection(config.getInstance(),
                    config.getDatabase(), config.getUser(), config.getPassword());
            conn.setAutoCommit(false);
            config.setConn(conn);

        } catch (SQLException e) {
            System.out.println("Error: could not establish connection to SQLServer instance");
            e.printStackTrace();
            System.exit(0);
        }
    }

    /**
     *  Method that initializes the DAO's connections and the users directory.
     */
    private void init() throws IOException {
        UserDAO.connection = config.getConn();
        SessionLogDAO.connection = config.getConn();
        FilePermissionDAO.connection = config.getConn();
        ExceptionDAO.connection = config.getConn();

        Path p = Paths.get(Const.USERS_FOLDER_NAME);
        if (!Files.exists(p)) {
            Files.createDirectory(p);
        }

        ServerController.showMenu(config.isSecondary());
    }

    /**
     * Method used to start the failsafe/redundancy related threads.
     * @throws InterruptedException - if the method is interrupted (i.e. manually stopping the program)
     */
    private void startThreads() throws InterruptedException {
        ServerUserSessions sessions = new ServerUserSessions();
        BlockingQueue<FailoverData> dataToSync = new LinkedBlockingQueue<>();

        if (config.isSecondary()) {
            new ServerSynced(config.getSyncedHostPort());
            new ServerListener(config.getListenedHostIp(), config.getListenedHostPort(),
                    config.getHeartbeatInterval(), config.getMaxFailedHeartbeat(), config.getHeartbeatTimeout());
        } else {
            new ServerListened(config.getListenedHostPort());
            new ServerSyncer(config.getSyncedHostIp(), config.getSyncedHostPort(), dataToSync);
        }

        new Thread(new ServerCommandChannelHandler(config.getCommandPort(), sessions, dataToSync)).start();
        new Thread(new ServerDataChannelHandler(config.getDataPort(), sessions, dataToSync)).start();

    }

    /**
     * Method that runs all the methods needed for the program startup.
     * @throws InterruptedException - if the method is interrupted (i.e. manually stopping the program)
     */
    private void run() throws InterruptedException, IOException {
        setDBConnection();
        init();
        startThreads();
    }

    // endregion Private methods

    // region Public methods

    /**
     *  Server Main method.
     * @param args main arguments.
     */
    public ServerMain(String[] args) {
        try {
            config = ServerConfig.getFromFile(args[0]);
            run();
        } catch (java.lang.Exception e) {
            if (config.getConn() != null) {
                logException(e, config.getConn());
            }
        }
    }

    /**
     * Main method.
     * @param args main arguments
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Error: a config file must be provided as an argument.");
            return;
        }
        new ServerMain(args);
    }

    // endregion Public methods

}