package businesslayer.User;

import datalayer.enumerate.FilePermissionEnum;
import datalayer.model.User.User;
import helper.SHA256Hasher;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.HashSet;

public class UserDAO {
    public static Connection connection;

    public static int create(User user) throws NoSuchAlgorithmException {
        PreparedStatement stmt = null;
        try {
            stmt = connection.prepareStatement("INSERT INTO [User] (UserName, PasswordHash, CreateDate) VALUES (?, ?, ?)");
            stmt.setString(1, user.getUserName());
            stmt.setString(2, SHA256Hasher.hashSHA256(user.getPassword()));
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

    public static int authenticate(User user) throws SQLException, NoSuchAlgorithmException {
        PreparedStatement stmt = connection.prepareStatement("SELECT PasswordHash FROM [User] WHERE UserName = ? ");

        stmt.setString(1, user.getUserName());
        ResultSet res = stmt.executeQuery();

        String passwordHash = null;

        while (res.next()) {
            passwordHash = res.getString(1);
        }

        if (passwordHash == null) {
            // Wrong userName
            return -1;
        }

        if (!SHA256Hasher.hashSHA256(user.getPassword()).equals(passwordHash)) {
            // Wrong password
            return -2;
        }

        return 0;
    }

    public static HashSet<FilePermissionEnum> getDirectoryPermissions(int userId, String directory) throws SQLException {
        HashSet<FilePermissionEnum> permissions = new HashSet<FilePermissionEnum>();
        PreparedStatement stmt = connection.prepareStatement("SELECT PermissionType FROM DirectoryPermission WHERE UserId = ? AND Directory = ?");

        stmt.setInt(1, userId);
        stmt.setString(2, directory);
        ResultSet res = stmt.executeQuery();

        while (res.next()) {
            permissions.add(FilePermissionEnum.toEnum(res.getInt(1)));
        }

        return permissions;
    }
}
