import businesslayer.User.UserDAO;
import datalayer.model.User.User;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    private Connection conn;

    private void addDefaults() throws NoSuchAlgorithmException, SQLException {
        User u = new User();
        u.setUserName("administrator");
        u.setPassword("administrator123#");

        UserDAO.create(u);
    }

    private void run() {
        try {
            conn = ConnectionFactory.getConnection();
            conn.setAutoCommit(false);

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }

        UserDAO.connection = conn;

    }

    public Main() {
        run();
    }

    public static void main(String[] args) {
        new Main();
    }
}