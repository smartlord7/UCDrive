package businesslayer.Exception;

import businesslayer.base.DAOResult;
import businesslayer.base.DAOResultStatusEnum;
import datalayer.model.Exception.Exception;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Class that has the exception DAO methods.
 */
public class ExceptionDAO {

    // region Public properties

    public static Connection connection;

    // endregion Public properties

    public static DAOResult create(Exception exception) {
        String sql;
        PreparedStatement stmt;

        sql = "INSERT INTO Exception (UserId, Message, Stack, Source, CreateDate) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)";

        try {
            stmt = connection.prepareStatement(sql);
            stmt.setInt(1, exception.getUserId());
            stmt.setString(2, exception.getMessage());
            stmt.setString(3, exception.getStack());
            stmt.setString(4, exception.getSource());

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

        try {
            return new DAOResult(false, DAOResultStatusEnum.SUCCESS, null, exception,
                    ExceptionDAO.class, Exception.class, ExceptionDAO.class.getMethod("create", Exception.class).getName());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        return null;
    }
}
