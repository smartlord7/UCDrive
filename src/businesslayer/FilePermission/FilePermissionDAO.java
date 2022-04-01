package businesslayer.FilePermission;

import datalayer.enumerate.FilePermissionEnum;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FilePermissionDAO {
    public static Connection connection;

    public static void create(int userId, String directory, FilePermissionEnum permission) {
        PreparedStatement stmt;

        try {
            if (permission == FilePermissionEnum.NONE) {
                return;
            }

            stmt = connection.prepareStatement(
                    "INSERT INTO FilePermission (Directory, PermissionType, UserId)" +
                            "VALUES (?, ?, ?)");
            stmt.setString(1, directory);
            stmt.setInt(2, permission.ordinal());
            stmt.setInt(3, userId);
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

    public static FilePermissionEnum getPermission(int userId, String directory) throws SQLException {
        FilePermissionEnum permission;
        PreparedStatement stmt;

        permission = FilePermissionEnum.NONE;
        stmt = connection.prepareStatement(
                "SELECT PermissionType FROM FilePermission" +
                        " WHERE UserId = ? AND ? LIKE Directory + '%'");

        stmt.setInt(1, userId);
        stmt.setString(2, directory);
        ResultSet res = stmt.executeQuery();

        while (res.next()) {
            permission = FilePermissionEnum.toEnum(res.getInt(1));
        }

        return permission;
    }
}
