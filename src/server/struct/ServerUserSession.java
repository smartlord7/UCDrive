package server.struct;

import sync.SyncObj;
import util.FileMetadata;

public class ServerUserSession {
    private int userId;
    private String currentDir;
    private FileMetadata fileMetadata;
    private SyncObj syncObj;

    /**
     * Constructor method.
     */
    public ServerUserSession() {
        this.syncObj = new SyncObj();
    }

    /**
     * Constructor method
     * @param userId is the user id.
     * @param lastSessionDir is the last session accessed directory.
     */
    public ServerUserSession(int userId, String lastSessionDir) {
        this.userId = userId;
        this.currentDir = lastSessionDir;
        this.syncObj = new SyncObj();
    }
    // region Getters and Setters
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
    // endregion Getters and Setters
}
