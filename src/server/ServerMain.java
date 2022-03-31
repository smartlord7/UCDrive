package server;

import businesslayer.DirectoryPermission.DirectoryPermissionDAO;
import businesslayer.SessionLog.SessionLogDAO;
import businesslayer.User.UserDAO;
import com.google.gson.Gson;
import datalayer.enumerate.DirectoryPermissionEnum;
import datalayer.model.User.User;
import sync.UserSessions;
import util.Const;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
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

    private void run() throws IOException {
        try {
            Connection conn = ConnectionFactory.getConnection(config.getInstance(),
                    config.getDatabase(), config.getUser(), config.getPassword());
            conn.setAutoCommit(false);
            config.setConn(conn);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        init();
        UserSessions sessions = new UserSessions();

        new Thread(new ServerCommandChannelHandler(config.getCommandPort(), sessions)).start();
        new Thread(new ServerDataChannelHandler(config.getDataPort(), sessions)).start();

        if (config.isSecondary()) {
            new ServerWatcherWorker(config.getWatchedHostIp(), config.getWatchedHostPort(),
                    config.getHeartbeatInterval(), config.getMaxFailedHeartbeat(), config.getHeartbeatTimeout());
        } else {
            new ServerWatchedWorker(config.getWatchedHostPort());
        }
    }

    public ServerMain(String[] args) throws IOException {
        config = ServerConfig.getFromFile(args[0]);
        run();
    }

    public static void main(String[] args) throws IOException {
        new ServerMain(args);
    }
}