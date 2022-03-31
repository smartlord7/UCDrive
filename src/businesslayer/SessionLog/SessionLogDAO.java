package businesslayer.SessionLog;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SessionLogDAO {
    public static Connection connection;

    public static void create() {

    }

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
