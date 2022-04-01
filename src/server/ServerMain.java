package server;

import businesslayer.DirectoryPermission.DirectoryPermissionDAO;
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

public class ServerMain {
    private final ServerConfig config;

    private void init() throws IOException {
        UserDAO.connection = config.getConn();
        SessionLogDAO.connection = config.getConn();
        DirectoryPermissionDAO.connection = config.getConn();

        Path p = Paths.get(Const.USERS_FOLDER_NAME);
        if (!Files.exists(p)) {
            Files.createDirectory(p);
        }
    }

    private void run() throws IOException, InterruptedException {
        ServerUserSessions sessions = new ServerUserSessions();
        BlockingQueue<FailoverData> dataToSync = new LinkedBlockingQueue<>();

        if (config.isSecondary()) {
            new ServerListener(config.getListenedHostIp(), config.getListenedHostPort(),
                    config.getHeartbeatInterval(), config.getMaxFailedHeartbeat(), config.getHeartbeatTimeout());
            new ServerSynced(config.getSyncedHostPort());
        } else {
            new ServerListened(config.getListenedHostPort());
            new ServerSyncer(config.getSyncedHostIp(), config.getSyncedHostPort(), dataToSync);
        }

        try {
            Connection conn = ConnectionFactory.getConnection(config.getInstance(),
                    config.getDatabase(), config.getUser(), config.getPassword());
            conn.setAutoCommit(false);
            config.setConn(conn);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        init();

        new Thread(new ServerCommandChannelHandler(config.getCommandPort(), sessions, dataToSync)).start();
        new Thread(new ServerDataChannelHandler(config.getDataPort(), sessions, dataToSync)).start();

    }

    public ServerMain(String[] args) throws IOException, InterruptedException {
        config = ServerConfig.getFromFile(args[0]);
        run();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        new ServerMain(args);
    }
}