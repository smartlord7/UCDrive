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

import sync.SyncObj;
import util.FileMetadata;

/**
 * Class that the server user session methods.
 */
public class ServerUserSession {

    // region Private properties

    private int userId;
    private String currentDir;
    private FileMetadata fileMetadata;
    private SyncObj syncObj;

    // endregion Private properties

    // region Public methods

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

    // endregion Public methods

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
