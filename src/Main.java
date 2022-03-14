import businesslayer.SessionLog.SessionLogDAO;
import businesslayer.User.UserDAO;
import datalayer.model.User.User;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    private Connection conn;

    private void test() throws SQLException, NoSuchAlgorithmException {
        User u = new User();
        u.setUserName("administrator");
        u.setPassword("administrator123##");

        System.out.println("Auth: " + UserDAO.authenticate(u));
        System.out.println("Permissions: " + UserDAO.getDirectoryPermissions(1, "test"));
        System.out.println("Change password: " + UserDAO.changePassword(u, "administrator123##"));
        System.out.println("Last session dir: " + SessionLogDAO.getDirectoryFromLastSession(1));
    }

    private void initConnections() {
        UserDAO.connection = conn;
        SessionLogDAO.connection = conn;
    }

    private void run() throws SQLException, NoSuchAlgorithmException {
        try {
            conn = ConnectionFactory.getConnection();
            conn.setAutoCommit(false);

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }

        initConnections();
        test();
    }

    public Main() throws SQLException, NoSuchAlgorithmException {
        run();
    }

    public static void main(String[] args) throws SQLException, NoSuchAlgorithmException {
        new Main();
    }
}