package server;

import java.sql.Connection;

public class ServerConfig {
    private boolean isSecondary;
    private int commandPort;
    private int dataPort;
    private int watchedHostPort;
    private int maxFailedHeartbeat;
    private int heartbeatInterval;
    private int heartbeatTimeout;
    private String instance;
    private String database;
    private String user;
    private String password;
    private String watchedHostIp;
    private Connection conn;

    public ServerConfig() {
    }

    public ServerConfig(boolean isSecondary, int commandPort, int dataPort, int watchedHostPort, int maxFailedHeartbeat, int heartbeatInterval, int heartbeatTimeout, String instance, String database, String user, String password, String watchedHostIp, Connection conn) {
        this.isSecondary = isSecondary;
        this.commandPort = commandPort;
        this.dataPort = dataPort;
        this.watchedHostPort = watchedHostPort;
        this.maxFailedHeartbeat = maxFailedHeartbeat;
        this.heartbeatInterval = heartbeatInterval;
        this.heartbeatTimeout = heartbeatTimeout;
        this.instance = instance;
        this.database = database;
        this.user = user;
        this.password = password;
        this.watchedHostIp = watchedHostIp;
        this.conn = conn;
    }

    public ServerConfig(String[] args) {
        try {
            instance = args[0];
            database = args[1];
            user = args[2];
            password = args[3];
            commandPort = Integer.parseInt(args[4]);
            dataPort = Integer.parseInt(args[5]);

            if (args.length >= 8) {
                isSecondary = true;
                watchedHostIp = args[6];
                watchedHostPort = Integer.parseInt(args[7]);
                heartbeatInterval = Integer.parseInt(args[8]);
                maxFailedHeartbeat = Integer.parseInt(args[9]);
                heartbeatTimeout = Integer.parseInt(args[10]);
            } else {
                watchedHostPort = Integer.parseInt(args[6]);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Error: missing argument");
            System.exit(0);
        }
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

    public int getWatchedHostPort() {
        return watchedHostPort;
    }

    public void setWatchedHostPort(int watchedHostPort) {
        this.watchedHostPort = watchedHostPort;
    }

    public int getHeartbeatTimeout() {
        return heartbeatTimeout;
    }

    public void setHeartbeatTimeout(int heartbeatTimeout) {
        this.heartbeatTimeout = heartbeatTimeout;
    }

    public String getWatchedHostIp() {
        return watchedHostIp;
    }

    public void setWatchedHostIp(String watchedHostIp) {
        this.watchedHostIp = watchedHostIp;
    }

    public boolean isSecondary() {
        return isSecondary;
    }

    public void setSecondary(boolean secondary) {
        isSecondary = secondary;
    }
}
