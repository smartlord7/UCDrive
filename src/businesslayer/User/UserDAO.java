package businesslayer.User;

import datalayer.model.User.User;
import util.Const;
import util.Hasher;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

public class UserDAO {
    public static Connection connection;

    public static void create(User user) throws NoSuchAlgorithmException {
        PreparedStatement stmt;
        try {
            stmt = connection.prepareStatement("INSERT INTO [User] (UserName, PasswordHash, CreateDate) VALUES (?, ?, CURRENT_TIMESTAMP)");
            stmt.setString(1, user.getUserName());
            stmt.setString(2, Hasher.hashString(user.getPassword(), Const.PASSWORD_HASH_ALGORITHM));

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

            if (!Hasher.hashString(user.getPassword(), Const.PASSWORD_HASH_ALGORITHM).equals(passwordHash)) {
                // Wrong password
                return -2;
            }

            stmt = connection.prepareStatement("UPDATE [User] SET PasswordHash = ? WHERE UserId = ?");
            stmt.setString(1, Hasher.hashString(user.getNewPassword(), Const.PASSWORD_HASH_ALGORITHM));
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

        if (!Hasher.hashString(user.getPassword(), Const.PASSWORD_HASH_ALGORITHM).equals(passwordHash)) {
            // Wrong password
            return -2;
        }

        return 0;
    }
}
