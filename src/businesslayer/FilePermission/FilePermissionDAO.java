package businesslayer.FilePermission;

import businesslayer.base.BaseDAO;
import businesslayer.base.DAOResult;
import businesslayer.base.DAOResultStatusEnum;
import datalayer.enumerate.FilePermissionEnum;
import datalayer.model.FilePermission.FilePermission;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FilePermissionDAO implements BaseDAO, Serializable {
    public static Connection connection;

    public static DAOResult create(FilePermission filePerm) throws NoSuchMethodException, SQLException {
        String sql;
        PreparedStatement stmt;

        if (filePerm.getPermission() == FilePermissionEnum.NONE) {
            return new DAOResult(false, DAOResultStatusEnum.IGNORED, null, filePerm,
                    DAOResult.class, FilePermission.class, DAOResult.class.getEnclosingMethod().getName());
        }

        sql = "INSERT INTO FilePermission (Directory, PermissionType, UserId)" +
                "VALUES (?, ?, ?)";

        stmt = connection.prepareStatement(sql);
        stmt.setString(1, filePerm.getDirectory());
        stmt.setInt(2, filePerm.getPermission().ordinal());
        stmt.setInt(3, filePerm.getUserId());
        stmt.executeUpdate();

        connection.commit();

        return new DAOResult(false, DAOResultStatusEnum.SUCCESS, null, filePerm,
                FilePermissionDAO.class, FilePermission.class, FilePermissionDAO.class.getMethod("create", FilePermission.class).getName());
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
