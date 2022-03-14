import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
    public static final String HOST_NAME = "localhost";
    public static final String CONNECTION_PARAMS = "encrypt=true;trustServerCertificate=true";
    public static final String DEFAULT_INSTANCE = "sqlexpress02";
    public static final String DEFAULT_DATABASE = "UCDriveMainServer";
    public static final String DEFAULT_USER = "ucdriveadmin";

    private static void printConnectionData(Connection conn) throws SQLException {
        DatabaseMetaData dm = conn.getMetaData();
        System.out.println("Connected successfully!");
        System.out.println("Driver name: " + dm.getDriverName());
        System.out.println("Driver version: " + dm.getDriverVersion());
        System.out.println("Product name: " + dm.getDatabaseProductName());
        System.out.println("Product version: " + dm.getDatabaseProductVersion());
    }

    public static Connection getConnection(String instance, String database, String user, String pass) throws SQLException {
        String connString = "jdbc:sqlserver://" + HOST_NAME + "\\" + instance + ";databaseName=" + database + ";" + CONNECTION_PARAMS;
        Connection conn = DriverManager.getConnection(connString, user, pass);
        printConnectionData(conn);

        return conn;
    }

    public static Connection getConnection() throws IOException, SQLException {
        String instance;
        String database;
        String user;
        String password;
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Instance: ");
        instance = in.readLine();

        if (instance.length() < 1) {
            instance = DEFAULT_INSTANCE;
        }

        System.out.println("Database: ");
        database = in.readLine();

        if (database.length() < 1) {
            database = DEFAULT_DATABASE;
        }

        System.out.println("User: ");
        user = in.readLine();

        if (user.length() < 1) {
            user = DEFAULT_USER;
        }

        System.out.println("Password: ");
        password = in.readLine();

        return getConnection(instance, database, user, password);
    }
}
