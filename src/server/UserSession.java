package server;

import util.FileMetadata;

public class UserSession {
    private int userId;
    private String currentDir;
    private FileMetadata fileMetadata;
    private String other;

    public UserSession() {
    }

    public UserSession(int userId, String lastSessionDir) {
        this.userId = userId;
        this.currentDir = lastSessionDir;
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

    public synchronized String getOther() {
        return other;
    }

    public synchronized void setOther(String other) {
        this.other = other;
    }
}
