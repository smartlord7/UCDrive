package businesslayer.DirectoryPermission;

import datalayer.enumerate.DirectoryPermissionEnum;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DirectoryPermissionDAO {
    public static Connection connection;

    public static DirectoryPermissionEnum getDirectoryPermission(int userId, String directory) throws SQLException {
        DirectoryPermissionEnum permission = DirectoryPermissionEnum.NONE;
        PreparedStatement stmt = connection.prepareStatement("SELECT PermissionType FROM DirectoryPermission WHERE UserId = ? AND ? LIKE Directory + '%'");

        stmt.setInt(1, userId);
        stmt.setString(2, directory);
        ResultSet res = stmt.executeQuery();

        while (res.next()) {
            permission = DirectoryPermissionEnum.toEnum(res.getInt(1));
        }

        return permission;
    }
}
