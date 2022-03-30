package businesslayer.User;

import datalayer.enumerate.DirectoryPermissionEnum;
import datalayer.model.User.User;
import util.SHA256Hasher;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

public class UserDAO {
    public static Connection connection;

    public static int create(User user) throws NoSuchAlgorithmException {
        PreparedStatement stmt;
        try {
            stmt = connection.prepareStatement("INSERT INTO [User] (UserName, PasswordHash, CreateDate) VALUES (?, ?, ?)");
            stmt.setString(1, user.getUserName());
            stmt.setString(2, SHA256Hasher.hash(user.getPassword()));
            stmt.setDate(3, new Date(System.currentTimeMillis()));

            stmt.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }

        return 0;
    }

    public static int changePassword(User user) throws NoSuchAlgorithmException {
        PreparedStatement stmt;

        try {
            stmt = connection.prepareStatement("SELECT UserId, PasswordHash FROM [USER] WHERE UserName = ?");
            stmt.setString(1, user.getUserName());

            ResultSet res = stmt.executeQuery();

            int userId = -1;
            String passwordHash = null;

            while (res.next()) {
                userId = res.getInt(1);
                passwordHash = res.getString(2);
            }

            if (userId == -1) {
                // Wrong userName
                return -1;
            }

            if (!SHA256Hasher.hash(user.getPassword()).equals(passwordHash)) {
                // Wrong password
                return -2;
            }

            stmt = connection.prepareStatement("UPDATE [User] SET PasswordHash = ? WHERE UserId = ?");
            stmt.setString(1, SHA256Hasher.hash(user.getNewPassword()));
            stmt.setInt(2, userId);
            stmt.executeUpdate();
            connection.commit();

        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }

        return 0;
    }

    public static int authenticate(User user) throws SQLException, NoSuchAlgorithmException {
        PreparedStatement stmt;
        String passwordHash;
        ResultSet res;

        stmt = connection.prepareStatement("SELECT UserId, PasswordHash FROM [User] WHERE UserName = ? ");

        stmt.setString(1, user.getUserName());
        res = stmt.executeQuery();

        passwordHash = null;

        while (res.next()) {
            user.setUserId(res.getInt(1));
            passwordHash = res.getString(2);
        }

        if (passwordHash == null) {
            // Wrong userName
            return -1;
        }

        if (!SHA256Hasher.hash(user.getPassword()).equals(passwordHash)) {
            // Wrong password
            return -2;
        }

        return 0;
    }
}
