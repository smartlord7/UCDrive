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

package server.struct;

import com.google.gson.Gson;

import java.io.*;
import java.sql.Connection;

/**
 * Class that has the server configuration methods.
 */

public class ServerConfig {
    //TODO field validations and separation in two configs (Main and Secondary server)

    // region Private properties

    private boolean isSecondary;
    private int commandPort;
    private int dataPort;
    private int listenedHostPort;
    private int syncedHostPort;
    private int maxFailedHeartbeat;
    private int heartbeatInterval;
    private int heartbeatTimeout;
    private String listenedHostIp;
    private String syncedHostIp;
    private String instance;
    private String database;
    private String user;
    private String password;
    private Connection conn;

    // endregion Private properties

    // region Constructors

    /**
     * Constructor method.
     */
    public ServerConfig() {
    }

    /**
     * Method to read from the file.
     * @param path is the file path.
     * @return the config red from the file.
     * @throws IOException - whenever an input or output operation is failed or interrupted.
     */
    public static ServerConfig getFromFile(String path) throws IOException {
        String line;
        ServerConfig config;
        StringBuilder configStr;
        BufferedReader reader;
        Gson gson;

        gson = new Gson();
        configStr = new StringBuilder();
        reader = new BufferedReader(new FileReader(path));

        while ((line = reader.readLine()) != null && line.length() > 0) {
            configStr.append(line);
        }

        config = gson.fromJson(configStr.toString(), ServerConfig.class);

        return config;
    }

    // endregion Constructors

    // region Public methods

    // region Getters and Setters

    public int getCommandPort() {
        return commandPort;
    }

    public void setCommandPort(int commandPort) {
        this.commandPort = commandPort;
    }

    public int getDataPort() {
        return dataPort;
    }

    public void setDataPort(int dataPort) {
        this.dataPort = dataPort;
    }

    public int getMaxFailedHeartbeat() {
        return maxFailedHeartbeat;
    }

    public void setMaxFailedHeartbeat(int maxFailedHeartbeat) {
        this.maxFailedHeartbeat = maxFailedHeartbeat;
    }

    public int getHeartbeatInterval() {
        return heartbeatInterval;
    }

    public void setHeartbeatInterval(int heartbeatInterval) {
        this.heartbeatInterval = heartbeatInterval;
    }

    public String getInstance() {
        return instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Connection getConn() {
        return conn;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    public int getListenedHostPort() {
        return listenedHostPort;
    }

    public void setListenedHostPort(int listenedHostPort) {
        this.listenedHostPort = listenedHostPort;
    }

    public int getHeartbeatTimeout() {
        return heartbeatTimeout;
    }

    public void setHeartbeatTimeout(int heartbeatTimeout) {
        this.heartbeatTimeout = heartbeatTimeout;
    }

    public String getListenedHostIp() {
        return listenedHostIp;
    }

    public void setListenedHostIp(String listenedHostIp) {
        this.listenedHostIp = listenedHostIp;
    }

    public boolean isSecondary() {
        return isSecondary;
    }

    public void setSecondary(boolean secondary) {
        isSecondary = secondary;
    }

    public int getSyncedHostPort() {
        return syncedHostPort;
    }

    public void setSyncedHostPort(int syncedHostPort) {
        this.syncedHostPort = syncedHostPort;
    }

    public String getSyncedHostIp() {
        return syncedHostIp;
    }

    public void setSyncedHostIp(String syncedHostIp) {
        this.syncedHostIp = syncedHostIp;
    }

    // endregion Getters and Setters

    // endregion Public methods

}
