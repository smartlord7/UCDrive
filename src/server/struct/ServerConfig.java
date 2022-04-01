package server.struct;

import com.google.gson.Gson;

import java.io.*;
import java.sql.Connection;

public class ServerConfig {
    private boolean isSecondary;
    private int commandPort;
    private int dataPort;
    private int listenedHostPort;
    private int maxFailedHeartbeat;
    private int heartbeatInterval;
    private int heartbeatTimeout;
    private String instance;
    private String database;
    private String user;
    private String password;
    private String listenedHostIp;
    private Connection conn;

    public ServerConfig() {
    }

    public ServerConfig(boolean isSecondary, int commandPort, int dataPort, int listenedHostPort, int maxFailedHeartbeat, int heartbeatInterval, int heartbeatTimeout, String instance, String database, String user, String password, String listenedHostIp, Connection conn) {
        this.isSecondary = isSecondary;
        this.commandPort = commandPort;
        this.dataPort = dataPort;
        this.listenedHostPort = listenedHostPort;
        this.maxFailedHeartbeat = maxFailedHeartbeat;
        this.heartbeatInterval = heartbeatInterval;
        this.heartbeatTimeout = heartbeatTimeout;
        this.instance = instance;
        this.database = database;
        this.user = user;
        this.password = password;
        this.listenedHostIp = listenedHostIp;
        this.conn = conn;
    }

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
}
