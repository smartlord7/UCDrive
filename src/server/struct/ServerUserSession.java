package server.struct;

import sync.SyncObj;
import util.FileMetadata;

public class ServerUserSession {
    private int userId;
    private String currentDir;
    private FileMetadata fileMetadata;
    private SyncObj syncObj;

    public ServerUserSession() {
        this.syncObj = new SyncObj();
    }

    public ServerUserSession(int userId, String lastSessionDir) {
        this.userId = userId;
        this.currentDir = lastSessionDir;
        this.syncObj = new SyncObj();
    }

    public synchronized int getUserId() {
        return userId;
    }

    public synchronized void setUserId(int userId) {
        this.userId = userId;
    }

    public synchronized String getCurrentDir() {
        return currentDir;
    }

    public synchronized void setCurrentDir(String currentDir) {
        this.currentDir = currentDir;
    }

    public synchronized FileMetadata getFileMetadata() {
        return fileMetadata;
    }

    public synchronized void setFileMetadata(FileMetadata fileMetadata) {
        this.fileMetadata = fileMetadata;
    }

    public synchronized SyncObj getSyncObj() {
        return syncObj;
    }

    public synchronized void setSyncObj(SyncObj syncObj) {
        this.syncObj = syncObj;
    }
}
