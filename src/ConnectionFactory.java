import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
    public static final String HOST_NAME = "localhost";
    public static final String CONNECTION_PARAMS = ";encrypt=true;trustServerCertificate=true";

    private static void printConnectionData(Connection conn) throws SQLException {
        DatabaseMetaData dm = conn.getMetaData();
        System.out.println("Driver name: " + dm.getDriverName());
        System.out.println("Driver version: " + dm.getDriverVersion());
        System.out.println("Product name: " + dm.getDatabaseProductName());
        System.out.println("Product version: " + dm.getDatabaseProductVersion());
    }

    public static Connection getConnection(String instance, String user, String pass) throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:sqlserver://" + HOST_NAME + "\\" + instance + ";" + CONNECTION_PARAMS, user, pass);
        printConnectionData(conn);

        return conn;
    }

    public static Connection getConnection() throws IOException, SQLException {
        String instance;
        String user;
        String password;
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Instance name: ");
        instance = in.readLine();

        System.out.println("User: ");
        user = in.readLine();

        System.out.println("Password: ");
        password = in.readLine();

        return getConnection(instance, user, password);
    }

}
