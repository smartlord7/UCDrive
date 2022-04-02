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

package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Class that has the connection to the database methods.
 */

public class ConnectionFactory {

    // region Public properties

    public static final String HOST_NAME = "localhost";
    public static final String CONNECTION_PARAMS = "encrypt=true;trustServerCertificate=true";
    public static final String DEFAULT_INSTANCE = "sqlexpress02";
    public static final String DEFAULT_DATABASE = "UCDriveMainServer";
    public static final String DEFAULT_USER = "ucdriveadmin";

    // endregion Public properties

    // region Private methods

    /**
     * Method that prints the connection information.
     * @param conn is the current connection.
     * @throws SQLException - whenever a database related error occurs.
     */
    private static void printConnectionData(Connection conn) throws SQLException {
        DatabaseMetaData dm = conn.getMetaData();
        System.out.println("Connected successfully!");
        System.out.println("Driver name: " + dm.getDriverName());
        System.out.println("Driver version: " + dm.getDriverVersion());
        System.out.println("Product name: " + dm.getDatabaseProductName());
        System.out.println("Product version: " + dm.getDatabaseProductVersion());
    }

    // endregion Private methods

    // region Public methods

    /**
     * Method used to set up the connection to the database.
     * @param instance is the current instance.
     * @param database is the used database.
     * @param user is the user connecting to the database.
     * @param pass is the user password connecting to the database.
     * @return the connection.
     * @throws SQLException - whenever a database related error occurs.
     */
    public static Connection getConnection(String instance, String database, String user, String pass) throws SQLException {
        String connString = "jdbc:sqlserver://" + HOST_NAME + "\\" + instance + ";databaseName=" + database + ";" + CONNECTION_PARAMS;
        return DriverManager.getConnection(connString, user, pass);
    }

    /**
     * Method that gets the current connection via the terminal.
     * @return the current connection.
     * @throws IOException - whenever an input or output operation is failed or interrupted.
     * @throws SQLException - whenever a database related error occurs.
     */
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
        if (password.length() < 1) {
            password = "ucdriveadmin123#";
        }

        return getConnection(instance, database, user, password);
    }

    // endregion Public methods

}
