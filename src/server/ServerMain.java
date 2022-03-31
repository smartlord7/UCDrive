package server;

import businesslayer.DirectoryPermission.DirectoryPermissionDAO;
import businesslayer.SessionLog.SessionLogDAO;
import businesslayer.User.UserDAO;
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
    private Connection conn;
    private static int commandPort;
    private static int dataPort;

    private void test() throws SQLException, NoSuchAlgorithmException {
        User u = new User();
        u.setUserName("administrator");
        u.setPassword("administrator123##");

        System.out.println("Auth: " + UserDAO.authenticate(u));
        System.out.println("Permission: " + DirectoryPermissionDAO.getDirectoryPermission(1, "C:\\Users\\ssimoes\\Documents\\GitRepos\\UCDrive\\src\\workspaces"));
        u.setNewPassword("administrator123##");
        System.out.println("Change password: " + UserDAO.changePassword(u));
        System.out.println("Last session dir: " + SessionLogDAO.getDirectoryFromLastSession(1));
        DirectoryPermissionDAO.addDirectoryPermission(1, "users\\administrator", DirectoryPermissionEnum.READ_WRITE);
    }

    private void init() throws IOException {
        UserDAO.connection = conn;
        SessionLogDAO.connection = conn;
        DirectoryPermissionDAO.connection = conn;

        Path p = Paths.get(Const.USERS_FOLDER_NAME);
        if (!Files.exists(p)) {
            Files.createDirectory(p);
        }

        commandPort = 8000;
        dataPort = 8001;
    }

    private void initConnections() {
        UserDAO.connection = conn;
        SessionLogDAO.connection = conn;
        DirectoryPermissionDAO.connection = conn;
    }

    private void run() throws SQLException, NoSuchAlgorithmException, IOException {
        try {
            conn = ConnectionFactory.getConnection();
            conn.setAutoCommit(false);

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }

        init();
        test();
        UserSessions sessions = new UserSessions();

        new Thread(new ServerCommandChannelHandler(commandPort, sessions)).start();
        new Thread(new ServerDataChannelHandler(dataPort, sessions)).start();
    }

    public ServerMain() throws SQLException, NoSuchAlgorithmException, IOException {
        run();
    }

    public static void main(String[] args) throws SQLException, NoSuchAlgorithmException, IOException {
        new ServerMain();
    }
}