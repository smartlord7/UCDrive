package server;

import businesslayer.DirectoryPermission.DirectoryPermissionDAO;
import businesslayer.SessionLog.SessionLogDAO;
import businesslayer.User.UserDAO;
import server.threads.failover.ServerListened;
import server.threads.failover.ServerListener;
import server.struct.ServerConfig;
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
        if (config.isSecondary()) {
            new ServerListener(config.getWatchedHostIp(), config.getWatchedHostPort(),
                    config.getHeartbeatInterval(), config.getMaxFailedHeartbeat(), config.getHeartbeatTimeout());
        } else {
            new ServerListened(config.getWatchedHostPort());
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
        ServerUserSessions sessions = new ServerUserSessions();

        new Thread(new ServerCommandChannelHandler(config.getCommandPort(), sessions)).start();
        new Thread(new ServerDataChannelHandler(config.getDataPort(), sessions)).start();

    }

    public ServerMain(String[] args) throws IOException, InterruptedException {
        config = ServerConfig.getFromFile(args[0]);
        run();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        new ServerMain(args);
    }
}