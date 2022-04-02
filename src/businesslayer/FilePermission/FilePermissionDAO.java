/**------------ucDrive: REPOSITÓRIO DE FICHEIROS NA UC------------
 University of Coimbra
 Degree in Computer Science and Engineering
 Sistemas Distribuidos
 3rd year, 2nd semester
 Authors:
 Sancho Amaral Simões, 2019217590, uc2019217590@student.uc.pt
 Tiago Filipe Santa Ventura, 2019243695, uc2019243695@student.uc.pt
 Coimbra, 2nd April 2022
 ---------------------------------------------------------------------------*/

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

/**
 * Class that has the file permission DAO methods.
 */

public class FilePermissionDAO implements BaseDAO, Serializable {

    // region Public properties

    public static Connection connection;

    // endregion Public properties

    // region Public methods

    /**
     * Method that creates the DAO result.
     * @param filePerm are the file permissions.
     * @return the created DAO result.
     * @throws NoSuchMethodException - when a particular method cannot be found.
     * @throws SQLException - whenever a database related error occurs.
     */
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

    /**
     * Method that gets the file permissions.
     * @param filePerm are the file permissions.
     * @return the file permissions.
     * @throws SQLException - whenever a database related error occurs.
     */
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

    // endregion Public methods

}
