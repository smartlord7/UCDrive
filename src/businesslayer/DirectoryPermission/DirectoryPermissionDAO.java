package businesslayer.DirectoryPermission;

import datalayer.enumerate.DirectoryPermissionEnum;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DirectoryPermissionDAO {
    public static Connection connection;

    public static void create(int userId, String directory, DirectoryPermissionEnum permission) {
        PreparedStatement stmt;

        try {
            if (permission == DirectoryPermissionEnum.NONE) {
                return;
            }

            stmt = connection.prepareStatement(
                    "INSERT INTO DirectoryPermission (Directory, PermissionType, UserId)" +
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

    public static DirectoryPermissionEnum getPermission(int userId, String directory) throws SQLException {
        DirectoryPermissionEnum permission;
        PreparedStatement stmt;

        permission = DirectoryPermissionEnum.NONE;
        stmt = connection.prepareStatement(
                "SELECT PermissionType FROM DirectoryPermission" +
                        " WHERE UserId = ? AND ? LIKE Directory + '%'");

        stmt.setInt(1, userId);
        stmt.setString(2, directory);
        ResultSet res = stmt.executeQuery();

        while (res.next()) {
            permission = DirectoryPermissionEnum.toEnum(res.getInt(1));
        }

        return permission;
    }
}
