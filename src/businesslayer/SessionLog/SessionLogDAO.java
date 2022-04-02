package businesslayer.SessionLog;

import datalayer.model.SessionLog.SessionLog;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SessionLogDAO {
    public static Connection connection;

    /**
     * Method that creates the session,
     * @param sessionLog is the session log.
     * @throws SQLException - whenever a database related error occurs.
     */
    public static void create(SessionLog sessionLog) throws SQLException {
        PreparedStatement stmt;

        stmt = connection.prepareStatement("INSERT INTO SessionLog " +
                "(StartDate, EndDate, LastDirectory, UserId) VALUES " +
                "(?, ?, ?, ?)");
        stmt.setTimestamp(1, sessionLog.getStartDate());
        stmt.setTimestamp(2, sessionLog.getEndDate());
        stmt.setString(3, sessionLog.getLastDirectory());
        stmt.setInt(4, sessionLog.getUserId());

        connection.commit();
    }

    /**
     * Method that gets the directory from the last session.
     * @param userId is the user id.
     * @return the last directory.
     * @throws SQLException - whenever a database related error occurs.
     */
    public static String getDirectoryFromLastSession(int userId) throws SQLException {
        PreparedStatement stmt;
        ResultSet res;

        stmt = connection.prepareStatement("SELECT TOP 1 [LastDirectory] " +
                "FROM [UCDriveMainServer].[dbo].[SessionLog] " +
                "WHERE UserId = ? " +
                "ORDER BY EndDate DESC ");
        stmt.setInt(1, userId);

        res = stmt.executeQuery();

        String lastDirectory = null;
        while (res.next()) {
            lastDirectory = res.getString(1);
        }

        return lastDirectory;
    }
}
