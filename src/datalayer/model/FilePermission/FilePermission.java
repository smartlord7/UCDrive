package datalayer.model.FilePermission;

import datalayer.enumerate.FilePermissionEnum;

public class FilePermission {
    private int userId;
    private String directory;
    private FilePermissionEnum permission;

    public FilePermission() {
    }

    public FilePermission(int userId, String directory, FilePermissionEnum permission) {
        this.userId = userId;
        this.directory = directory;
        this.permission = permission;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public FilePermissionEnum getPermission() {
        return permission;
    }

    public void setPermission(FilePermissionEnum permission) {
        this.permission = permission;
    }
}
