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

package datalayer.model.FilePermission;

import datalayer.base.IDatabaseEntity;
import datalayer.enumerate.FilePermissionEnum;
import java.io.Serializable;

/**
 * Class that holds the data about a file permission.
 */

public class FilePermission implements IDatabaseEntity, Serializable {

    // region Private properties

    private int userId;
    private String directory;
    private FilePermissionEnum permission;

    // endregion Private properties

    // region Constructors

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

    // endregion Constructors

    // region Public methods

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

    // endregion Public methods

}
