package datalayer.model.FilePermission;

import datalayer.base.IDatabaseEntity;
import datalayer.enumerate.FilePermissionEnum;
import java.io.Serializable;

/**
 * Class that has the file permission methods.
 */

public class FilePermission implements IDatabaseEntity, Serializable {

    // region Private properties

    private int userId;
    private String directory;
    private FilePermissionEnum permission;

    // endregion Private properties

    // region Public methods

    /**
     * Constructor method.
     */
    public FilePermission() {
    }

    /**
     * Constructor method.
     * @param userId is the user id.
     * @param directory is the file directory.
     * @param permission are the permissions.
     */
    public FilePermission(int userId, String directory, FilePermissionEnum permission) {
        this.userId = userId;
        this.directory = directory;
        this.permission = permission;
    }

    // endregion Public methods

    // region Getters and Setters

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

    // endregion Getters and Setters

}
