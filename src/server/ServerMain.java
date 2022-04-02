package server;

import businesslayer.FilePermission.FilePermissionDAO;
import businesslayer.SessionLog.SessionLogDAO;
import businesslayer.User.UserDAO;
import protocol.failover.redundancy.FailoverData;
import server.threads.failover.ServerListened;
import server.threads.failover.ServerListener;
import server.struct.ServerConfig;
import server.threads.failover.ServerSynced;
import server.threads.failover.ServerSyncer;
import server.threads.handlers.ServerCommandChannelHandler;
import server.threads.handlers.ServerDataChannelHandler;
import server.struct.ServerUserSessions;
import util.Const;
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

    private final ServerConfig config;

    // endregion Private properties

    // region Private methods

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
    private void init() {
        try {

            UserDAO.connection = config.getConn();
            SessionLogDAO.connection = config.getConn();
            FilePermissionDAO.connection = config.getConn();

            Path p = Paths.get(Const.USERS_FOLDER_NAME);
            if (!Files.exists(p)) {
                Files.createDirectory(p);
            }
        } catch (IOException i) {
            System.out.println("Error: the input/output operation has failed.");
        }
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
    private void run() throws InterruptedException {
        setDBConnection();
        init();
        startThreads();
    }

    // endregion Private methods

    // region Public methods

    /**
     *  Server Main method.
     * @param args main arguments.
     * @throws IOException - whenever an input or output operation is failed or interpreted
     * @throws InterruptedException - if the method is interrupted (i.e. manually stopping the program)
     */
    public ServerMain(String[] args) throws IOException, InterruptedException {
        config = ServerConfig.getFromFile(args[0]);
        run();
    }

    /**
     * Main method.
     * @param args main arguments
     * @throws IOException - whenever an input or output operation is failed or interpreted
     * @throws InterruptedException - if the method is interrupted (i.e. manually stopping the program)
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        new ServerMain(args);
    }

    // endregion Public methods

}