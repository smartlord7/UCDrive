package businesslayer.FilePermission;

import datalayer.enumerate.FilePermissionEnum;
import datalayer.model.FilePermission.FilePermission;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FilePermissionDAO {
    public static Connection connection;

    public static void create(FilePermission filePerm) {
        PreparedStatement stmt;

        try {
            if (filePerm.getPermission() == FilePermissionEnum.NONE) {
                return;
            }

            stmt = connection.prepareStatement(
                    "INSERT INTO FilePermission (Directory, PermissionType, UserId)" +
                            "VALUES (?, ?, ?)");
            stmt.setString(1, filePerm.getDirectory());
            stmt.setInt(2, filePerm.getPermission().ordinal());
            stmt.setInt(3, filePerm.getUserId());
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

    public static FilePermissionEnum getPermission(FilePermission filePerm) throws SQLException {
        FilePermissionEnum permission;
        PreparedStatement stmt;

        permission = FilePermissionEnum.NONE;
        stmt = connection.prepareStatement(
                "SELECT PermissionType FROM FilePermission" +
                        " WHERE UserId = ? AND ? LIKE Directory + '%'");

        stmt.setInt(1, filePerm.getUserId());
        stmt.setString(2, filePerm.getDirectory());
        ResultSet res = stmt.executeQuery();

        while (res.next()) {
            permission = FilePermissionEnum.toEnum(res.getInt(1));
        }

        return permission;
    }
}
